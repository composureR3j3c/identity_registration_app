import 'package:flutter/material.dart';
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

  /// Dismiss the keyboard before launching a native capture activity.
  /// Do not call [SystemChannels.textInput] hide — it can crash Samsung IMEs
  /// with ArrayIndexOutOfBoundsException in SpannableStringBuilder.
  Future<void> _dismissKeyboardBeforeNativeCapture() async {
    FocusScope.of(context).unfocus();
    await WidgetsBinding.instance.endOfFrame;
    await Future.delayed(const Duration(milliseconds: 350));
  }

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
                  await _dismissKeyboardBeforeNativeCapture();
                  if (!context.mounted) return;

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
      await _dismissKeyboardBeforeNativeCapture();
      if (!context.mounted) return;

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
