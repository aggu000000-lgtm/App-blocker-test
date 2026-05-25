# App-blocker-test
The app built by Jules

## Design System Abstraction

This project includes a fully abstracted **Design System** to handle volatile Google Material 3 "Expressive" guidelines (currently alpha-track). 

### Architecture
1. **`design-system` module:** Encapsulates all volatile dependencies (like `androidx.compose.material3` alpha versions). Exposes stable Compose components mapped to Expressive guidelines.
2. **`feature` module:** Consumes the Design System without any direct knowledge of the underlying alpha libraries.
3. **Build-time enforcement:** A custom Gradle task `checkAlphaImports` blocks developers from importing alpha libraries directly in feature modules.

See `design-system/MIGRATION_GUIDE.md` for information on upgrading alpha libraries without breaking consumer code.
