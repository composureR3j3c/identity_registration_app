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
    try {
      return await _channel.invokeMethod<String>('startFaceCapture');
    } on PlatformException {
      rethrow;
    }
  }
}
