// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("org.jetbrains.intellij") version "1.16.1"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

fun properties(key: String) = project.findProperty(key).toString()

group = project.property("pluginGroup") as String
version = properties("version")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    updateSinceUntilBuild.set(false)

    type.set("AI")
    plugins.set(listOf("android"))
    version.set(project.property("ideVersion").toString())

//    version.set(properties("InstrumentCodeVersion"))

//    instrumentCode {
//        compilerVersion = "231.9225.16"
//    }

//    type.set(properties("platformType"))

//    plugins.set(
//        listOf(
//            "Kotlin",
//            "gradle",
//            "android"
//        )
//    )
}

tasks {
    instrumentCode {
        compilerVersion.set(project.property("InstrumentCodeVersion").toString())
    }

    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        version.set("${project.version}")
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks {
    runIde {
        // Absolute path to installed target 3.5 Android Studio to use as
        // IDE Development Instance (the "Contents" directory is macOS specific):
        ideDir.set(file("/Applications/Android Studio.app/Contents"))
    }
}
