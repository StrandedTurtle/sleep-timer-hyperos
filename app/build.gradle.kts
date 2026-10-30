plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// CI passes the run number so every release Obtainium sees has a higher versionCode.
val ciBuildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1

android {
    namespace = "com.strandedturtle.hush"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.strandedturtle.hush"
        minSdk = 31
        targetSdk = 36
        versionCode = ciBuildNumber
        versionName = "1.0.$ciBuildNumber"
    }

    signingConfigs {
        create("release") {
            // Prefer a keystore supplied through CI secrets; fall back to the bundled one so
            // every build is signed with the same key and Obtainium can update in place.
            val envStore = System.getenv("HUSH_KEYSTORE_PATH")
            storeFile = if (envStore.isNullOrBlank()) rootProject.file("keystore/hush.jks") else file(envStore)
            storePassword = System.getenv("HUSH_KEYSTORE_PASSWORD") ?: "hushhush"
            keyAlias = System.getenv("HUSH_KEY_ALIAS") ?: "hush"
            keyPassword = System.getenv("HUSH_KEY_PASSWORD") ?: "hushhush"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.coroutines.android)
}
