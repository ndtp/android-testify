plugins {
    id("org.jetbrains.kotlin.jvm")
}

val versions = rootProject.extra["versions"] as Map<String, String>
val testifyVersion = versions["testify"] as String

group = "dev.testify.plugin.common"
version = testifyVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}
