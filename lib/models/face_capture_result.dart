import 'dart:convert';

class FaceCaptureResult {
  final String? imagePath;
  final double? liveness;
  final int? brisque;
  final bool isSuccess;

  FaceCaptureResult({
    this.imagePath,
    this.liveness,
    this.brisque,
    required this.isSuccess,
  });

  factory FaceCaptureResult.fromJsonString(String jsonString) {
    try {
      final json = jsonDecode(jsonString);
      return FaceCaptureResult.fromJson(json);
    } catch (e) {
      // If parsing fails, treat the string as image path
      return FaceCaptureResult(
        imagePath: jsonString.isNotEmpty ? jsonString : null,
        isSuccess: jsonString.isNotEmpty,
      );
    }
  }

  factory FaceCaptureResult.fromJson(Map<String, dynamic> json) {
    return FaceCaptureResult(
      imagePath: json['imagePath'] ?? json['image_path'],
      liveness: _parseDouble(json['liveness']),
      brisque: _parseInt(json['brisque']),
      isSuccess: true,
    );
  }

  static double? _parseDouble(dynamic value) {
    if (value == null) return null;
    if (value is double) return value;
    if (value is int) return value.toDouble();
    if (value is String) return double.tryParse(value);
    return null;
  }

  static int? _parseInt(dynamic value) {
    if (value == null) return null;
    if (value is int) return value;
    if (value is double) return value.toInt();
    if (value is String) return int.tryParse(value);
    return null;
  }

  @override
  String toString() {
    return 'FaceCaptureResult(imagePath: $imagePath, liveness: $liveness, brisque: $brisque, isSuccess: $isSuccess)';
  }
}
