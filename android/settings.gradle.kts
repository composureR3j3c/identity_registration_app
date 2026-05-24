pluginManagement {
    val flutterSdkPath =
        run {
            val properties = java.util.Properties()
            file("local.properties").inputStream().use { properties.load(it) }
            val flutterSdkPath = properties.getProperty("flutter.sdk")
            require(flutterSdkPath != null) { "flutter.sdk not set in local.properties" }
            flutterSdkPath
        }

    includeBuild("$flutterSdkPath/packages/flutter_tools/gradle")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.flutter.flutter-plugin-loader") version "1.0.0"
    id("com.android.application") version "8.11.1" apply false
    id("com.android.library") version "8.11.1" apply false
    id("org.jetbrains.kotlin.android") version "2.2.20" apply false
}

val localProperties = java.util.Properties().apply {
    val file = file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

val tech5SdkDir =
    localProperties.getProperty("tech5.sdk.dir")
        ?: "/home/bereket/Documents/new tech 5 sdks/SampleSourceCode_with_UI_code_v6.6.30/AirsnapFingerUIDemo_with_ui_source"

include(":app")
include(":AirsnapFinger")
include(":AirsnapFingerUI")
include(":Ncnn_CPP_20230816")
include(":OpenCV_CPP_460")

project(":AirsnapFinger").projectDir = file("$tech5SdkDir/AirsnapFinger")
project(":AirsnapFingerUI").projectDir = file("$tech5SdkDir/AirsnapFingerUI")
project(":Ncnn_CPP_20230816").projectDir = file("$tech5SdkDir/Ncnn_CPP_20230816")
project(":OpenCV_CPP_460").projectDir = file("$tech5SdkDir/OpenCV_CPP_460")
