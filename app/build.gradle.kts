plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.blinkituserclone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.blinkituserclone"
        minSdk = 24
        targetSdk = 35
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Text dimensions (✅ Safe to keep same)
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")

    // Navigation components (⬇️ downgrade from 2.9.5)
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Lifecycle (⬇️ downgrade from 2.9.4)
//    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Firebase (✅ compatible with AGP 8.5.2)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    // phone otp
    implementation("com.google.android.gms:play-services-auth-api-phone:18.0.1")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    // database and storage
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")
    // for FCM
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")

    // shimmer Effect
    implementation("com.facebook.shimmer:shimmer:0.5.0@aar")

    // image slider
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    // room db
    implementation("androidx.room:room-runtime:2.8.4") // Room core library - Runtime
//    annotationProcessor("androidx.room:room-compiler:2.8.4") // Annotation processor for java files (no need of this)
    kapt("androidx.room:room-compiler:2.8.4")     // kapt(for annotation only) is for kotlin files
    implementation("androidx.room:room-ktx:2.8.4")     // Kotlin Extensions and Coroutines support for Room

    // glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Use the KSP annotation processor for Kotlin projects
    kapt("com.github.bumptech.glide:compiler:4.16.0")

}