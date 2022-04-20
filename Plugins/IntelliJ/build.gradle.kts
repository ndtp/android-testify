/**
 * See https://github.com/JetBrains/intellij-platform-plugin-template/blob/main/build.gradle.kts
 */
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

@Suppress("UNCHECKED_CAST")
val versions = rootProject.extra["versions"] as Map<String, String>
val testifyVersion = versions["testify"] as String

if (!hasProperty("StudioCompilePath")) throw GradleException("No StudioCompilePath value was set. Please define StudioCompilePath in gradle.properties.")
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij") version "1.4.0"
    id("org.jetbrains.changelog") version "1.3.1"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

group = project.property("pluginGroup") as String
version = testifyVersion

val studioRunPath = project.property("StudioRunPath") as String
val studioCompilePath = project.property("StudioCompilePath") as String
val instrumentCodeVersion = project.property("InstrumentCodeVersion") as String

/**
 * Configure gradle-intellij-plugin plugin.
 * Read more: https://github.com/JetBrains/gradle-intellij-plugin
 */
intellij {
    pluginName.set(project.name)
    updateSinceUntilBuild.set(false)
    // https://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_dependencies.html
    plugins.set(
        listOf(
            "android"
        )
    )
    // Set runIde path
    localPath.set( if (project.hasProperty("StudioRunPath")) studioRunPath else studioCompilePath)
}

repositories {
    mavenCentral()
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${versions["kotlin"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    compileOnly(fileTree("dir" to "$studioCompilePath/plugins/android/lib", "include" to "*.jar"))
    compileOnly(fileTree("dir" to "$studioCompilePath/lib", "include" to "*.jar"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

changelog {
    version.set(testifyVersion)
    groups.set(emptyList())
}

tasks {
    instrumentCode {
        compilerVersion.set(instrumentCodeVersion)
    }
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

        changeNotes.set(
            provider {
                changelog.run {
                    getOrNull(testifyVersion) ?: getLatest()
                }.toHTML()
            }
        )
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }

    register("verifySetup") {
        doLast {
            val ideaJar = "$studioCompilePath/lib/idea.jar"
            if (file(ideaJar).exists().not()) {
                throw GradleException("$ideaJar not found, set 'StudioCompilePath' in gradle.properties")
            }
        }
    }

    findByName("compileJava")?.dependsOn(findByName("verifySetup"))

    buildSearchableOptions {
        enabled = false
    }
}
