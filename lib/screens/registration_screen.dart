import 'package:flutter/material.dart';
import '../services/face_capture_service.dart';

class RegistrationScreen extends StatefulWidget {
  const RegistrationScreen({super.key});

  @override
  State<RegistrationScreen> createState() =>
      _RegistrationScreenState();
}

class _RegistrationScreenState
    extends State<RegistrationScreen> {

  final TextEditingController fullNameController =
      TextEditingController();

  final TextEditingController nationalIdController =
      TextEditingController();

  @override
  Widget build(BuildContext context) {

    return Scaffold(
      appBar: AppBar(
        title: const Text('Register Identity'),
      ),

      body: Padding(
        padding: const EdgeInsets.all(20),

        child: Column(
          children: [

            TextField(
              controller: fullNameController,
              decoration: const InputDecoration(
                labelText: 'Full Name',
              ),
            ),

            const SizedBox(height: 20),

            TextField(
              controller: nationalIdController,
              decoration: const InputDecoration(
                labelText: 'National ID',
              ),
            ),

            const SizedBox(height: 40),

            ElevatedButton.icon(
              onPressed: () async {
                await FaceCaptureService.startCapture();
              },
              icon: const Icon(Icons.face),
              label: const Text('Capture Face'),
            ),
          ],
        ),
      ),
    );
  }
}