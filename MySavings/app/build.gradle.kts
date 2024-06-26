plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.fredy.mysavings"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fredy.mysavings"
        minSdk = 29
        targetSdk = 33
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Fire Base
    implementation ("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    // Coil
//    implementation ("io.coil-kt:coil-compose:2.4.0")

    //    More Icons
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.compose.material:material-icons-extended")

    //    View Model lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // Room works with plugin kapt
    val room_version = "2.6.0"
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    //    Date And Time Dialog
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")

    //    Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")

    var compose_version = "1.5.4"
    // Compose Material Design
    implementation("androidx.compose.material:material:$compose_version")

    // Compose UI
    implementation("androidx.compose.ui:ui:$compose_version")

    // Compose UI Tooling
    implementation("androidx.compose.ui:ui-tooling:$compose_version")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.45")
    kapt("androidx.hilt:hilt-compiler:1.1.0")
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")
}