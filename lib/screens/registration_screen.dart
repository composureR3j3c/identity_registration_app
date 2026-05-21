import 'package:flutter/material.dart';

import '../services/identity_lookup_service.dart';
import 'face_authentication_screen.dart';
import 'face_enrollment_screen.dart';

class RegistrationScreen extends StatefulWidget {
  const RegistrationScreen({super.key});

  @override
  State<RegistrationScreen> createState() => _RegistrationScreenState();
}

class _RegistrationScreenState extends State<RegistrationScreen> {
  final IdentityLookupService identityLookupService = IdentityLookupService();

  final TextEditingController phoneNumberController = TextEditingController();

  bool isLookingUpIdentity = false;
  String? lookupMessage;

  @override
  void dispose() {
    identityLookupService.dispose();
    phoneNumberController.dispose();
    super.dispose();
  }

  Future<void> lookupIdentity() async {
    final phoneNumber = phoneNumberController.text.trim();

    if (phoneNumber.isEmpty) {
      setState(() {
        lookupMessage = 'Enter a phone number to continue.';
      });
      return;
    }

    setState(() {
      isLookingUpIdentity = true;
      lookupMessage = null;
    });

    try {
      final identity = await identityLookupService.findByPhoneNumber(
        phoneNumber,
      );

      if (!mounted) {
        return;
      }

      setState(() {
        isLookingUpIdentity = false;
      });

      await Navigator.push(
        context,
        MaterialPageRoute(
          builder: (_) => identity == null
              ? FaceEnrollmentScreen(phoneNumber: phoneNumber)
              : FaceAuthenticationScreen(identity: identity),
        ),
      );
    } on IdentityLookupException catch (error) {
      if (!mounted) {
        return;
      }

      setState(() {
        isLookingUpIdentity = false;
        lookupMessage = error.message;
      });
    } catch (_) {
      if (!mounted) {
        return;
      }

      setState(() {
        isLookingUpIdentity = false;
        lookupMessage = 'Could not reach the identity service.';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Register Identity')),

      body: Padding(
        padding: const EdgeInsets.all(20),

        child: ListView(
          children: [
            TextField(
              controller: phoneNumberController,
              keyboardType: TextInputType.phone,
              textInputAction: TextInputAction.search,
              decoration: const InputDecoration(
                labelText: 'Phone Number',
                prefixIcon: Icon(Icons.phone),
              ),
              onSubmitted: (_) => lookupIdentity(),
            ),

            const SizedBox(height: 16),

            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: isLookingUpIdentity ? null : lookupIdentity,
                icon: isLookingUpIdentity
                    ? const SizedBox(
                        width: 18,
                        height: 18,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Icon(Icons.search),
                label: Text(
                  isLookingUpIdentity ? 'Searching...' : 'Find Identity',
                ),
              ),
            ),

            if (lookupMessage != null) ...[
              const SizedBox(height: 12),
              Text(
                lookupMessage!,
                style: TextStyle(
                  color: Theme.of(context).colorScheme.secondary,
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}
