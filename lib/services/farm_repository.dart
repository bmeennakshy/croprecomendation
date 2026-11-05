import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';
import '../models/marketplace_listing.dart';
import '../models/purchase_request.dart';
import '../models/crop_recommendation.dart';
import '../models/educational_article.dart';

class FarmRepository {
  final FirebaseFirestore? _firestore;
  bool useFirebase = false;

  FarmRepository() : _firestore = _tryGetFirestore();

  static FirebaseFirestore? _tryGetFirestore() {
    try {
      return FirebaseFirestore.instance;
    } catch (_) {
      return null;
    }
  }

  void setFirebaseUsage(bool enabled) {
    useFirebase = enabled;
  }

  final List<String> indiaLocations = [
    'Ludhiana, Punjab',
    'Amritsar, Punjab',
    'Pune, Maharashtra',
    'Nagpur, Maharashtra',
    'Nashik, Maharashtra',
    'Lucknow, Uttar Pradesh',
    'Varanasi, Uttar Pradesh',
    'Chennai, Tamil Nadu',
    'Coimbatore, Tamil Nadu',
    'Madurai, Tamil Nadu',
    'Vellore, Tamil Nadu',
    'Bengaluru, Karnataka',
    'Mysuru, Karnataka',
    'Ahmedabad, Gujarat',
    'Surat, Gujarat',
    'Indore, Madhya Pradesh',
    'Bhopal, Madhya Pradesh',
    'Kolkata, West Bengal',
    'Patna, Bihar',
    'Jaipur, Rajasthan',
    'Karnal, Haryana',
    'Hyderabad, Telangana',
    'Vijayawada, Andhra Pradesh',
    'Kochi, Kerala',
    'Bhubaneswar, Odisha',
    'Guwahati, Assam',
  ];

  final List<String> cropCategories = [
    'All',
    'Cereals',
    'Vegetables',
    'Fruits',
    'Spices',
    'Cash Crops',
    'Flowers',
  ];

  final List<String> educationalCategories = [
    'All',
    'Soil Guide',
    'Crop Guide',
    'Seasonal Farming',
    'Sustainable Farming',
    'Water Management',
  ];

  final List<MarketplaceListing> _seedListings = [
    MarketplaceListing(
      id: 'l1',
      farmerId: 'u1',
      farmerName: 'Murugan Farmer',
      cropName: 'Samba Rice (Paddy)',
      category: 'Cereals',
      quantityKg: 500.0,
      pricePerKg: 42.0,
      location: 'Vellore, Tamil Nadu',
      description: 'High quality organic Samba rice freshly harvested from Vellore delta.',
      timestamp: DateTime.now().millisecondsSinceEpoch - 86400000,
    ),
    MarketplaceListing(
      id: 'l2',
      farmerId: 'u1',
      farmerName: 'Murugan Farmer',
      cropName: 'Sugarcane',
      category: 'Cash Crops',
      quantityKg: 1200.0,
      pricePerKg: 3.5,
      location: 'Vellore, Tamil Nadu',
      description: 'Sweet high-sucrose sugarcane ready for jaggery or processing.',
      timestamp: DateTime.now().millisecondsSinceEpoch - 72000000,
    ),
    MarketplaceListing(
      id: 'l3',
      farmerId: 'u3',
      farmerName: 'Selvam Farm',
      cropName: 'Coimbatore Tomato',
      category: 'Vegetables',
      quantityKg: 250.0,
      pricePerKg: 28.0,
      location: 'Coimbatore, Tamil Nadu',
      description: 'Fresh farm-grade juicy red tomatoes packed securely.',
      timestamp: DateTime.now().millisecondsSinceEpoch - 50000000,
    ),
    MarketplaceListing(
      id: 'l4',
      farmerId: 'u4',
      farmerName: 'Meenakshi Agro',
      cropName: 'Madurai Malli (Jasmine)',
      category: 'Flowers',
      quantityKg: 50.0,
      pricePerKg: 350.0,
      location: 'Madurai, Tamil Nadu',
      description: 'Aromatic fresh Madurai jasmine flowers for vendors & exporters.',
      timestamp: DateTime.now().millisecondsSinceEpoch - 30000000,
    ),
    MarketplaceListing(
      id: 'l5',
      farmerId: 'u5',
      farmerName: 'Gurpreet Singh',
      cropName: 'Premium Sharbati Wheat',
      category: 'Cereals',
      quantityKg: 1000.0,
      pricePerKg: 32.0,
      location: 'Ludhiana, Punjab',
      description: 'Golden grains of Sharbati wheat grown in fertile alluvial plains of Punjab.',
      timestamp: DateTime.now().millisecondsSinceEpoch - 20000000,
    ),
    MarketplaceListing(
      id: 'l6',
      farmerId: 'u6',
      farmerName: 'Rajesh Patil',
      cropName: 'Nagpur Organic Oranges',
      category: 'Fruits',
      quantityKg: 400.0,
      pricePerKg: 60.0,
      location: 'Nagpur, Maharashtra',
      description: 'Juicy, vitamin-rich organic Nagpur mandarins direct from orchard.',
      timestamp: DateTime.now().millisecondsSinceEpoch - 10000000,
    ),
  ];

