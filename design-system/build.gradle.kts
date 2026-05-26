plugins {
    id("com.example.buildlogic.android-library")
    id("com.example.buildlogic.android-compose")
}

android {
    namespace = "com.example.designsystem"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.3.0-alpha03")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    testImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")
}
