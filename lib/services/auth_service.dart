import 'package:firebase_auth/firebase_auth.dart' as fb;
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';
import '../models/user.dart';

class AuthService {
  final fb.FirebaseAuth? _firebaseAuth;
  final FirebaseFirestore? _firestore;
  bool useFirebase = false;

  AuthService()
      : _firebaseAuth = _tryGetFirebaseAuth(),
        _firestore = _tryGetFirestore();

  static fb.FirebaseAuth? _tryGetFirebaseAuth() {
    try {
      return fb.FirebaseAuth.instance;
    } catch (_) {
      return null;
    }
  }

  static FirebaseFirestore? _tryGetFirestore() {
    try {
      return FirebaseFirestore.instance;
    } catch (_) {
      return null;
    }
  }

  Future<bool> initialize() async {
    try {
      if (_firebaseAuth != null) {
        useFirebase = true;
      }
    } catch (_) {
      useFirebase = false;
    }
    return useFirebase;
  }

  static final Map<String, User> _demoUsers = {
    'farmer@farm2market.com': User(
      id: 'u1',
      name: 'Murugan Farmer',
      email: 'farmer@farm2market.com',
      role: UserRole.farmer,
      location: 'Vellore, Tamil Nadu',
      phone: '+91 9876543210',
    ),
    'vendor@farm2market.com': User(
      id: 'u2',
      name: 'Kumar Vendor',
      email: 'vendor@farm2market.com',
      role: UserRole.vendor,
      location: 'Chennai, Tamil Nadu',
      phone: '+91 9123456789',
    ),
  };

  static final Map<String, String> _demoPasswords = {
    'farmer@farm2market.com': 'password123',
    'vendor@farm2market.com': 'password123',
  };

  Future<User?> signIn(String email, String password) async {
    email = email.trim().toLowerCase();

    if (useFirebase && _firebaseAuth != null && _firestore != null) {
      try {
        final credential = await _firebaseAuth.signInWithEmailAndPassword(
          email: email,
          password: password,
        );
        final uid = credential.user?.uid;
        if (uid != null) {
          final doc = await _firestore.collection('users').doc(uid).get();
          if (doc.exists && doc.data() != null) {
            final user = User.fromJson(doc.data()!);
            await _saveLocalSession(user);
            return user;
          }
        }
      } catch (_) {}
    }

    if (_demoUsers.containsKey(email) && _demoPasswords[email] == password) {
      final user = _demoUsers[email]!;
      await _saveLocalSession(user);
      return user;
    }

    final prefs = await SharedPreferences.getInstance();
    final registeredUsersStr = prefs.getString('mock_users');
    if (registeredUsersStr != null) {
      final Map<String, dynamic> usersMap = jsonDecode(registeredUsersStr);
      if (usersMap.containsKey(email)) {
        final userData = usersMap[email] as Map<String, dynamic>;
        if (userData['password'] == password) {
          final user = User.fromJson(userData['user']);
          await _saveLocalSession(user);
          return user;
        }
      }
    }

    throw Exception('Invalid email or password. Try farmer@farm2market.com / password123');
  }

  Future<User?> signUp({
    required String name,
    required String email,
    required String password,
    required UserRole role,
    required String location,
    required String phone,
  }) async {
    email = email.trim().toLowerCase();

    if (useFirebase && _firebaseAuth != null && _firestore != null) {
      try {
        final credential = await _firebaseAuth.createUserWithEmailAndPassword(
          email: email,
          password: password,
        );
        final uid = credential.user?.uid ?? DateTime.now().millisecondsSinceEpoch.toString();
        final newUser = User(
          id: uid,
          name: name,
          email: email,
          role: role,
          location: location,
          phone: phone,
        );
        await _firestore.collection('users').doc(uid).set(newUser.toJson());
        await _saveLocalSession(newUser);
        return newUser;
      } catch (_) {}
    }

    final newUser = User(
      id: 'u_${DateTime.now().millisecondsSinceEpoch}',
      name: name,
      email: email,
      role: role,
      location: location,
      phone: phone,
    );

    final prefs = await SharedPreferences.getInstance();
    final registeredUsersStr = prefs.getString('mock_users');
    Map<String, dynamic> usersMap = {};
    if (registeredUsersStr != null) {
      usersMap = jsonDecode(registeredUsersStr);
    }
    usersMap[email] = {
      'password': password,
      'user': newUser.toJson(),
    };
    await prefs.setString('mock_users', jsonEncode(usersMap));
    await _saveLocalSession(newUser);
    return newUser;
  }

  Future<void> signOut() async {
    if (useFirebase && _firebaseAuth != null) {
      try {
        await _firebaseAuth.signOut();
      } catch (_) {}
    }
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('current_user');
  }

  Future<User?> getCurrentUser() async {
    if (useFirebase && _firebaseAuth != null && _firestore != null) {
      try {
        final fbUser = _firebaseAuth.currentUser;
        if (fbUser != null) {
          final doc = await _firestore.collection('users').doc(fbUser.uid).get();
          if (doc.exists && doc.data() != null) {
            return User.fromJson(doc.data()!);
          }
        }
      } catch (_) {}
    }

    final prefs = await SharedPreferences.getInstance();
    final userStr = prefs.getString('current_user');
    if (userStr != null) {
      try {
        return User.fromJson(jsonDecode(userStr));
      } catch (_) {}
    }
    return null;
  }

  Future<void> _saveLocalSession(User user) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('current_user', jsonEncode(user.toJson()));
  }
}
