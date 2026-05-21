import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class FaceCaptureService {
  static const MethodChannel _channel = MethodChannel('tech5/face_capture');

  static Future<void> startCapture() async {
    await _startFaceCapture();
  }

  static Future<void> startEnrollment() async {
    await _startFaceCapture();
  }

  static Future<void> startAuthentication() async {
    await _startFaceCapture();
  }

  static Future<String?> _startFaceCapture(BuildContext context) async {
    String? result = " ";
    try {
      result = await _channel.invokeMethod('startFaceCapture');
      //await platform.invokeMethod<String>('startFaceCapture');

       if (result != null && result.isNotEmpty) {
      debugPrint("Base64 Image: $result");

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Face capture successful'),
          backgroundColor: Colors.green,
        ),
      );

      return result;
     
    } else{
        ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Face capture failed'),
          backgroundColor: Colors.red,
        ),
      );

      return null;
      }
    } on PlatformException catch (e) {
      debugPrint(e.message);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Face capture failed'),
          backgroundColor: Colors.red,
        ),
      );

      return null;
    }
  }
}
