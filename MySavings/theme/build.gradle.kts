plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.fredy.theme"

    buildFeatures {
        compose = true
    }
}

dependencies {


    // Core
    implementation(libs.androidx.core.ktx)

// Jetpack Compose UI libraries
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.foundation)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

// Material Design and UI Enhancements
    implementation(libs.material)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.animated.navigation.bar)
    implementation(libs.colorpicker.compose)
    implementation(libs.coil.compose)
    implementation(libs.android.image.cropper)
    implementation(libs.mpfilepicker)
    implementation(libs.ycharts)
    implementation(libs.datetime)

}