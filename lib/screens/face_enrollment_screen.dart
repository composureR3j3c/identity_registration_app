import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:identity_registration_app/services/finger_capture_service.dart';

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
                try {
                  final result = await FaceCaptureService.startEnrollment();

                  if (result != "" && result != null && result.isNotEmpty) {
                    print("Fetched Image: $result");
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(
                        content: Text('Face enrollment successful'),
                        backgroundColor: Colors.green,
                      ),
                    );
                  } else {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(
                        content: Text('Face enrollment failed'),
                        backgroundColor: Colors.red,
                      ),
                    );
                  }
                } catch (e) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('Error: $e'),
                      backgroundColor: Colors.red,
                    ),
                  );
                }
              },
              icon: const Icon(Icons.face_retouching_natural),
              label: const Text('Enroll Face'),
            ),
               const SizedBox(height: 40),
      ElevatedButton.icon(
  onPressed: () async {
    try {
      // 1. Remove focus (VERY IMPORTANT)
      FocusManager.instance.primaryFocus?.unfocus();

      // 2. Force keyboard close
      SystemChannels.textInput.invokeMethod('TextInput.hide');

      // 3. Small delay to allow IME to settle
      await Future.delayed(const Duration(milliseconds: 200));

      final result = await FingerCaptureService.startEnrollment();

      if (result != null && result.toString().isNotEmpty) {
        print("Fetched Image: $result");

        if (!context.mounted) return;

        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Finger enrollment successful'),
            backgroundColor: Colors.green,
          ),
        );
      } else {
        if (!context.mounted) return;

        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Finger enrollment failed'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } catch (e) {
      if (!context.mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Error: $e'),
          backgroundColor: Colors.red,
        ),
      );
    }
  },
  icon: const Icon(Icons.fingerprint),
  label: const Text('Enroll Finger'),
)   ],
        ),
      ),
    );
  }
}
