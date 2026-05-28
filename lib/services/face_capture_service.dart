import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class FaceCaptureService {
  static const MethodChannel _channel = MethodChannel('tech5/face_capture');

  static Future<void> startCapture() async {
    await _startFaceCapture();
  }

  static Future<String?> startEnrollment({
    int? brisqueThreshold,
    double? livenessThreshold,
    bool? useBackCamera,
  }) async {
    return await _startFaceCapture(
      brisqueThreshold: brisqueThreshold,
      livenessThreshold: livenessThreshold,
      useBackCamera: useBackCamera,
    );
  }

  static Future<String?> startAuthentication() async {
    return _startFaceCapture();
  }

  static Future<String?> _startFaceCapture({
    int? brisqueThreshold,
    double? livenessThreshold,
    bool? useBackCamera,
  }) async {
    try {
      final args = <String, dynamic>{};
      if (brisqueThreshold != null) {
        args['brisqueThreshold'] = brisqueThreshold;
      }
      if (livenessThreshold != null) {
        args['livenessThreshold'] = livenessThreshold;
      }
      if (useBackCamera != null) {
        args['useBackCamera'] = useBackCamera;
      }
       final result= await _channel.invokeMethod<String>(
        'startFaceCapture',
        args.isEmpty ? null : args,
      );
      print('Face capture succeded $result');
     
      return result;
    } on PlatformException catch (e) {
      // throw Exception('Face capture failed: ${e.code} - ${e.message}');
      print("Face capture failed: ${e.code} - ${e.message}");
    } catch (e) {
      // throw Exception('Unexpected error: $e');
      print("unexpected error: $e");
    }
  }
}
