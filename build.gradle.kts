plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets) apply false
    alias(libs.plugins.google.dagger.hilt.android) apply false
    alias(libs.plugins.jetbrains.dokka) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.paparazzi) apply false
    id("dev.testify") apply false
}

allprojects {
    apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)
    ktlint {
        version.set("1.7.1")
        additionalEditorconfig.set(
            mapOf(
                "ktlint_code_style" to "android_studio", // Use Android Studio code style defaults
                "ktlint_standard_class-signature" to "disabled", // Disable class signature wrapping enforcement
                "ktlint_standard_function-expression-body" to "disabled", // Function body should be replaced with body expression
                "ktlint_standard_function-signature" to "disabled", // Disable function signature wrapping enforcement
                "ktlint_standard_import-ordering" to "disabled", // Disable import ordering rule
                "ktlint_standard_no-unused-imports" to "enabled", // Enable unused-imports rule
                "ktlint_standard_property-naming" to "disabled", // Disable property naming convention checks
                "max_line_length" to "200", // Max line length before ktlint enforces wrapping
            )

        )
        filter {
            exclude {
                it.file.path.startsWith("${layout.buildDirectory.get()}/generated/")
            }
        }
    }
}
