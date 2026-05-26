# App-blocker-test
The app built by Jules

## Design System Abstraction

This project includes a fully abstracted **Design System** to handle volatile Google Material 3 "Expressive" guidelines (currently alpha-track). 

### Architecture
1. **`design-system` module:** Encapsulates all volatile dependencies (like `androidx.compose.material3` alpha versions). Exposes stable Compose components mapped to Expressive guidelines.
2. **`feature` module:** Consumes the Design System without any direct knowledge of the underlying alpha libraries.
3. **Build-time enforcement:** A custom Gradle task `checkAlphaImports` blocks developers from importing alpha libraries directly in feature modules.

See `design-system/MIGRATION_GUIDE.md` for information on upgrading alpha libraries without breaking consumer code.

## Automated Pipeline-Driven Delivery

This project has transitioned from manual local builds to automated pipeline-driven delivery.

### Continuous Integration (CI)
Every pull request and push to the `main` branch triggers the CI pipeline (`.github/workflows/ci.yml`). This pipeline automatically builds the project and runs the `checkAlphaImports` verification task. If a developer violates the architectural boundaries (e.g., importing a private alpha component instead of a stable `Catalog` component), the pipeline will fail, providing human-readable feedback to prevent structural decay.

### Release Management
To generate a production-ready, signed binary, you no longer need to execute Gradle commands locally or manage sensitive keys on your machine. Instead, trigger the **Release Pipeline** (`.github/workflows/release.yml`) via GitHub Actions. 
- The system will automatically apply signing configurations using repository secrets.
- It will run optimizations (R8/ProGuard) for production-grade distribution.
- The workflow generates a distributable, signed APK artifact that can be downloaded directly from the GitHub Actions run.