  final List<PurchaseRequest> _seedRequests = [
    PurchaseRequest(
      id: 'r1',
      listingId: 'l1',
      cropName: 'Samba Rice (Paddy)',
      vendorId: 'u2',
      vendorName: 'Kumar Vendor',
      farmerId: 'u1',
      quantityKg: 100.0,
      offeredPricePerKg: 40.0,
      status: RequestStatus.pending,
      timestamp: DateTime.now().millisecondsSinceEpoch - 3600000,
    ),
  ];

  final List<EducationalArticle> _seedArticles = [
    EducationalArticle(
      id: 'a1',
      title: 'Optimizing Soil pH and NPK for Maximum Yield',
      category: 'Soil Guide',
      summary: 'Learn how to balance nitrogen, phosphorus, potassium, and soil acidity for optimal crop growth across Indian soils.',
      content: 'Soil testing is the foundation of scientific farming. By maintaining soil pH between 6.0 and 7.5 and ensuring balanced NPK ratios, farmers can increase crop yield by up to 30% while reducing fertilizer wastage and chemical runoff.',
      author: 'Dr. R. Venkatraman, Agronomist',
      readTimeMin: 4,
      timestamp: DateTime.now().millisecondsSinceEpoch - 200000000,
    ),
    EducationalArticle(
      id: 'a2',
      title: 'Drip Irrigation Strategies for Water-Scarce Regions',
      category: 'Water Management',
      summary: 'Advanced drip irrigation methods to conserve groundwater in drought-prone agricultural belts.',
      content: 'Drip irrigation delivers water directly to the plant roots drop by drop, minimizing evaporation and weed growth. Combining solar pumps with drip systems ensures sustainable irrigation even during dry summer months.',
      author: 'Sunita Sharma, Water Resources Engineer',
      readTimeMin: 5,
      timestamp: DateTime.now().millisecondsSinceEpoch - 150000000,
    ),
    EducationalArticle(
      id: 'a3',
      title: 'Organic Pest Control & Bio-Pesticides',
      category: 'Sustainable Farming',
      summary: 'Protect your crops from pests naturally using neem oil, pheromone traps, and companion planting.',
      content: 'Chemical pesticide residues reduce export value and degrade soil microflora. Utilizing neem-based formulations and biological predators protects crops sustainably while retaining premium organic pricing in the B2B marketplace.',
      author: 'K. Nambiar, Organic Farming Advisor',
      readTimeMin: 3,
      timestamp: DateTime.now().millisecondsSinceEpoch - 100000000,
    ),
  ];

  Future<List<MarketplaceListing>> getListings() async {
    if (useFirebase && _firestore != null) {
      try {
        final snapshot = await _firestore.collection('listings').orderBy('timestamp', descending: true).get();
        if (snapshot.docs.isNotEmpty) {
          return snapshot.docs.map((doc) => MarketplaceListing.fromJson(doc.data())).toList();
        }
      } catch (_) {}
    }
    final prefs = await SharedPreferences.getInstance();
    final str = prefs.getString('mock_listings');
    if (str != null) {
      final List decoded = jsonDecode(str);
      return decoded.map((item) => MarketplaceListing.fromJson(item)).toList();
    }
    return _seedListings;
  }

  Future<void> saveListing(MarketplaceListing listing) async {
    if (useFirebase && _firestore != null) {
      try {
        await _firestore.collection('listings').doc(listing.id).set(listing.toJson());
        return;
      } catch (_) {}
    }
    final listings = await getListings();
    final index = listings.indexWhere((l) => l.id == listing.id);
    if (index >= 0) {
      listings[index] = listing;
    } else {
      listings.insert(0, listing);
    }
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('mock_listings', jsonEncode(listings.map((l) => l.toJson()).toList()));
  }

