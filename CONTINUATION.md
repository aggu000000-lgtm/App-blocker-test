> [!IMPORTANT]
> **Developer Sync Note**: To maintain continuity across multiple chat sessions, you MUST update this document after every feature change, configuration modification, or design iteration. Keep documentation in sync at all times.

# Multi-Session Continuity Guidelines

This file maintains the development state of the **Distraction Blocker** app across different chat sessions or system restarts. If you are starting a new session, read this file to understand the current progress and continue from where the previous agent left off.

## Current State & Context
- **Project Goal**: A SwiftUI-level premium, minimalist app blocker built using Jetpack Compose on Android.
- **Key Mechanics**:
  - `AppBlockerForegroundService` utilizing the `UsageStatsManager` API for background app polling and redirection (500ms ticks).
  - Strict block session timer where user cannot bypass the lock.
  - Expiry prompt: Option to extend or release.
  - `BootReceiver` to restart the monitoring service on device reboot if a session is currently active.
  - **Premium Visual Overhaul & Theme Engine (Completed)**:
    - Pure white background (light mode) and deep obsidian black background (dark mode) with organic, morphing irregular gradient shapes using 5 colors.
    - Custom typography using the offline-bundled **Outfit** font family (loaded directly from resources).
    - Polished 3D liquid glass cards with a diagonal animated specular shine sweep, double-gradient refraction borders, and ambient drop shadows.
    - Full-scale dynamic theme engine supporting System, Light, and Dark modes, plus Android 12+ wallpaper dynamic colors (Material You). Persisted reactively using `OnSharedPreferenceChangeListener`.
  - **Play Protect Compliance**: Replaced `QUERY_ALL_PACKAGES` permission with targeted `<queries>` block; added a prominent in-app disclosure dialog; signed both builds using a custom keystore `distraction_blocker_release.jks`.
  - **Robust System Blocking & Event Tracking (Completed)**:
    - Overhauled `AppBlockerForegroundService` with an event-driven `UsageStatsManager` log processor.
    - Captures foreground app switches with startup seeding to prevent duplicate tracking or omissions.
    - Calculates active focus session durations and daily stats (stored in `SharedPreferences` via `StatsRepository`).
    - Detects rapid app-switching behavior (switches between different apps within a 10s threshold, excluding launcher/blocker app) to measure distraction metrics.
- **Environment**:
  - OS: Windows
  - IDE/Build: Android CLI (`C:\Users\hp1\AppData\AndroidCLI\android.exe`)
  - Target SDK: API 33+ (needed for native glass and runtime shaders), Min SDK: API 24 (Android 7.0).
  - ANDROID_HOME: `C:\Users\hp1\AppData\Local\Android\Sdk`
  - Latest Release APK: Located at [release/app-release.apk](file:///C:/Users/hp1/Desktop/Distraction-bloc/release/app-release.apk) and Debug APK at [release/app-debug.apk](file:///C:/Users/hp1/Desktop/Distraction-bloc/release/app-debug.apk).

## Next Steps
1. **Explore Visual & Dashboard Upgrades**:
   - Implement custom AGSL fragment shaders (API 33+) for real-time lens distortion, chromatic aberration, and noise grain overlays.
   - Build a swipeable Cinematic Onboarding tour for permissions and user setup.
   - Create a Focus Insights dashboard tab (using `StatsRepository` to render the canvas-drawn charts).
2. **Security & Logic Enhancements**:
   - Implement Blocker Bypass Protection to prevent force-stops or disabling usage stats permissions during active lock sessions.
   - Design App Grouping/Profiles.
   - Build Recurring Block Schedules.

## Resuming Execution
When entering a new session, run:
```powershell
C:\Users\hp1\AppData\AndroidCLI\android.exe info
```
to verify path setup, inspect `CHANGELOG.md` to see the last written files, and read `task.md` to see the current TODO list.
