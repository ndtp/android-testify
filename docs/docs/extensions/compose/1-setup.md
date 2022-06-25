# Set up testify-compose

<a href="https://search.maven.org/artifact/com.shopify.testify/testify-compose"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/com.shopify.testify/testify-compose?color=%236e40ed&label=com.shopify.testify%3Atestify-compose"/></a>

**Root build.gradle**
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "com.shopify.testify:plugin:1.2.0-alpha01"
    }
}
```

**Application build.gradle**
```groovy
dependencies {
    androidTestImplementation "com.shopify.testify:testify-compose:1.2.0-alpha01"
}
```