  Future<List<PurchaseRequest>> getPurchaseRequests({String? userId, bool isVendor = false}) async {
    if (useFirebase && _firestore != null) {
      try {
        Query query = _firestore.collection('purchase_requests');
        if (userId != null) {
          query = isVendor ? query.where('vendorId', isEqualTo: userId) : query.where('farmerId', isEqualTo: userId);
        }
        final snapshot = await query.get();
        if (snapshot.docs.isNotEmpty) {
          return snapshot.docs.map((doc) => PurchaseRequest.fromJson(doc.data() as Map<String, dynamic>)).toList();
        }
      } catch (_) {}
    }

    final prefs = await SharedPreferences.getInstance();
    final str = prefs.getString('mock_requests');
    List<PurchaseRequest> requests = _seedRequests;
    if (str != null) {
      final List decoded = jsonDecode(str);
      requests = decoded.map((item) => PurchaseRequest.fromJson(item)).toList();
    }
    if (userId != null) {
      return requests.where((r) => isVendor ? r.vendorId == userId : r.farmerId == userId).toList();
    }
    return requests;
  }

  Future<void> savePurchaseRequest(PurchaseRequest request) async {
    if (useFirebase && _firestore != null) {
      try {
        await _firestore.collection('purchase_requests').doc(request.id).set(request.toJson());
        return;
      } catch (_) {}
    }

    final requests = await getPurchaseRequests();
    final index = requests.indexWhere((r) => r.id == request.id);
    if (index >= 0) {
      requests[index] = request;
    } else {
      requests.insert(0, request);
    }
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('mock_requests', jsonEncode(requests.map((r) => r.toJson()).toList()));
  }

  Future<List<EducationalArticle>> getArticles() async {
    if (useFirebase && _firestore != null) {
      try {
        final snapshot = await _firestore.collection('articles').get();
        if (snapshot.docs.isNotEmpty) {
          return snapshot.docs.map((doc) => EducationalArticle.fromJson(doc.data())).toList();
        }
      } catch (_) {}
    }
    return _seedArticles;
  }

  CropRecommendation calculateRecommendation({
    required String farmerId,
    required String soilType,
    required double pH,
    required int nitrogen,
    required int phosphorus,
    required int potassium,
  }) {
    String cropName = 'Samba Rice (Paddy)';
    double confidence = 94.5;
    String advice = 'Optimal conditions for high yield. Apply nitrogen-rich fertilizer in split doses and ensure steady water logging.';

    if (soilType == 'Clayey' || nitrogen > 120) {
      cropName = 'Samba Rice (Paddy)';
      confidence = 96.2;
      advice = 'Clayey soil with high nitrogen is exceptionally well-suited for paddy cultivation. Maintain 5cm standing water during vegetative growth.';
    } else if (soilType == 'Alluvial' || potassium > 80) {
      cropName = 'Premium Sharbati Wheat';
      confidence = 95.8;
      advice = 'Alluvial soil with rich potassium promotes robust grain filling. Schedule first irrigation at crown root initiation (21 DAS).';
    } else if (soilType == 'Red' && pH >= 6.0 && pH <= 7.0) {
      cropName = 'Ragi (Finger Millet)';
      confidence = 92.4;
      advice = 'Red soil thrives with drought-resistant millets. Minimal irrigation required; excellent market demand among health-conscious vendors.';
    } else if (soilType == 'Black' || phosphorus > 60) {
      cropName = 'Cotton / Sugarcane';
      confidence = 93.1;
      advice = 'Black soil retains moisture efficiently, ideal for deep-rooted cash crops like cotton and sugarcane. Monitor for bollworms.';
    } else if (pH < 5.5) {
      cropName = 'Tea / Potato (Acidic Tolerant)';
      confidence = 89.5;
      advice = 'Soil is strongly acidic. Consider adding agricultural lime to correct pH or cultivate acid-tolerant crops like potato.';
    } else {
      cropName = 'Maize (Corn)';
      confidence = 91.0;
      advice = 'Versatile soil profile supports hybrid maize. Ensure good drainage and weeding during initial 30 days.';
    }

    return CropRecommendation(
      id: 'rec_${DateTime.now().millisecondsSinceEpoch}',
      farmerId: farmerId,
      cropName: cropName,
      confidence: confidence,
      soilType: soilType,
      pH: pH,
      nitrogen: nitrogen,
      phosphorus: phosphorus,
      potassium: potassium,
      advice: advice,
      timestamp: DateTime.now().millisecondsSinceEpoch,
    );
  }
}
