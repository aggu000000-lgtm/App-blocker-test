import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.file.FileTree

abstract class CheckAlphaImportsTask : DefaultTask() {
    @get:InputFiles
    abstract var sourceFiles: FileTree

    @TaskAction
    fun checkImports() {
        var failed = false
        sourceFiles.forEach { file ->
            val lines = file.readLines()
            lines.forEachIndexed { index, line ->
                val trimmed = line.trim()
                if (trimmed.startsWith("import androidx.compose.material3.")) {
                    println("${file.absolutePath}:${index + 1}: Architectural Violation - Direct import of Material3 alpha component: ${trimmed}")
                    failed = true
                }
                if (trimmed.startsWith("import com.example.designsystem.components.")) {
                    val importedName = trimmed.substringAfterLast(".")
                    if (importedName != "*" && !importedName.startsWith("Catalog")) {
                        println("${file.absolutePath}:${index + 1}: Architectural Violation - Imported internal component does not have 'Catalog' prefix: ${trimmed}")
                        failed = true
                    }
                }
            }
        }
        if (failed) {
            throw GradleException("Architectural rule violations found. Please use stable Catalog components from the Design System.")
        }
    }
}

val checkAlphaImports by tasks.registering(CheckAlphaImportsTask::class) {
    sourceFiles = project.fileTree("src/main/java") {
        include("**/*.kt")
    }
}

tasks.named("check") {
    dependsOn(checkAlphaImports)
}
