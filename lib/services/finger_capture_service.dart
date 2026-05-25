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
    try {
      final result = await _channel.invokeMethod<String>('startFingerCapture');
      debugPrint('Finger capture result length: ${result?.length ?? 0}');
      return result;
    } on PlatformException {
      rethrow;
    }
  }
}
