> [!IMPORTANT]
> **Developer Sync Note**: To maintain continuity across multiple chat sessions, you MUST update this document after every feature release, bug fix, or commit checkpoint.

# Changelog

All notable changes to this project will be documented in this file.

## [0.1.0] - 2026-06-03

### Added
- Completed initial design architecture documentation:
  - [CONTINUATION.md](file:///c:/Users/hp1/Desktop/Distraction-bloc/CONTINUATION.md): Continuity guidelines.
  - [APP-DESIGN.md](file:///c:/Users/hp1/Desktop/Distraction-bloc/APP-DESIGN.md): Premium SwiftUI UI specs.
  - [APP-BACKEND.md](file:///c:/Users/hp1/Desktop/Distraction-bloc/APP-BACKEND.md): Interception and session management spec.
- Defined initial project roadmap and verification strategy.
- Verified Windows developer tools and installed `android` CLI.

## [0.2.0] - 2026-06-03

### Added
- Completed project implementation phase:
  - Created `BlockedAppsManager` for session and preference tracking.
  - Created `AppBlockerService` to intercept foreground app switches using Accessibility APIs.
  - Created custom canvas-based `AuroraBackground`, `GlassmorphicCard`, and mechanical `BounceButton`.
  - Built full-screen deep focus lock `BlockerActivity` with live countdown timer and launcher redirects.
  - Built `MainScreen` with neon slides, timer selection grids, and permission help overlays.
  - Resolved compiler warnings (safe null checking for packages) and Compose compiler context resolutions.
- Verified compilation and successfully ran build `.\gradlew.bat assembleDebug`.

## [0.3.0] - 2026-06-03

### Changed
- Replaced the highly flagged, sensitive `QUERY_ALL_PACKAGES` permission in `AndroidManifest.xml` with a targeted `<queries>` intent block matching launcher main activities, complying with Play Protect policies.

### Added
- Created an elegant, in-app prominent Accessibility Service disclosure popup inside `MainScreen` that ensures explicit consent and details how window tracking works before redirecting the user.
- Configured a dedicated project-specific keystore (`distraction_blocker_release.jks`) to sign the APK, replacing Gradle's default public debug credentials to establish a unique signature.
