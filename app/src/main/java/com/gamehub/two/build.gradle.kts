plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.gamehub.two"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gamehub.two"
        minSdk = 26
        targetSdk = 35
        versionCode = 20
        versionName = "2.0"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")
}
