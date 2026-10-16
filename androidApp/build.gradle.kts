plugins {
    alias(libs.plugins.androidApplication)
    // AGP9 built-in Kotlin does not auto-apply the compose compiler —
    // MainActivity's setContent lambda must be Compose-transformed (@Composable→Function2).
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.horology.store"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.horology.store"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.activity.compose)
}
