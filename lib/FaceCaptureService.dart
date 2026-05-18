import 'package:flutter/services.dart';

class FaceCaptureService {
  static const platform = MethodChannel('tech5/face_capture');

  static Future<void> startCapture() async {
    await platform.invokeMethod('startFaceCapture');
  }
}