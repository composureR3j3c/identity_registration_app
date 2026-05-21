import 'package:flutter/material.dart';

import '../services/face_capture_service.dart';

class FaceEnrollmentScreen extends StatefulWidget {
  const FaceEnrollmentScreen({required this.phoneNumber, super.key});

  final String phoneNumber;

  @override
  State<FaceEnrollmentScreen> createState() => _FaceEnrollmentScreenState();
}

class _FaceEnrollmentScreenState extends State<FaceEnrollmentScreen> {
  final TextEditingController fullNameController = TextEditingController();
  final TextEditingController nationalIdController = TextEditingController();

  @override
  void dispose() {
    fullNameController.dispose();
    nationalIdController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Enroll Face')),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: ListView(
          children: [
            TextFormField(
              initialValue: widget.phoneNumber,
              readOnly: true,
              decoration: const InputDecoration(labelText: 'Phone Number'),
            ),
            const SizedBox(height: 20),
            TextField(
              controller: fullNameController,
              decoration: const InputDecoration(labelText: 'Full Name'),
            ),
            const SizedBox(height: 20),
            TextField(
              controller: nationalIdController,
              decoration: const InputDecoration(labelText: 'National ID'),
            ),
            const SizedBox(height: 40),
            ElevatedButton.icon(
              onPressed: () async {
                await FaceCaptureService.startEnrollment();
              },
              icon: const Icon(Icons.face_retouching_natural),
              label: const Text('Enroll Face'),
            ),
          ],
        ),
      ),
    );
  }
}
