plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.iamashad.ashad_swipe"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.iamashad.ashad_swipe"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    }
}

dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2025.10.00"))

    // Lottie
    implementation("com.airbnb.android:lottie-compose:6.6.10")

    // Splash Screen API
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.android.material:material:1.13.0")

    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // Compose UI stack
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation(libs.androidx.compose.runtime.livedata)
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.activity:activity-compose:1.11.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.5")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Retrofit 3 + OkHttp 4.12
    implementation(platform("com.squareup.retrofit2:retrofit-bom:3.0.0"))
    implementation("com.squareup.retrofit2:retrofit")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization")
    implementation("com.squareup.okhttp3:okhttp:5.2.1")
    implementation("com.squareup.okhttp3:logging-interceptor:5.2.1")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Room (KSP)
    implementation("androidx.room:room-runtime:2.8.2")
    ksp("androidx.room:room-compiler:2.8.2")
    implementation("androidx.room:room-ktx:2.8.2")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.10.5")

    // Coil 3 (Compose)
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    // Icons
    implementation("androidx.compose.material:material-icons-extended-android:1.7.8")

    // Palettes
    implementation("androidx.palette:palette-ktx:1.0.0")

    // Koin
    implementation("io.insert-koin:koin-android:4.1.1")
    implementation("io.insert-koin:koin-androidx-compose:4.1.1")
    implementation("io.insert-koin:koin-androidx-workmanager:4.1.1")
}
