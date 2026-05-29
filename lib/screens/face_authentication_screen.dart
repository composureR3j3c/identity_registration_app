import 'package:flutter/material.dart';

import '../models/face_capture_result.dart';
import '../models/identity_record.dart';
import '../services/face_capture_service.dart';

class FaceAuthenticationScreen extends StatelessWidget {
  const FaceAuthenticationScreen({required this.identity, super.key});

  final IdentityRecord identity;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Authenticate Face')),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            TextFormField(
              initialValue: identity.fullName,
              readOnly: true,
              decoration: const InputDecoration(labelText: 'Full Name'),
            ),
            const SizedBox(height: 20),
            TextFormField(
              initialValue: identity.nationalId,
              readOnly: true,
              decoration: const InputDecoration(labelText: 'National ID'),
            ),
            const SizedBox(height: 20),
            TextFormField(
              initialValue: identity.phoneNumber,
              readOnly: true,
              decoration: const InputDecoration(labelText: 'Phone Number'),
            ),
            const SizedBox(height: 40),
            ElevatedButton.icon(
              onPressed: () async {
                try {
                  final result = await FaceCaptureService.startAuthentication();

                  if (result != null && result.isNotEmpty) {
                    // _showCaptureResultDialog(context, result);
                  } else {
                    _showAlertDialog(
                      context,
                      title: 'Authentication Failed',
                      message: 'Failed to authenticate face. Please try again.',
                    );
                  }
                } catch (e) {
                  _showAlertDialog(
                    context,
                    title: 'Error',
                    message: 'Error: $e',
                  );
                }
              },
              icon: const Icon(Icons.verified_user),
              label: const Text('Authenticate Face'),
            ),
          ],
        ),
      ),
    );
  }

  static void _showCaptureResultDialog(
    BuildContext context,
    FaceCaptureResult result,
  ) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Authentication Successful'),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Face authenticated successfully!'),
                const SizedBox(height: 16),
                if (result.liveness != null)
                  Padding(
                    padding: const EdgeInsets.symmetric(vertical: 8.0),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text(
                          'Liveness:',
                          style: TextStyle(fontWeight: FontWeight.bold),
                        ),
                        Text(
                          result.liveness!.toStringAsFixed(4),
                          style: const TextStyle(fontSize: 16),
                        ),
                      ],
                    ),
                  ),
                if (result.brisque != null)
                  Padding(
                    padding: const EdgeInsets.symmetric(vertical: 8.0),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text(
                          'Brisque:',
                          style: TextStyle(fontWeight: FontWeight.bold),
                        ),
                        Text(
                          result.brisque.toString(),
                          style: const TextStyle(fontSize: 16),
                        ),
                      ],
                    ),
                  ),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text('OK'),
            ),
          ],
        );
      },
    );
  }

  static void _showAlertDialog(
    BuildContext context, {
    required String title,
    required String message,
  }) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text(title),
          content: Text(message),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text('OK'),
            ),
          ],
        );
      },
    );
  }
}
