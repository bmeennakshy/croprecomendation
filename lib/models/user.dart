enum UserRole { farmer, vendor }

class User {
  final String id;
  final String name;
  final String email;
  final UserRole role;
  final String location; // e.g. "Vellore, Tamil Nadu", "Ludhiana, Punjab"
  final String phone;

  User({
    required this.id,
    required this.name,
    required this.email,
    required this.role,
    required this.location,
    required this.phone,
  });

  Map<String, dynamic> toJson() => {
        'id': id,
        'name': name,
        'email': email,
        'role': role.name,
        'location': location,
        'phone': phone,
      };

  factory User.fromJson(Map<String, dynamic> json) => User(
        id: json['id'] ?? '',
        name: json['name'] ?? '',
        email: json['email'] ?? '',
        role: (json['role'] == 'vendor' || json['role'] == 'UserRole.VENDOR')
            ? UserRole.vendor
            : UserRole.farmer,
        location: json['location'] ?? 'All India',
        phone: json['phone'] ?? '+91 9876543210',
      );
}
