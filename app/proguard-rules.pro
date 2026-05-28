# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep DataStore
-keepclassmembers class * extends androidx.datastore.preferences.core.Preferences {
    <fields>;
}

# Keep Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
