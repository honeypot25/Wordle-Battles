plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // Add the Google services Gradle plugin (for Firebase SDKs)
    id("com.google.gms.google-services")
    // Kotlin serialization
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"

}

android {
    namespace = "com.honeyapps.wordlebattles"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.honeyapps.wordlebattles"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // use BOM for compose deps
    implementation(platform("androidx.compose:compose-bom:2024.04.00"))
    // 1.7.0-alpha05 to solve CircularProgressIndicator issues
    implementation("androidx.compose.foundation:foundation:1.7.0-alpha06")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // env
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // NAVIGATION
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // INTERNET
    // Requests & Serialization
    implementation("com.squareup.retrofit2:retrofit:2.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Images
    implementation("io.coil-kt:coil-compose:2.6.0")

    // PERMISSIONS
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")
    // Location
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // STYLE
    // Extended Material icons
    implementation("androidx.compose.material:material-icons-extended-android:1.6.5")
    // Material3 placeholder for Modifier
    implementation("io.github.fornewid:placeholder-material3:1.1.2")
    // Konfetti
    implementation("nl.dionsegijn:konfetti-compose:2.0.4")

    // DEPENDENCY INJECTION (KOIN)
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")

    // GOOGLE / FIREBASE SDK
    // Import the Firebase BoM (when using the BoM, don't specify versions in Firebase dependencies)
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    // FROM https://firebase.google.com/docs/android/setup#available-libraries
    // Google Play services
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    // Authentication
    implementation("com.google.firebase:firebase-auth")
    // Storage
    implementation("com.google.firebase:firebase-storage")
    // Firestore
    implementation("com.google.firebase:firebase-firestore")
    // Cloud Messaging
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-messaging-directboot")
    // for Google Analytics
    implementation("com.google.firebase:firebase-analytics")
    // App Check (Play Integrity)
//    implementation("com.google.firebase:firebase-appcheck-playintegrity")
//    implementation("com.google.firebase:firebase-appcheck-debug")

}