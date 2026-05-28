# App Blocker

A production-ready Android app built with Kotlin, Jetpack Compose, and Material 3.

## CI/CD — Zero-Touch APK Builds

**No local setup required.** Every push to `main` (or any PR) automatically builds and uploads the APK.

### How to get the APK

| Trigger | Where to find APK |
|---------|-------------------|
| **Push to `main`** | Go to **Releases → [Latest](https://github.com/aggu000000-lgtm/App-blocker-test/releases/tag/latest)** |
| **Any PR or push** | Go to **Actions** → click the run → scroll to **Artifacts** → download `app-release-apk` |
| **Push a tag `v*.*.*`** | Go to **Releases** → find the new version → download APK |

### Quick start for developers

1. Clone the repo
2. Open in Android Studio (Hedgehog or newer)
3. Sync Gradle and run

No manual signing setup needed — the CI uses the debug keystore for automated builds. For Play Store publishing, add your own `release` signing config in `app/build.gradle.kts`.

## Architecture

- **Language:** Kotlin 2.0
- **UI Framework:** Jetpack Compose with Material 3
- **Navigation:** Compose Navigation with type-safe routes
- **Minimum SDK:** 26 (Android 8.0)
- **Target SDK:** 34 (Android 14)
- **Build System:** Gradle with Kotlin DSL

## Project Structure

```
app/
├── src/main/java/com/example/appblocker/
│   ├── MainActivity.kt
│   ├── AppBlockerApp.kt          // Root navigation + bottom bar
│   ├── AppBlockerApplication.kt
│   └── ui/
│       ├── home/HomeScreen.kt
│       ├── blocker/BlockerScreen.kt
│       ├── settings/SettingsScreen.kt
│       └── theme/
│           ├── Color.kt
│           ├── Theme.kt
│           └── Type.kt
└── build.gradle.kts
```

## Build locally

```bash
./gradlew assembleRelease
```

APK will be at `app/build/outputs/apk/release/`.
