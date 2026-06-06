> [!IMPORTANT]
> **Developer Sync Note**: To maintain continuity across multiple chat sessions, you MUST update this document after every feature release, bug fix, or commit checkpoint.

# Changelog

All notable changes to this project will be documented in this file.

## [0.6.0] - 2026-06-06

### Added
- Implemented a dynamic theme engine supporting System, Light, and Dark modes.
- Added Android 12+ (API 31+) Dynamic Color support (Material You) to match system wallpaper color palettes.
- Designed a custom-drawn Canvas settings gear button in the header section.
- Built a polished, glassmorphic settings dialog overlay (`ThemeSettingsDialog`) to toggle theme customization options.
- Introduced `ThemeConfig` CompositionLocal to customize refraction borders, card transparency, and background blobs dynamically based on the active theme.

### Fixed
- Resolved the critical visual bug forcing a white background base in dark mode.
- Corrected global text color variables to resolve dynamically from `MaterialTheme.colorScheme` instead of static color codes.
- Added `enableEdgeToEdge()` inside `BlockerActivity` to align status/navigation bar appearance.

## [0.5.0] - 2026-06-05

### Added
- Integrated custom typography by bundling the **Outfit** font family weights (Regular, Medium, SemiBold, Bold, ExtraBold) offline in project assets (`res/font`).
- Re-architected `AuroraBackground` to draw two overlapping, dynamically morphing irregular bezier path shapes filled with linear and radial 5-color gradients (Green, Yellow, Red, Pink, Magenta) on a pure white background base.
- Enhanced `GlassmorphicCard` to create a polished 3D liquid glass effect:
  - Added a soft 3D ambient drop shadow.
  - Added a double-gradient refractive border (highlighting top-left, shading bottom-right).
  - Added an inner white highlight border.
  - Added a repeating diagonal specular shine sweep animation.
- Released the updated and compiled signed debug APK to `release/app-debug.apk`.

### Changed
- Shifted application design system to a clean, minimalist light-theme base.
- Updated all text elements (app items, settings, headers) to deep charcoal-obsidian (`TextPrimary`) and slate gray (`TextSecondary`) for high readability.
- Overhauled `NeonToggle` to support light-theme tracks and a white shadow-casting toggle thumb.
- Fixed `BlockerActivity` compilation by adding missing `TextPrimary` import.

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

## [0.4.0] - 2026-06-03

### Changed
- Refactored package structure and Kotlin files from placeholder name `com.example.distractionblocker` to `com.antigravity.distractionshield`. This assigns a unique, custom applicationId to bypass automated signature collision blocks and placeholder restrictions during sideload installation.

### Added
- Integrated a premium custom glowing lock-shield app logo, replacing the default Android system vector with a high-resolution branded asset (`ic_launcher_foreground.png`).
- Set launcher background theme to match our solid brand color Obsidian Purple (`#090514`).
