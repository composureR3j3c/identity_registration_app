import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:shared_preferences/shared_preferences.dart';

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
  final TextEditingController brisqueThresholdController =
      TextEditingController();
  final TextEditingController livenessThresholdController =
      TextEditingController();
  final FocusNode _fullNameFocus = FocusNode();
  final FocusNode _nationalIdFocus = FocusNode();
  final FocusNode _captureFocusNode = FocusNode(skipTraversal: true);

  bool _textFieldsLocked = false;
  bool _useBackCamera = false;

  @override
  void initState() {
    super.initState();
    _loadThresholdDefaults();
  }

  Future<void> _loadThresholdDefaults() async {
    final prefs = await SharedPreferences.getInstance();
    final brisque = prefs.getInt('brisque_threshold') ?? 60;
    final liveness = prefs.getDouble('liveness_threshold') ?? 0.5;
    final useBackCamera = prefs.getBool('use_back_camera') ?? false;

    setState(() {
      brisqueThresholdController.text = brisque.toString();
      livenessThresholdController.text = liveness.toString();
      _useBackCamera = useBackCamera;
    });
  }

  Future<void> _saveThresholdDefaults() async {
    final prefs = await SharedPreferences.getInstance();
    final brisque = int.tryParse(brisqueThresholdController.text) ?? 60;
    final liveness = double.tryParse(livenessThresholdController.text) ?? 0.5;

    await prefs.setInt('brisque_threshold', brisque);
    await prefs.setDouble('liveness_threshold', liveness);
    await prefs.setBool('use_back_camera', _useBackCamera);
  }

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
    brisqueThresholdController.dispose();
    livenessThresholdController.dispose();
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
        print("result is null");
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Capture failed'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } on PlatformException catch (e) {
      print("PlatformException during capture: ${e.code} - ${e.message}");
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
        SnackBar(content: Text('Error: $e'), backgroundColor: Colors.red),
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
            const SizedBox(height: 30),
            TextField(
              controller: brisqueThresholdController,
              readOnly: _textFieldsLocked,
              enableInteractiveSelection: !_textFieldsLocked,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(
                labelText: 'BRISQUE Threshold',
                hintText: 'Default: 60',
              ),
            ),
            const SizedBox(height: 20),
            TextField(
              controller: livenessThresholdController,
              readOnly: _textFieldsLocked,
              enableInteractiveSelection: !_textFieldsLocked,
              keyboardType: const TextInputType.numberWithOptions(
                decimal: true,
              ),
              decoration: const InputDecoration(
                labelText: 'Liveness Threshold',
                hintText: 'Default: 0.5',
              ),
            ),
            const SizedBox(height: 20),
            SwitchListTile(
              title: const Text('Use Back Camera'),
              subtitle: Text(_useBackCamera ? 'Back' : 'Front'),
              value: _useBackCamera,
              onChanged: _textFieldsLocked
                  ? null
                  : (value) {
                      setState(() => _useBackCamera = value);
                    },
            ),
            const SizedBox(height: 40),
            ElevatedButton.icon(
              onPressed: () async {
                await _saveThresholdDefaults();
                if (!mounted) return;
                await _runNativeCapture(() async {
                  final brisque =
                      int.tryParse(brisqueThresholdController.text) ?? 60;
                  final liveness =
                      double.tryParse(livenessThresholdController.text) ?? 0.5;
                  return FaceCaptureService.startEnrollment(
                    brisqueThreshold: brisque,
                    livenessThreshold: liveness,
                    useBackCamera: _useBackCamera,
                  );
                });
              },
              icon: const Icon(Icons.face_retouching_natural),
              label: const Text('Enroll Face'),
            ),
            Focus(focusNode: _captureFocusNode, child: const SizedBox.shrink()),
          ],
        ),
      ),
    );
  }
}
