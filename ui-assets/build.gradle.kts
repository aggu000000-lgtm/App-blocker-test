plugins {
    id("com.example.buildlogic.android-library")
    id("com.example.buildlogic.android-compose")
}

android {
    namespace = "com.example.uiassets"
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.compose.ui:ui:1.7.0-beta01")
    implementation("androidx.compose.material3:material3:1.3.0-alpha03")
}
