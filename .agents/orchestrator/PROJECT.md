# Project: Distraction Blocker

## Architecture
- Custom accessibility service (`AppBlockerService`) listens to `TYPE_WINDOW_STATE_CHANGED` events to block apps.
- `BlockedAppsManager` persists state via `SharedPreferences`.
- Main UI contains tabs and layout for app configuration.

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| 1 | Exploration | Exploration and build verification | None | DONE |
| 2 | E2E Testing | Dual track: Design & build E2E test suite | None | IN_PROGRESS |
| 3 | UI/UX Upgrades | AGSL shaders, Onboarding flow, Insights Dashboard | M1 | IN_PROGRESS |
| 4 | Backend & Security | Bypass protection, profiles, recurring schedules | M3 | PLANNED |
| 5 | E2E & Polish | Pass all E2E tests, Adversarial coverage hardening | M2, M4 | PLANNED |

## Interface Contracts
- **Profiles Database & API**: Shared contract for saving/loading profiles in `BlockedAppsManager` or a repository.
- **Schedules API**: Contract for scheduling and checking recurring schedules.
- **Bypass Protection API**: Settings monitoring and redirect handling.

## Code Layout
- `app/src/main/java/com/antigravity/distractionshield/`: Core application package
  - `MainActivity.kt`: Entry point
  - `AppBlockerService.kt`: Interception service
  - `BlockedAppsManager.kt`: Storage/state logic
  - `BlockerActivity.kt`: Intercept/block UI overlay
  - `ui/main/MainScreen.kt`: Settings/insights dashboard
  - `ui/components/`: Core UI design system components
