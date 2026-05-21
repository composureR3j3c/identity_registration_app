import 'package:flutter/material.dart';
import 'screens/home_screen.dart';

import 'package:permission_handler/permission_handler.dart';

Future<void> requestCameraPermission() async {
  if (await Permission.camera.isDenied) {
    await Permission.camera.request();
  }

  if (await Permission.camera.isPermanentlyDenied) {
    openAppSettings();
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
      home: const HomeScreen(),
    );
  }
}