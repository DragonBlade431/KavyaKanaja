# ಕಾವ್ಯ-ಕಣಜ — Kavya-Kanaja

> "Duolingo for Kannada Literature" — A Literary Revival Android App

[![Android](https://img.shields.io/badge/Platform-Android-green)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue)](https://kotlinlang.org)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-orange)](https://firebase.google.com)

## Problem Statement
Karnataka has a rich literary history spanning 2,500+ years, but Gen-Z finds it difficult to access classical Kannada poems or understand Old Kannada meanings. Literary pride is fading. Kavya-Kanaja solves this by making Kannada poetry cool, accessible, and interactive.

## Features
- 📖 **Poem of the Day** — Changes automatically every day using date-based algorithm
- 💬 **Word Meanings Popup** — Tap any difficult Kannada word to see its meaning instantly
- 🎵 **Audio Recitation** — Stream poem recitations via ExoPlayer from Firebase Storage
- 📚 **Bhavartha** — Philosophical explanation in both Kannada and English
- 🎭 **Poet's Corner** — Biographies of 10 poets including all Jnanpith awardees
- 📚 **Library** — Browse all 50 poems with search and tag filters
- ❤️ **Favourites** — Save poems with heart toggle, persisted locally
- 🌐 **Bilingual UI** — Every label in Kannada + English throughout

## Tech Stack
| Layer | Technology |
|---|---|
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM + StateFlow |
| Cloud Database | Firebase Firestore |
| Local Cache | Room Database |
| Audio | ExoPlayer / Media3 |
| Storage | Firebase Storage |
| Preferences | DataStore |
| Navigation | Navigation Compose |
| Font | Noto Serif Kannada |

## Project Structure
```
app/src/main/java/com/kavyakanaja/app/
├── ui/
│   ├── screens/          # HomeScreen, LibraryScreen, PoemDetailScreen, etc.
│   ├── components/       # AudioPlayer, WordMeaningSheet, BhavarthaCard, etc.
│   └── theme/            # Color, Type, Theme
├── viewmodel/            # PoemViewModel, PoetViewModel
├── data/
│   ├── model/            # Poem, Poet, WordMeaning
│   ├── local/            # Room Database, DAOs
│   ├── remote/           # FirestoreService
│   └── repository/       # PoemRepository, PoetRepository
└── utils/                # JsonLoader, NetworkUtils, DateUtils
```

## Setup Instructions
1. Clone the repo: `git clone https://github.com/DragonBlade431/KavyaKanaja.git`
2. Open in Android Studio (Hedgehog or newer)
3. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
4. Add Android app with package `com.kavyakanaja.app`
5. Download `google-services.json` and place in `app/` folder
6. Enable Firestore and Storage in Firebase console
7. Download `NotoSerifKannada-Regular.ttf` from Google Fonts → place in `app/src/main/assets/fonts/`
8. Build and run: `./gradlew assembleDebug`

## Download APK
[⬇️ Download latest release APK](https://github.com/DragonBlade431/KavyaKanaja/releases)

## Screenshots
*(Add screenshots here)*

## Impact Goals
- **Cultural Renaissance** — Reconnecting Karnataka's youth with 2,500 years of literary heritage
- **Educational Enrichment** — Supporting students with Kannada literature curriculum
- **Soft Power** — Preserving one of the world's oldest living languages
