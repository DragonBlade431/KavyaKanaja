import json
from pathlib import Path
from typing import Any

import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore


PROJECT_ROOT = Path(__file__).resolve().parent
POEMS_JSON_PATH = PROJECT_ROOT / "app" / "src" / "main" / "res" / "raw" / "poems.json"
FIREBASE_KEY_PATH = PROJECT_ROOT / "firebase_key.json"
BUCKET_NAME = "kavyakanaja.appspot.com"
COLLECTION_NAME = "poems"
BATCH_LIMIT = 450


def load_poems():
    with open(POEMS_JSON_PATH, encoding="utf-8-sig") as file:
        return json.load(file)


def initialize_firebase() -> None:
    if firebase_admin._apps:
        return
    cred = credentials.Certificate(str(FIREBASE_KEY_PATH))
    firebase_admin.initialize_app(cred, {"storageBucket": BUCKET_NAME})


def delete_collection(db: firestore.Client, collection_name: str) -> int:
    deleted = 0
    collection_ref = db.collection(collection_name)

    while True:
        docs = list(collection_ref.limit(BATCH_LIMIT).stream())
        if not docs:
            break

        batch = db.batch()
        for doc in docs:
            batch.delete(doc.reference)
        batch.commit()

        deleted += len(docs)
        print(f"Deleted {deleted} existing poem documents...")

    return deleted


def upload_poems(db: firestore.Client, poems: list[dict[str, Any]]) -> None:
    collection_ref = db.collection(COLLECTION_NAME)
    total = len(poems)

    for start in range(0, total, BATCH_LIMIT):
        batch = db.batch()
        chunk = poems[start : start + BATCH_LIMIT]

        for poem in chunk:
            poem_id = int(poem["id"])
            doc_ref = collection_ref.document(str(poem_id))
            batch.set(doc_ref, poem)

        batch.commit()

        uploaded = min(start + len(chunk), total)
        print(f"Uploaded {uploaded}/{total} poems to Firestore...")


def main() -> None:
    poems = load_poems()
    initialize_firebase()
    db = firestore.client()

    print(f"Loaded {len(poems)} poems from {POEMS_JSON_PATH}")
    print(f"Deleting existing documents in '{COLLECTION_NAME}' collection...")
    deleted = delete_collection(db, COLLECTION_NAME)
    print(f"Deleted {deleted} existing documents.")

    print(f"Re-uploading {len(poems)} poems to Firestore...")
    upload_poems(db, poems)

    print(f"ALL DONE! {len(poems)} poems uploaded to Firestore.")


if __name__ == "__main__":
    main()
