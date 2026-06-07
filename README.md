> [!IMPORTANT]
> **Developer Sync Note**: To maintain continuity across multiple chat sessions, you MUST update this document after every major architectural shift, scope change, or design approval.

# Distraction Blocker - SwiftUI-Level Android App Blocker

### 📲 [Download Latest APK (app-debug.apk)](https://github.com/aggu000000-lgtm/App-blocker-test/raw/main/release/app-debug.apk)
*Install directly on your Android phone to test the Deep Focus experience.*

A premium, minimalist app blocker for Android built using **Jetpack Compose** (Material 3) and Kotlin. It features a stunning **Liquid Glass** UI and strict **Session-Based Timer Locking** to help you stay focused.

## Key Features
- **SwiftUI-Level Aesthetics**: Vibrant, hardware-accelerated animated gradient background (Aurora effect) with high-radius blur overlays.
- **Liquid Glass Materials**: Frosted-glass containers, thin refraction borders, and tactile spring physics on all touch targets.
- **Strict Lockouts**: No-bypass full-screen interception activity covering blocked applications during active focus sessions.
- **Expiry Prompt**: Prompt to immediately extend deep focus when a session terminates, preventing instant relapse.
- **Real-Time Detection**: Background app monitoring using `UsageStatsManager` API via `AppBlockerForegroundService` (runs with 500ms polling interval).

## Documentation Map
- [CONTINUATION.md](file:///c:/Users/hp1/Desktop/Distraction-bloc/CONTINUATION.md): Multi-session agent handoff, target variables, and next coding tasks.
- [APP-DESIGN.md](file:///c:/Users/hp1/Desktop/Distraction-bloc/APP-DESIGN.md): Detailed color tokens, animation physics formulas, and glass math.
- [APP-BACKEND.md](file:///c:/Users/hp1/Desktop/Distraction-bloc/APP-BACKEND.md): Interception workflow diagram, persistent state configurations, and security permissions structure.
- [CHANGELOG.md](file:///c:/Users/hp1/Desktop/Distraction-bloc/CHANGELOG.md): History of features, updates, and releases.

## Prerequisites
- **Android SDK (API 33+)**: Required for AGSL shaders and native hardware blurs.
- **Android CLI**: For project compilation and emulator interaction.
  - CLI location: `C:\Users\hp1\AppData\AndroidCLI\android.exe`
  - Target emulator command: `android emulator list` or deploy using `android run`.
