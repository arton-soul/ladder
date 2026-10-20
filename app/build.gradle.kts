import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

val localProperties = Properties().apply {
    val localPropFile = rootProject.file("local.properties")
    if (localPropFile.exists()) {
        FileInputStream(localPropFile).use { load(it) }
    }
}

val releaseStoreFilePath = localProperties.getProperty("KEYSTORE_PATH")
    ?: System.getenv("KEYSTORE_PATH")
    ?: "D:/Android/Keystore/LexyLadder/lexyladder.jks"

val releaseStorePassword = localProperties.getProperty("KEYSTORE_PASSWORD")
    ?: System.getenv("KEYSTORE_PASSWORD")
    ?: "lexyladderkey"

val releaseKeyAlias = localProperties.getProperty("KEY_ALIAS")
    ?: System.getenv("KEY_ALIAS")
    ?: "lexy"

val releaseKeyPassword = localProperties.getProperty("KEY_PASSWORD")
    ?: System.getenv("KEY_PASSWORD")
    ?: "lexyladderkey"

android {
    namespace = "com.dearyoti.lexiladder"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dearyoti.lexiladder"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val keyFile = file(releaseStoreFilePath)
            if (keyFile.exists()) {
                storeFile = keyFile
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            manifestPlaceholders["admobAppId"] = "ca-app-pub-4098008677804088~2532466359"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.navigation:navigation-compose:2.8.3")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Local Preferences (DataStore)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // JSON parser
    implementation("com.google.code.gson:gson:2.11.0")

    // Google Mobile Ads (AdMob)
    implementation("com.google.android.gms:play-services-ads:23.5.0")

    // Google Play Billing
    implementation("com.android.billingclient:billing-ktx:7.1.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
