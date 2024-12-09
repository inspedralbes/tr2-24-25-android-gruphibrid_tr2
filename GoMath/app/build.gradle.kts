plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.gomath"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gomath"
        minSdk = 35
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

    implementation(libs.androidx.core.core.ktx.v1131)
    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx.v287.x2)
    implementation(libs.androidx.activity.activity.compose.v192)
    implementation(platform(libs.androidx.compose.compose.bom.v20230800))
    implementation(libs.androidx.compose.ui.ui8)
    implementation(libs.androidx.compose.ui.ui.graphics4)
    implementation(libs.androidx.compose.ui.ui.tooling.preview8)
    implementation(libs.androidx.compose.material3.material34)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.ui)
    implementation(libs.androidx.material)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.coil)
    implementation(libs.androidx.recyclerview)
    implementation(libs.gson.v285)
    implementation(libs.json)
    implementation(libs.socket.io.client)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}