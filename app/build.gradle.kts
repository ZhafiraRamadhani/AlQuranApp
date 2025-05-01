plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.alquranapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.alquranapp"
        minSdk = 31
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    //core libraries
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material3:material3:1.1.1")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    //retrofit & Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    //firebase Authentication (Opsional)
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")

    //image loading
    implementation("io.coil-kt:coil-compose:2.2.2")
    //import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))

    //TODO: Add the dependencies for Firebase products you want to use
    implementation("com.google.firebase:firebase-analytics")

    implementation("androidx.compose.material:material-icons-extended:1.5.0")

    //untuk coil(gambar profil)
    implementation("io.coil-kt:coil-compose:2.4.0")

    //material 3
    implementation("androidx.compose.material3:material3:1.2.0")

    //icons
    implementation("androidx.compose.material:material-icons-extended")

}

//plugin google-services
apply(plugin = "com.google.gms.google-services")
