import json
from pathlib import Path
from typing import Any

import firebase_admin
from firebase_admin import credentials as firebase_credentials
from firebase_admin import storage
from google.cloud import texttospeech
from google.oauth2 import service_account


PROJECT_ROOT = Path(__file__).resolve().parent
POEMS_JSON_PATH = PROJECT_ROOT / "app" / "src" / "main" / "res" / "raw" / "poems.json"
GCLOUD_KEY_PATH = PROJECT_ROOT / "gcloud_key.json"
FIREBASE_KEY_PATH = PROJECT_ROOT / "firebase_key.json"
OUTPUT_AUDIO_DIR = PROJECT_ROOT / "audio"
BUCKET_NAME = "kavyakanaja-922de.firebasestorage.app"
CHECK = "\u2713"
CROSS = "\u2717"


def load_poems():
    with open(POEMS_JSON_PATH, encoding="utf-8-sig") as file:
        return json.load(file)


def save_poems(poems: list[dict[str, Any]]) -> None:
    with POEMS_JSON_PATH.open("w", encoding="utf-8") as file:
        json.dump(poems, file, ensure_ascii=False, indent=2)
        file.write("\n")


def create_tts_client() -> texttospeech.TextToSpeechClient:
    credentials = service_account.Credentials.from_service_account_file(str(GCLOUD_KEY_PATH))
    return texttospeech.TextToSpeechClient(credentials=credentials)


def initialize_firebase() -> None:
    if firebase_admin._apps:
        return
    cred = firebase_credentials.Certificate(str(FIREBASE_KEY_PATH))
    firebase_admin.initialize_app(cred, {"storageBucket": BUCKET_NAME})


def firebase_download_url(object_path: str) -> str:
    encoded_path = object_path.replace("/", "%2F")
    return f"https://firebasestorage.googleapis.com/v0/b/{BUCKET_NAME}/o/{encoded_path}?alt=media"


def generate_poem_audio(
    tts_client: texttospeech.TextToSpeechClient,
    verse: str,
    output_path: Path,
) -> None:
    synthesis_input = texttospeech.SynthesisInput(text=verse)
    voice = texttospeech.VoiceSelectionParams(
        language_code="kn-IN",
        name="kn-IN-Wavenet-A",
    )
    audio_config = texttospeech.AudioConfig(
        audio_encoding=texttospeech.AudioEncoding.MP3,
        speaking_rate=0.85,
    )
    response = tts_client.synthesize_speech(
        input=synthesis_input,
        voice=voice,
        audio_config=audio_config,
    )
    output_path.write_bytes(response.audio_content)


def upload_audio(output_path: Path, object_path: str) -> str:
    bucket = storage.bucket()
    blob = bucket.blob(object_path)
    blob.upload_from_filename(str(output_path), content_type="audio/mpeg")
    blob.make_public()
    return firebase_download_url(object_path)


def main() -> None:
    poems = load_poems()
    OUTPUT_AUDIO_DIR.mkdir(parents=True, exist_ok=True)
    tts_client = create_tts_client()
    initialize_firebase()

    total = len(poems)
    successful = 0

    for index, poem in enumerate(poems, start=1):
        poem_id = int(poem.get("id", index))
        title = poem.get("titleKannada") or poem.get("title") or f"Poem {poem_id}"
        verse = poem.get("verse", "")
        file_name = f"poem_{poem_id:03d}.mp3"
        output_path = OUTPUT_AUDIO_DIR / file_name
        object_path = f"audio/{file_name}"

        print(f"Processing poem {index}/{total}: {title}")

        try:
            if not verse.strip():
                raise ValueError("Missing verse field.")

            generate_poem_audio(tts_client, verse, output_path)
            print(f"{CHECK} Generated audio/{file_name}")

            url = upload_audio(output_path, object_path)
            print(f"{CHECK} Uploaded to Firebase Storage")

            poem["audioUrl"] = url
            save_poems(poems)
            print(f"{CHECK} Updated audioUrl in poems.json")
            successful += 1
        except Exception as exc:
            print(f"{CROSS} Error processing poem {poem_id}: {exc}")

        print("---")

    print("First 3 updated audioUrls:")
    for poem in poems[:3]:
        print(poem.get("audioUrl", ""))

    if successful != total:
        print(f"{successful}/{total} poems completed successfully.")
    print(f"ALL DONE! {total} poems processed.")
    print("Now run the app and trigger re-seed to update Firestore.")


if __name__ == "__main__":
    main()
