// Top-level build file where you can add configuration options common to all sub-projects/modules.
import java.io.File

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// Keep Gradle outputs out of the OneDrive-synced project folder to avoid file-lock issues.
val localBuildRoot = File(System.getProperty("java.io.tmpdir"), "dreamiaselite-build")
rootProject.buildDir = File(localBuildRoot, "root")
subprojects {
    buildDir = File(localBuildRoot, name)
}
