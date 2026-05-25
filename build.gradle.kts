plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
}

val checkAlphaImports by tasks.registering {
    doLast {
        val featureDir = file("feature/src/main/java")
        if (featureDir.exists()) {
            featureDir.walkTopDown().filter { it.extension == "kt" }.forEach { file ->
                file.readLines().forEachIndexed { index, line ->
                    if (line.contains("import androidx.compose.material3.pulltorefresh") ||
                        line.contains("import androidx.compose.material3.FloatingAppBar") ||
                        line.contains("import androidx.compose.material3.Carousel")) {
                        throw GradleException("Build-time enforcement failed: Direct import of alpha-track Expressive UI libraries found in feature module ${file.name}:${index + 1}")
                    }
                    if (line.contains("import androidx.compose.material3.ExperimentalMaterial3Api")) {
                        throw GradleException("Build-time enforcement failed: Direct import of ExperimentalMaterial3Api found in feature module ${file.name}:${index + 1}")
                    }
                }
            }
        }
    }
}

// Hook to check task
subprojects {
    afterEvaluate {
        tasks.findByName("check")?.dependsOn(checkAlphaImports)
        tasks.findByName("preBuild")?.dependsOn(checkAlphaImports)
    }
}
