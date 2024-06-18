plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ticketmasterandroid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ticketmasterandroid"
        minSdk = 24
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
}

dependencies {
    // GSON
    implementation("com.google.code.gson:gson:2.10.1")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    // Import the Firebase BoM
    implementation("com.google.firebase:firebase-bom:32.8.0")
    implementation ("com.firebaseui:firebase-ui-auth:8.0.0")
    // Import Firestore
    implementation("com.google.firebase:firebase-firestore:24.11.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.transport.api)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // create viewmodel in compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    // images
    implementation("io.coil-kt:coil-compose:2.4.0")
    // for icons
    implementation("androidx.compose.material:material-icons-extended:1.6.6")
    // add the dependency for the Google AI client SDK for Android
    implementation("com.google.ai.client.generativeai:generativeai:0.3.0")


}










