rootProject.name = "Testify IntelliJ Plugin"

fun includeModule(module: String, path: String? = null) {
    val moduleName = ":$module"
    include(moduleName)

    project(moduleName).apply {
        buildFileName = "build.gradle"
        path?.run {
            projectDir = File(path)
        }
    }
}

includeModule("Common", "../Common/")
