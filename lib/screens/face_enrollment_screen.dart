import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../services/face_capture_service.dart';

class FaceEnrollmentScreen extends StatefulWidget {
  const FaceEnrollmentScreen({this.phoneNumber, super.key});

  final String phoneNumber;

  @override
  State<FaceEnrollmentScreen> createState() => _FaceEnrollmentScreenState();
}

class _FaceEnrollmentScreenState extends State<FaceEnrollmentScreen> {
  final TextEditingController fullNameController = TextEditingController();
  final TextEditingController nationalIdController = TextEditingController();
  final FocusNode _fullNameFocus = FocusNode();
  final FocusNode _nationalIdFocus = FocusNode();
  final FocusNode _captureFocusNode = FocusNode(skipTraversal: true);

  bool _textFieldsLocked = false;

  /// Detach editable text fields before launching native capture.
  /// Unfocus alone still triggers [TextInputPlugin.hideTextInput] on Samsung and
  /// can crash in finishComposingText when the activity resumes.
  Future<void> _prepareForNativeCapture() async {
    setState(() => _textFieldsLocked = true);
    await WidgetsBinding.instance.endOfFrame;

    _fullNameFocus.unfocus();
    _nationalIdFocus.unfocus();
    FocusManager.instance.primaryFocus?.unfocus();
    _captureFocusNode.requestFocus();

    await Future.delayed(const Duration(milliseconds: 500));
  }

  Future<void> _restoreAfterNativeCapture() async {
    await Future.delayed(const Duration(milliseconds: 300));
    if (!mounted) return;

    _captureFocusNode.unfocus();
    FocusManager.instance.primaryFocus?.unfocus();

    if (mounted) {
      setState(() => _textFieldsLocked = false);
    }
  }

  @override
  void dispose() {
    fullNameController.dispose();
    nationalIdController.dispose();
    _fullNameFocus.dispose();
    _nationalIdFocus.dispose();
    _captureFocusNode.dispose();
    super.dispose();
  }

  Future<void> _runNativeCapture(Future<String?> Function() capture) async {
    await _prepareForNativeCapture();
    if (!mounted) return;

    try {
      final result = await capture();
      if (!mounted) return;

      if (result != null && result.isNotEmpty) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Capture successful'),
            backgroundColor: Colors.green,
          ),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Capture failed'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } on PlatformException catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(e.message ?? 'Capture failed'),
          backgroundColor: Colors.red,
        ),
      );
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Error: $e'),
          backgroundColor: Colors.red,
        ),
      );
    } finally {
      await _restoreAfterNativeCapture();
    }
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
            // const SizedBox(height: 20),
            // TextField(
            //   controller: fullNameController,
            //   focusNode: _fullNameFocus,
            //   readOnly: _textFieldsLocked,
            //   enableInteractiveSelection: !_textFieldsLocked,
            //   decoration: const InputDecoration(labelText: 'Full Name'),
            // ),
            // const SizedBox(height: 20),
            // TextField(
            //   controller: nationalIdController,
            //   focusNode: _nationalIdFocus,
            //   readOnly: _textFieldsLocked,
            //   enableInteractiveSelection: !_textFieldsLocked,
            //   decoration: const InputDecoration(labelText: 'National ID'),
            // ),
            const SizedBox(height: 40),
            ElevatedButton.icon(
              onPressed: () => _runNativeCapture(FaceCaptureService.startEnrollment),
              icon: const Icon(Icons.face_retouching_natural),
              label: const Text('Enroll Face'),
            ),
            Focus(
              focusNode: _captureFocusNode,
              child: const SizedBox.shrink(),
            ),
          ],
        ),
      ),
    );
  }
}
