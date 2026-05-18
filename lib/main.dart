import 'package:flutter/material.dart';
import 'screens/home_screen.dart';

void main() {
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