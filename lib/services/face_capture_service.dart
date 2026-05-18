import 'package:flutter/services.dart';

class FaceCaptureService {
  static const MethodChannel _channel =
      MethodChannel('tech5/face_capture');

  static Future<void> startCapture() async {
    try {
      await _channel.invokeMethod('startFaceCapture');
    } on PlatformException catch (e) {
      print(e.message);
    }
  }
}