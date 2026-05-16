#!/usr/bin/env python3
"""Generate Kannada MP3 narration for every poem using gTTS and place the
files under `app/src/main/assets/audio/poem_XXX.mp3` so the app can ship
offline audio without relying on Firebase Storage.

Usage:
    python3 -m venv .venv && .venv/bin/pip install gTTS
    .venv/bin/python tools/gen_audio.py

Re-runs are idempotent: existing non-empty files are skipped. Delete the
matching MP3 to force regeneration.
"""
import json
import os
import sys
import time
from gtts import gTTS

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "app/src/main/res/raw/poems.json")
DST = os.path.join(ROOT, "app/src/main/assets/audio")
os.makedirs(DST, exist_ok=True)

with open(SRC, encoding="utf-8-sig") as f:
    poems = json.load(f)

print(f"Generating audio for {len(poems)} poems -> {DST}")
ok = 0
for p in poems:
    pid = int(p["id"])
    verse = (p.get("verse") or "").strip() or (p.get("transliteration") or "").strip()
    name = f"poem_{pid:03d}.mp3"
    out = os.path.join(DST, name)
    if os.path.exists(out) and os.path.getsize(out) > 1000:
        print(f"  skip {name}")
        ok += 1
        continue
    try:
        gTTS(text=verse, lang="kn", slow=False).save(out)
        print(f"  wrote {name}  ({os.path.getsize(out)} bytes)")
        ok += 1
        time.sleep(0.2)
    except Exception as e:
        print(f"  FAIL {name}: {e}", file=sys.stderr)

print(f"Done: {ok}/{len(poems)} generated")
