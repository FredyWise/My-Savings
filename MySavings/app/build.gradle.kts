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
    // Essential libraries for app core functionality
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    // Jetpack Compose UI libraries
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // Additional UI
    implementation("androidx.compose.foundation:foundation:1.6.1")
    // More UI elements
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.compose.material:material-icons-extended")
    // Firebase integration and features
    implementation("com.google.android.gms:play-services-vision:20.1.3")//add this to remove duplicate class error
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.firebase:firebase-ml-vision:24.1.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.2")) // this will automatically give the latest stable version
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Google ml kit
    implementation("com.google.android.gms:play-services-mlkit-document-scanner:16.0.0-beta1")

    // Unit and UI testing libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    // Additional functionalities like permissions, workers, image cropping
    // Worker
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    // Permission
    implementation("com.google.accompanist:accompanist-permissions:0.33.2-alpha")
    // Coil
    implementation("io.coil-kt:coil-compose:2.4.0")
    // Image Cropper
    implementation("com.github.CanHub:Android-Image-Cropper:4.0.0")
    // Color Picker Compose
    implementation("com.github.skydoves:colorpicker-compose:1.0.7")
    // dir and file picker
    implementation("com.darkrockstudios:mpfilepicker:3.1.0")
    // splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // View Model lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Room database
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-paging:2.6.1")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Charts
    implementation("co.yml:ycharts:2.1.0")

    // Date and Time dialogs
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Custom compose versions (if needed)
    val composeVersion = "1.6.1"
    // Compose UI
    implementation("androidx.compose.ui:ui:$composeVersion")
    // Compose UI Tooling
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")

    // Retrofit networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Dagger-Hilt dependency injection
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    kapt("androidx.hilt:hilt-compiler:1.1.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Paging
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("androidx.paging:paging-compose:3.2.1")
    implementation("androidx.paging:paging-common-ktx:3.2.1")
}

kapt {
    correctErrorTypes = true
}