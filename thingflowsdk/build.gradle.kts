plugins {
    id("com.android.library")
}

android {
    namespace = "com.example.thingflowsdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 31
        targetSdk = 34
//        versionCode = 1
//        versionName = "1.0"

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
}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
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
}