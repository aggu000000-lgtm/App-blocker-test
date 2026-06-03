> [!IMPORTANT]
> **Developer Sync Note**: To maintain continuity across multiple chat sessions, you MUST update this document after every feature change, configuration modification, or design iteration. Keep documentation in sync at all times.

# Multi-Session Continuity Guidelines

This file maintains the development state of the **Distraction Blocker** app across different chat sessions or system restarts. If you are starting a new session, read this file to understand the current progress and continue from where the previous agent left off.

## Current State & Context
- **Project Goal**: A SwiftUI-level premium, minimalist app blocker built using Jetpack Compose on Android.
- **Key Mechanics**:
  - `AccessibilityService` for instantaneous window interception.
  - Strict block session timer where user cannot bypass the lock.
  - Expiry prompt: Option to extend or release.
  - UI styled with animated colorful fluid gradients (aurora canvas) and frosted liquid glass.
- **Environment**:
  - OS: Windows
  - IDE/Build: Android CLI (`C:\Users\hp1\AppData\AndroidCLI\android.exe`)
  - Target SDK: API 33+ (needed for native glass and runtime shaders), Min SDK: API 26 (Android 8.0).
  - ANDROID_HOME: `C:\Users\hp1\AppData\Local\Android\Sdk`

## Next Steps
1. **Initialize Project**: Generate the template using the `empty-activity` command.
2. **Apply Configurations**: Edit `AndroidManifest.xml` and gradle configurations.
3. **Core Coding**:
   - Persist blocked list & timers in `BlockedAppsManager`.
   - Build background interceptor service (`AppBlockerService`).
   - Draw canvas-animated custom `AuroraBackground`.
   - Build `GlassmorphicCard` & `BounceButton`.
   - Implement settings search UI in `MainActivity`.
   - Build lock interface in `BlockerActivity`.

## Resuming Execution
When entering a new session, run:
```powershell
C:\Users\hp1\AppData\AndroidCLI\android.exe info
```
to verify path setup, inspect `CHANGELOG.md` to see the last written files, and read `task.md` to see the current TODO list.
