# Design System Abstraction Migration Guide

This document explains how the internal Design System maps volatile Google Material 3 "Expressive" libraries (e.g. `1.3.0-alphaXX`) into stable internal APIs, and how platform engineers should maintain this layer during library upgrades.

## Objective
The primary goal is zero build failures in feature modules when the underlying Material 3 alpha track library introduces breaking changes. Feature developers interact only with the `Catalog*` components, ensuring they are insulated from any volatile APIs.

## How to Update the Underlying Material 3 Library

1. **Update Dependency**
   In `design-system/build.gradle.kts`, bump the version of `androidx.compose.material3:material3` to the new alpha/beta version.

2. **Re-compile the Design System**
   Run `./gradlew :design-system:assembleDebug`. Since feature modules depend on the design system via standard `implementation`, they are not exposed to the new transient APIs.

3. **Resolve Breaking Changes in Abstractions**
   If a component's signature changed in the Google library (e.g., `PullToRefreshContainer` renamed to `PullToRefreshBox`), you will see a build error in the `design-system` module. Update the internal implementation of `CatalogPullToRefresh` to match the new API.
   - **DO NOT** change the public signature of `CatalogPullToRefresh`.
   - The feature modules must remain unaware of the underlying migration.

4. **Run Regression Tests**
   Run `./gradlew check` to ensure all unit and visual regression tests pass for the updated abstractions.

## Migrating to Stable APIs
When a Material 3 Expressive component graduates from the alpha track to the stable track:
1. Update the dependency in `design-system/build.gradle.kts` to the new stable release.
2. If necessary, refactor the `Catalog*` component to wrap the stable API.
3. Update the `@OptIn(ExperimentalMaterial3Api::class)` annotations in the design system module where they are no longer required.
4. Notify feature teams that the catalog components are now backed by stable implementations (no action required on their end).

## Feature Module Guidelines
- **Strict Isolation:** Feature modules must NEVER declare a direct dependency on `androidx.compose.material3` alpha-track versions. 
- Use the Gradle verification task `:checkAlphaImports` (or Detekt checks) to automatically fail the build if an alpha component is imported directly.
