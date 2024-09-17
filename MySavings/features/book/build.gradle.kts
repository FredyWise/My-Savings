plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)

    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.fredy.book"

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Projects
    implementation(projects.theme)
    implementation(projects.ui)
    implementation(projects.domain)

    // Core
    // Core Functions
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.timber)

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

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
}