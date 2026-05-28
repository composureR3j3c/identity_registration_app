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
  }) async {
    return await _startFaceCapture(
      brisqueThreshold: brisqueThreshold,
      livenessThreshold: livenessThreshold,
    );
  }

  static Future<String?> startAuthentication() async {
    return _startFaceCapture();
  }

  static Future<String?> _startFaceCapture({
    int? brisqueThreshold,
    double? livenessThreshold,
  }) async {
    Future<String?> startFaceCapture({
      double? brisqueThreshold,
      double? livenessThreshold,
    }) async {
      try {
        final Map<String, dynamic> args = {};

        if (brisqueThreshold != null) {
          args['brisqueThreshold'] = brisqueThreshold;
        }

        if (livenessThreshold != null) {
          args['livenessThreshold'] = livenessThreshold;
        }

        final String? result = await _channel.invokeMethod<String>(
          'startFaceCapture',
          args,
        );

        return result;
      } on PlatformException catch (e) {
        throw Exception('Face capture failed: ${e.code} - ${e.message}');
      } catch (e) {
        throw Exception('Unexpected error: $e');
      }
    }
  }
}
