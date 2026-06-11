# Quizland

A Quizlet-style flashcard learning app for **SDG 4 – Quality Education**, built with Kotlin and Jetpack Compose (Material 3). Students create their own study sets, discover quizzes from the web, share sets to a community cloud, and import a friend's set by scanning a QR code.

## Student Information

| | |
|---|---|
| **Name** | YUAN YIWEI |
| **Matric No** | A207421 |
| **Course** | TM2213 Mobile Application Programming |
| **Instructor** | Cikgu Izwan |
| **SDG** | SDG 4 – Quality Education |

## SDG Theme

**SDG 4 – Quality Education.** Many students struggle to keep study notes in one place and revise efficiently. Quizland lets users build flashcard sets, pull in live quiz content, and share decks with classmates — supporting self-directed learning and equal access to study tools.

## Features

The app implements four technical pillars across **9 screens** (Home, Discover, Create, Library, Community, Profile, Premium, Set Detail, Scan):

| Pillar | Where | What it does |
|---|---|---|
| **Local Persistence** (Room) | Library, Create, Profile | Flashcard sets, card contents and the user profile are stored on-device and stay available offline |
| **Web API** (Retrofit) | Discover | Fetches live quiz categories and questions from the [Open Trivia DB](https://opentdb.com) REST API; one tap saves them as a set |
| **Cloud** (Firebase Firestore) | Community, Set Detail | Share a set to the community cloud and browse / import sets shared by others |
| **Sensor** (Camera) | Scan | Scans a Quizland QR code with CameraX + ML Kit, then fetches that set from the cloud into the local library |

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3 + Navigation Compose
- **Local DB:** Room
- **Networking:** Retrofit + Gson
- **Cloud:** Firebase Firestore
- **Sensor:** CameraX + ML Kit Barcode Scanning (QR generation via ZXing)
- **Min SDK:** 24 (Android 7.0) · **Target SDK:** 36

## Setup & Run

> ⚠️ **Developed & compiled on Linux.** On Windows, the first open may show a path / "SDK location not found" error, because `local.properties` is machine-specific (git-ignored) and still holds a Linux SDK path. Fix: when Android Studio prompts, set your local SDK location (it regenerates `local.properties`), then **Sync Gradle** — it builds normally after that.

1. Clone the repository and open it in **Android Studio**.
2. **Firebase:** `app/google-services.json` is included. To use your own project, create a Firebase project, add an Android app with package `com.alanwine.quizland`, enable **Firestore Database** (test mode), and drop the downloaded `google-services.json` into `app/`.
3. **Sync Gradle**, then **Run** on an emulator or physical device (API 24+).
4. A physical device is recommended so the camera (QR scanning) works.
