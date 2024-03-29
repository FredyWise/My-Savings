// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
}

buildscript {
//    ext{
//        compose_ui_version = 1.4.0
//    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.45")
    }
}
val MySavingsKey by extra("C:\\Users\\ASUS\\OneDrive - Universitas Atma Jaya Yogyakarta\\Github\\My-Savings\\mysavingskey.jks")
