-keep class ai.tech5.sdk.abis.finger.T5AirSnap.** { *; }
-keep class ai.tech5.sdk.abis.T5AirSnap.HttpUtils { *; }
-keep class ai.tech5.sdk.abis.T5AirSnap.SgmRectImage{ *; }
-keep class ai.tech5.pheonix.capture.controller.AirsnapFaceThresholds { *; }
-keep class ai.tech5.pheonix.capture.controller.FaceCaptureController { *; }
-keep class ai.tech5.pheonix.capture.controller.FaceCaptureListener { *; }
-keep class ai.tech5.pheonix.capture.controller.GlassDetection { *; }
-keep class com.phoenixcapture.camerakit.FaceBox { *; }
-keepclassmembers class ai.tech5.sdk.abis.T5AirSnap.** {
    public *;
}