plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.thingsflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.thingsflow"
        minSdk = 31
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    var okhttp3 = "4.10.0"
    var paho_client = "1.2.5"
    var lottie = "5.0.3"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(group = "", name = "rogocore", ext = "jar")
    implementation(group = "", name = "rogocloudapi", ext = "jar")
    implementation(group = "", name = "rogoplatform", ext = "jar")
    implementation(group = "", name = "rogoplatformandroid-release", ext = "aar")
    implementation(group = "", name = "rogoutils", ext = "jar")
    implementation(group = "", name = "rogosigmesh", ext = "jar")
    implementation(group = "", name = "rogocli", ext = "jar")

    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.okhttp3:okhttp:$okhttp3")
    implementation ("io.github.rburgst:okhttp-digest:2.6")
    implementation ("org.eclipse.paho:org.eclipse.paho.mqttv5.client:$paho_client")
    implementation ("com.airbnb.android:lottie:$lottie")
    implementation ("com.google.dagger:hilt-android:2.50")
    kapt ("com.google.dagger:hilt-android-compiler:2.50")

}