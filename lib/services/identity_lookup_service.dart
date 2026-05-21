import 'dart:convert';

import 'package:http/http.dart' as http;

import '../models/identity_record.dart';

class IdentityLookupService {
  IdentityLookupService({http.Client? client})
    : _client = client ?? http.Client();

  static const String _baseUrl = 'https://example.com/api';

  final http.Client _client;

  Future<IdentityRecord?> findByPhoneNumber(String phoneNumber) async {
    final uri = Uri.parse(
      '$_baseUrl/identities',
    ).replace(queryParameters: {'phone': phoneNumber});

    final response = await _client.get(uri);

    if (response.statusCode == 404) {
      return null;
    }

    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw IdentityLookupException(
        'Identity lookup failed with status ${response.statusCode}.',
      );
    }

    final decoded = jsonDecode(response.body);
    if (decoded is! Map<String, dynamic>) {
      throw const IdentityLookupException(
        'Identity lookup returned invalid data.',
      );
    }

    final payload = decoded['data'] is Map<String, dynamic>
        ? decoded['data'] as Map<String, dynamic>
        : decoded;

    if (payload.isEmpty ||
        payload['identity'] == null &&
            payload['fullName'] == null &&
            payload['full_name'] == null) {
      return null;
    }

    final identityJson = payload['identity'] is Map<String, dynamic>
        ? payload['identity'] as Map<String, dynamic>
        : payload;

    return IdentityRecord.fromJson({
      ...identityJson,
      'phoneNumber':
          identityJson['phoneNumber'] ??
          identityJson['phone_number'] ??
          phoneNumber,
    });
  }

  void dispose() {
    _client.close();
  }
}

class IdentityLookupException implements Exception {
  const IdentityLookupException(this.message);

  final String message;

  @override
  String toString() => message;
}
