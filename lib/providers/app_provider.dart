import 'package:flutter/material.dart';
import '../models/user.dart';
import '../models/marketplace_listing.dart';
import '../models/purchase_request.dart';
import '../models/crop_recommendation.dart';
import '../models/educational_article.dart';
import '../services/auth_service.dart';
import '../services/farm_repository.dart';

class AppProvider with ChangeNotifier {
  final AuthService _authService = AuthService();
  final FarmRepository _farmRepository = FarmRepository();

  User? _currentUser;
  bool _isLoading = true;
  String? _errorMessage;

  List<MarketplaceListing> _listings = [];
  List<PurchaseRequest> _purchaseRequests = [];
  List<EducationalArticle> _articles = [];
  CropRecommendation? _lastRecommendation;

  String _selectedCategory = 'All';
  String _selectedLocation = 'All India';

  User? get currentUser => _currentUser;
  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;
  List<MarketplaceListing> get listings => _listings;
  List<PurchaseRequest> get purchaseRequests => _purchaseRequests;
  List<EducationalArticle> get articles => _articles;
  CropRecommendation? get lastRecommendation => _lastRecommendation;
  String get selectedCategory => _selectedCategory;
  String get selectedLocation => _selectedLocation;
  List<String> get indiaLocations => _farmRepository.indiaLocations;
  List<String> get cropCategories => _farmRepository.cropCategories;
  List<String> get educationalCategories => _farmRepository.educationalCategories;

  AppProvider() {
    initApp();
  }

  Future<void> initApp() async {
    _isLoading = true;
    notifyListeners();

    try {
      final isFirebaseActive = await _authService.initialize();
      _farmRepository.setFirebaseUsage(isFirebaseActive);

      _currentUser = await _authService.getCurrentUser();
      await loadMarketData();
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> loadMarketData() async {
    try {
      _listings = await _farmRepository.getListings();
      _articles = await _farmRepository.getArticles();
      if (_currentUser != null) {
        _purchaseRequests = await _farmRepository.getPurchaseRequests(
          userId: _currentUser!.id,
          isVendor: _currentUser!.role == UserRole.vendor,
        );
      } else {
        _purchaseRequests = await _farmRepository.getPurchaseRequests();
      }
    } catch (e) {
      _errorMessage = e.toString();
    }
    notifyListeners();
  }

  Future<bool> signIn(String email, String password) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _currentUser = await _authService.signIn(email, password);
      await loadMarketData();
      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _errorMessage = e.toString().replaceAll('Exception: ', '');
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<bool> signUp({
    required String name,
    required String email,
    required String password,
    required UserRole role,
    required String location,
    required String phone,
  }) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _currentUser = await _authService.signUp(
        name: name,
        email: email,
        password: password,
        role: role,
        location: location,
        phone: phone,
      );
      await loadMarketData();
      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _errorMessage = e.toString().replaceAll('Exception: ', '');
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<void> signOut() async {
    await _authService.signOut();
    _currentUser = null;
    await loadMarketData();
    notifyListeners();
  }

  void setSelectedCategory(String category) {
    _selectedCategory = category;
    notifyListeners();
  }

  void setSelectedLocation(String location) {
    _selectedLocation = location;
    notifyListeners();
  }

  Future<void> addListing({
    required String cropName,
    required String category,
    required double quantityKg,
    required double pricePerKg,
    required String description,
  }) async {
    if (_currentUser == null) return;
    final newListing = MarketplaceListing(
      id: 'l_${DateTime.now().millisecondsSinceEpoch}',
      farmerId: _currentUser!.id,
      farmerName: _currentUser!.name,
      cropName: cropName,
      category: category,
      quantityKg: quantityKg,
      pricePerKg: pricePerKg,
      location: _currentUser!.location,
      description: description,
      timestamp: DateTime.now().millisecondsSinceEpoch,
    );
    await _farmRepository.saveListing(newListing);
    await loadMarketData();
  }

  Future<void> createPurchaseRequest({
    required MarketplaceListing listing,
    required double quantityKg,
    required double offeredPricePerKg,
  }) async {
    if (_currentUser == null) return;
    final request = PurchaseRequest(
      id: 'req_${DateTime.now().millisecondsSinceEpoch}',
      listingId: listing.id,
      cropName: listing.cropName,
      vendorId: _currentUser!.id,
      vendorName: _currentUser!.name,
      farmerId: listing.farmerId,
      quantityKg: quantityKg,
      offeredPricePerKg: offeredPricePerKg,
      status: RequestStatus.pending,
      timestamp: DateTime.now().millisecondsSinceEpoch,
    );
    await _farmRepository.savePurchaseRequest(request);
    await loadMarketData();
  }

  Future<void> updateRequestStatus(PurchaseRequest request, RequestStatus newStatus, {double? counterPrice}) async {
    final updated = PurchaseRequest(
      id: request.id,
      listingId: request.listingId,
      cropName: request.cropName,
      vendorId: request.vendorId,
      vendorName: request.vendorName,
      farmerId: request.farmerId,
      quantityKg: request.quantityKg,
      offeredPricePerKg: request.offeredPricePerKg,
      status: newStatus,
      counterOfferPricePerKg: counterPrice ?? request.counterOfferPricePerKg,
      timestamp: request.timestamp,
    );
    await _farmRepository.savePurchaseRequest(updated);
    await loadMarketData();
  }

  CropRecommendation getCropRecommendation({
    required String soilType,
    required double pH,
    required int nitrogen,
    required int phosphorus,
    required int potassium,
  }) {
    final rec = _farmRepository.calculateRecommendation(
      farmerId: _currentUser?.id ?? 'guest',
      soilType: soilType,
      pH: pH,
      nitrogen: nitrogen,
      phosphorus: phosphorus,
      potassium: potassium,
    );
    _lastRecommendation = rec;
    notifyListeners();
    return rec;
  }
}
