# App Blocker

A production-ready Android app built with Kotlin, Jetpack Compose, and Material 3.

## Architecture

- **Language:** Kotlin 2.0
- **UI Framework:** Jetpack Compose with Material 3
- **Navigation:** Compose Navigation with type-safe routes
- **Minimum SDK:** 26 (Android 8.0)
- **Target SDK:** 34 (Android 14)
- **Build System:** Gradle with Kotlin DSL

## Features

- **Home:** Overview dashboard
- **Blocker:** Manage app blocking rules with toggles
- **Settings:** Configure strict mode, notifications, and security options
- **Dynamic Color:** Material You theming on Android 12+
- **Edge-to-Edge:** Full-screen immersive UI

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

## Getting Started

1. Open in Android Studio Hedgehog or later
2. Sync Gradle
3. Run on emulator or device

## Build

```bash
./gradlew assembleRelease
```
