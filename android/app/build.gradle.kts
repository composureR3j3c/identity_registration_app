import java.util.Properties

plugins {
    id("com.android.application")
    id("dev.flutter.flutter-gradle-plugin")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

val tech5License =
    localProperties.getProperty("TECH5_LICENSE", "")
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")

android {
    namespace = "com.tech5.fingercapture"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "ai.tech5.fingercapture"
        minSdk = maxOf(flutter.minSdkVersion, 23)
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
        buildConfigField("String", "TECH5_LICENSE", "\"$tech5License\"")
    }

    packaging {
        jniLibs {
            pickFirsts += listOf("**/libc++_shared.so")
            useLegacyPackaging = true
        }
        
    }
    androidResources {
        noCompress += listOf(
            "bin",
            "param",
            "yaml",
            "txt"
        )
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

repositories {
    flatDir {
        dirs("libs")
    }
}

dependencies {
    // Face SDK (still bundled as AARs)
    implementation(files("libs/airsnap-face-pro-core-1.2.7.aar"))
    implementation(files("libs/airsnap-face-pro-ui-1.2.7.aar"))

    // Finger SDK — same Gradle module imports as AirsnapFingerUIDemo_with_ui_source
    implementation(files("libs/T5AirSnap-release.aar"))
    implementation(files("libs/AirsnapFingerUI-release.aar"))
    implementation(files("libs/t5ncnn-release.aar"))
    implementation(files("libs/t5opencv-release.aar"))

    // implementation(project(":AirsnapFinger"))
    // implementation(project(":AirsnapFingerUI"))
    // implementation(project(":Ncnn_CPP_20230816"))
    // implementation(project(":OpenCV_CPP_460"))

    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    val cameraxVersion = "1.5.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}

flutter {
    source = "../.."
}
