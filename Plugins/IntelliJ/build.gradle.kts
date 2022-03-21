/**
 * See https://github.com/JetBrains/intellij-platform-plugin-template/blob/main/build.gradle.kts
 */
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

val versions = rootProject.extra["versions"] as Map<String, String>
val testifyVersion = versions["testify"] as String

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij")
    id("org.jetbrains.changelog")
    id("org.jlleitschuh.gradle.ktlint")
}

group = properties("pluginGroup")
version = testifyVersion

/**
 * Configure gradle-intellij-plugin plugin.
 * Read more: https://github.com/JetBrains/gradle-intellij-plugin
 */
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))

    // https://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_dependencies.html
    plugins.set(
        listOf(
            "Kotlin",
            "android"
        )
    )

    /*
    // Unresolved reference: alternativeIdePath
    alternativeIdePath.set("/Applications/Android Studio.app")
    // https://github.com/JetBrains/gradle-intellij-plugin/blob/master/README.md#setup-dsl
    localPath.set("/Applications/Android Studio Preview.app")
     */
}

repositories {
    mavenCentral()
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("script-runtime"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
}

changelog {
    version.set(testifyVersion)
    groups.set(emptyList())
}

tasks {
    properties("javaVersion").let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = it
        }
    }

    patchPluginXml {
        version.set(testifyVersion)
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        pluginDescription.set(
            projectDir.resolve("README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) }
        )

        changeNotes.set(provider {
            changelog.run {
                getOrNull(testifyVersion) ?: getLatest()
            }.toHTML()
        })
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }
}
