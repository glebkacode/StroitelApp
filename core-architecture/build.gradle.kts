plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.itapp.core_architecture"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.kotlin.coroutines.core)

    api(libs.mvikotlin)
    api(libs.mvikotlin.main)
    api(libs.mvikotlin.timetravel)
    api(libs.mvikotlin.logging)
    api(libs.mvikotlin.extensions.coroutines)

    api(libs.decompose)
}
