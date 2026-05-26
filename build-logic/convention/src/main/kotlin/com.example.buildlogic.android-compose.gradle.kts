plugins.withId("com.android.application") {
    extensions.configure<com.android.build.api.dsl.ApplicationExtension>("android") {
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.4"
        }
    }
}

plugins.withId("com.android.library") {
    extensions.configure<com.android.build.api.dsl.LibraryExtension>("android") {
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.4"
        }
    }
}
