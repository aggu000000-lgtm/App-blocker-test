# Orchestrator Project Plan

## Steps and Verification

### Step 1: Codebase Exploration and Build Verification
- **Objective**: Explore the codebase, map classes and assets, and verify that the app currently builds cleanly.
- **Agent**: `teamwork_preview_explorer`
- **Verification**: Explorer runs a build command via its worker or provides evidence of a successful build using the Android CLI tool.

### Step 2: E2E Testing Track (Parallel)
- **Objective**: Design and build a comprehensive E2E test suite covering feature coverage, boundary conditions, cross-feature interaction, and real-world workloads.
- **Agent**: `self` as E2E Testing Orchestrator
- **Output**: `TEST_READY.md` containing test definitions and execution scripts.
- **Verification**: Test execution script runs successfully and reports zero failures.

### Step 3: Implementation Track (Parallel)
- **Milestone 1: UI/UX Premium Upgrades**
  - AGSL Fragment Shaders (Lens distortion, chromatic aberration, grain overlay on API 33+).
  - Cinematic Swipeable Onboarding (Spring animations, permission indicators).
  - Focus Insights Dashboard (Canvas-drawn weekly charts, blockage scores).
  - **Agent**: Sub-orchestrator
- **Milestone 2: Backend & Security Upgrades**
  - Blocker Bypass Protection (Monitoring accessibility service and settings force-stop triggers, HOME redirect).
  - App Profiles & Grouping (Independent lockout duration and lists, UI sorting).
  - Recurring Schedules (Cron-like scheduler to block apps automatically).
  - **Agent**: Sub-orchestrator
- **Milestone 3: Final E2E Pass and Adversarial Hardening**
  - Phase 1: Pass 100% of E2E tests (Tiers 1-4).
  - Phase 2: Adversarial coverage hardening (Challenger-driven white-box testing).
  - **Agent**: Sub-orchestrator
