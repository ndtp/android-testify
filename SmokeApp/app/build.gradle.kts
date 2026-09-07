plugins {
    id("com.android.application")
}

android {
    namespace = "dev.testify.smoke"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.testify.smoke"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
}
