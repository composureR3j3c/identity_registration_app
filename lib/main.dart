import 'package:flutter/material.dart';
import 'screens/home_screen.dart';

import 'package:permission_handler/permission_handler.dart';

Future<void> requestCameraPermission() async {
  try {
    PermissionStatus status = await Permission.camera.request();

    if (status.isDenied) {
      print("Camera permission denied");
    } else if (status.isGranted) {
      print("Camera permission granted");
    } else if (status.isPermanentlyDenied) {
      print("Camera permission permanently denied, opening settings");
      openAppSettings();
    }
  } catch (e) {
    print("Error requesting camera permission: $e");
  }
}

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await requestCameraPermission();
  runApp(const IdentityRegistrationApp());
}

class IdentityRegistrationApp extends StatelessWidget {
  const IdentityRegistrationApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Identity Registration',
      theme: ThemeData(
        primarySwatch: Colors.amber,
        scaffoldBackgroundColor: Colors.white,
      ),
      home: const FaceEnrollmentScreen(phoneNumber: '0912345678'),
    );
  }
}
