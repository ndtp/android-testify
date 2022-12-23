# Set up testify-compose

<a href="https://search.maven.org/artifact/dev.testify/testify-compose"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/dev.testify/testify-compose?color=%236e40ed&label=dev.testify%3Atestify-compose"/></a>

**Root build.gradle**
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "dev.testify:plugin:2.0.0-alpha01"
    }
}
```

**Application build.gradle**
```groovy
dependencies {
    androidTestImplementation "dev.testify:testify-compose:2.0.0-alpha01"
}
```
