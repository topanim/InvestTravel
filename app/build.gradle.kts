import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    id("com.google.devtools.ksp")
}

android {
    namespace = "app.what.investtravel"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.what.investtravel"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePropsFile = file("keystore/keystore_config.properties")
            if (keystorePropsFile.exists()) {
                val keystoreProperties = Properties().apply {
                    keystorePropsFile.inputStream().use { load(it) }
                }
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            } else {
                storeFile = file("keystore/what_apps_keystore.keystore")
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
                keyAlias = System.getenv("RELEASE_SIGN_KEY_ALIAS") ?: ""
                keyPassword = System.getenv("RELEASE_SIGN_KEY_PASSWORD") ?: ""
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true    // Включить для релиза
            isShrinkResources = true  // Удалить неиспользуемые ресурсы
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            isMinifyEnabled = false   // Отключить для отладки
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources.pickFirsts.add("META-INF/*")
    }
}

dependencies {
    implementation(project(":core:foundation"))
    implementation(project(":core:navigation"))

    ksp(libs.room.compiler)

    implementation("com.yandex.android:maps.mobile:4.25.0-full")
    implementation(libs.materialKolor)
    implementation(libs.ksoup.lite)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.room)
    implementation(libs.bundles.coil)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.paging:paging-compose:3.4.0-alpha04")
//    implementation(libs.androidx.glance)
//    implementation(libs.androidx.glance.appwidget)
//    implementation(libs.androidx.glance.material3)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.icons)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}