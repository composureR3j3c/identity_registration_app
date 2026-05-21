class IdentityRecord {
  const IdentityRecord({
    required this.fullName,
    required this.nationalId,
    required this.phoneNumber,
  });

  final String fullName;
  final String nationalId;
  final String phoneNumber;

  factory IdentityRecord.fromJson(Map<String, dynamic> json) {
    return IdentityRecord(
      fullName: (json['fullName'] ?? json['full_name'] ?? '').toString(),
      nationalId: (json['nationalId'] ?? json['national_id'] ?? '').toString(),
      phoneNumber: (json['phoneNumber'] ?? json['phone_number'] ?? '')
          .toString(),
    );
  }
}
