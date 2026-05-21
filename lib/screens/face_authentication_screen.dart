import 'package:flutter/material.dart';

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
                await FaceCaptureService.startAuthentication();
              },
              icon: const Icon(Icons.verified_user),
              label: const Text('Authenticate Face'),
            ),
          ],
        ),
      ),
    );
  }
}
