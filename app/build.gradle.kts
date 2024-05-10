plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.kitsune"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kitsune"
        minSdk = 31
        targetSdk = 34
        versionCode = 6
        versionName = "1.3.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}
dependencies {
    implementation(libs.volley)
    val work_version = "2.9.0"
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.work:work-runtime:$work_version")
    implementation("com.loopj.android:android-async-http:1.4.11")

}