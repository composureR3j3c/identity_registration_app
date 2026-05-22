import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class FaceCaptureService {
  static const MethodChannel _channel = MethodChannel('tech5/face_capture');

  static Future<void> startCapture() async {
    await _startFaceCapture();
  }

  static Future<String?> startEnrollment() async {
    return await _startFaceCapture();
  }

  static Future<String?> startAuthentication() async {
    return _startFaceCapture();
  }

  static Future<String?> _startFaceCapture() async {
    String? result = "";
    try {
      result = await _channel.invokeMethod('startFaceCapture');
      //await platform.invokeMethod<String>('startFaceCapture');

      print("Base64 Image: $result");
    } on PlatformException catch (e) {
      debugPrint(e.message);
    }
    return result;
  }
}
