## 2026-06-05T09:54:27Z

An Android App Blocker with premium minimalist Liquid Glass design, session-based locking, and robust bypass prevention features, matching the polish and capability of native Google applications.

Working directory: c:/Users/hp1/Desktop/Distraction-bloc
Integrity mode: development

## Requirements

### R1. UI/UX Upgrades (Premium Perfection)
- **AGSL Shaders**: Implement custom AGSL fragment shaders (API 33+) to add real-time lens distortion, chromatic aberration, and noise grain overlays to the Liquid Glass containers.
- **Cinematic Onboarding**: Create a swipeable visual onboarding tour guiding users through required permissions (Accessibility, Draw Over Other Apps) using custom spring transitions and animated device illustrations.
- **Focus Insights Dashboard**: Add a glassmorphic tab displaying custom Canvas-drawn charts for weekly focus duration, apps blocked counts, and distraction scores.

### R2. Backend & Security Upgrades
- **Blocker Bypass Protection**: Update `AppBlockerService` to monitor system settings navigation. If the user attempts to force-stop the app or disable the accessibility permission during an active blocking session, collapse the settings window (trigger Home) or redirect them to prevent bypassing.
- **Profiles & Grouping**: Allow sorting user-installed launcher apps into distinct profiles (e.g., "Work", "Social", "Entertainment") with independent lockout durations and target package lists.
- **Recurring Schedules**: Enable scheduling automatic, recurring blocking windows (e.g., block "Entertainment" on weekdays 9:00 AM - 5:00 PM).

### R3. Quality & Polish
- Ensure the application builds cleanly using `./gradlew assembleDebug` or the provided Android CLI utility.
- Maintain a premium, high-frame-rate responsive design with Outfit typography and liquid glass shaders.
- Ensure the blocker works seamlessly without memory leaks or service crashes.

## Acceptance Criteria

### Build & Execution
- [ ] The application compiles successfully using the command `C:\Users\hp1\AppData\AndroidCLI\android.exe build` or `./gradlew assembleDebug`.
- [ ] No compilation errors or resource loading errors occur.

### UI & UX Polish
- [ ] Liquid glass containers feature animated AGSL shaders (lens distortion/chromatic aberration/noise grain) on API 33+.
- [ ] Onboarding flow correctly lists permissions and uses spring animations.
- [ ] Focus Insights tab successfully shows weekly usage statistics using custom Compose Canvas drawing.

### Security & Functional Completeness
- [ ] Bypass protection prevents the user from disabling the accessibility service or force-stopping the app during an active lock session by taking appropriate redirection actions.
- [ ] Users can create, edit, and select app profiles.
- [ ] Recurring schedules trigger blocking sessions automatically at scheduled times.
