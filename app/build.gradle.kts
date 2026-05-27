plugins {
    id("com.example.buildlogic.android-application")
    id("com.example.buildlogic.android-compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.sharma.focusblocker"

    defaultConfig {
        applicationId = "com.sharma.focusblocker"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystoreFile = file("release-keystore.jks")
            if (keystoreFile.exists() || System.getenv("CI") == "true") {
                storeFile = file(System.getenv("KEYSTORE_FILE") ?: "release-keystore.jks")
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val keystoreFile = file("release-keystore.jks")
            if (keystoreFile.exists() || System.getenv("CI") == "true") {
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = signingConfigs.getByName("debug")
            }
        }
    }
}

dependencies {
    implementation(project(":feature"))
    implementation(project(":design-system"))
    implementation(project(":ui-assets"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    
    // Compose
    implementation("androidx.compose.ui:ui:1.7.0-beta01")
    implementation("androidx.compose.ui:ui-graphics:1.7.0-beta01")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.0-beta01")
    implementation("androidx.compose.material3:material3:1.3.0-alpha03")
    
    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.0-beta01")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    ksp("com.google.dagger:hilt-compiler:2.48.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
}
