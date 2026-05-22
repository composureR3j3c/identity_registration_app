import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class FingerCaptureService {
  static const MethodChannel _channel = MethodChannel('tech5/finger_capture');

  static Future<void> startCapture() async {
    await _startFingerCapture();
  }

  static Future<String?> startEnrollment() async {
    return await _startFingerCapture();
  }

  static Future<String?> startAuthentication() async {
    return _startFingerCapture();
  }

  static Future<String?> _startFingerCapture() async {
    String? result = "";
    try {
      result = await _channel.invokeMethod('startFingerCapture');
      //await platform.invokeMethod<String>('startFingerCapture');

      print("Base64 Image: $result");
    } on PlatformException catch (e) {
      debugPrint(e.message);
    }
    return result;
  }
}
