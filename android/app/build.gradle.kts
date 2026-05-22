plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "com.boabeta.idregtes"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
    compilerOptions {
        jvmTarget.set(
            org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
            )
        }
    }

    defaultConfig {
        applicationId = "com.boabeta.idregtes"
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }

    packaging {
        jniLibs {
            pickFirsts += listOf("**/libc++_shared.so")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

repositories {
    flatDir {
        dirs("libs")
    }
}

dependencies {

    implementation(files("libs/airsnap-face-pro-core-1.2.7.aar"))
    implementation(files("libs/airsnap-face-pro-ui-1.2.7.aar"))
    implementation(files("libs/t5ncnn-release.aar"))
    implementation(files("libs/t5opencv-release.aar"))
    implementation(files("libs/T5AirSnap-release.aar"))
    implementation(files("libs/AirsnapFingerUI-release.aar"))

    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")

    val cameraxVersion = "1.5.1"

    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
}

flutter {
    source = "../.."
}
