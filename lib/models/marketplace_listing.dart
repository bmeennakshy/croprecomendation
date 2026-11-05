class MarketplaceListing {
  final String id;
  final String farmerId;
  final String farmerName;
  final String cropName;
  final String category; // Cereals, Vegetables, Fruits, Spices, Cash Crops, Flowers
  final double quantityKg;
  final double pricePerKg;
  final String location;
  final String description;
  final String imageUrl;
  final bool active;
  final int timestamp;

  MarketplaceListing({
    required this.id,
    required this.farmerId,
    required this.farmerName,
    required this.cropName,
    required this.category,
    required this.quantityKg,
    required this.pricePerKg,
    required this.location,
    required this.description,
    this.imageUrl = '',
    this.active = true,
    required this.timestamp,
  });

  Map<String, dynamic> toJson() => {
        'id': id,
        'farmerId': farmerId,
        'farmerName': farmerName,
        'cropName': cropName,
        'category': category,
        'quantityKg': quantityKg,
        'pricePerKg': pricePerKg,
        'location': location,
        'description': description,
        'imageUrl': imageUrl,
        'active': active,
        'timestamp': timestamp,
      };

  factory MarketplaceListing.fromJson(Map<String, dynamic> json) => MarketplaceListing(
        id: json['id'] ?? '',
        farmerId: json['farmerId'] ?? '',
        farmerName: json['farmerName'] ?? '',
        cropName: json['cropName'] ?? '',
        category: json['category'] ?? 'Cereals',
        quantityKg: (json['quantityKg'] ?? 0.0).toDouble(),
        pricePerKg: (json['pricePerKg'] ?? 0.0).toDouble(),
        location: json['location'] ?? '',
        description: json['description'] ?? '',
        imageUrl: json['imageUrl'] ?? '',
        active: json['active'] ?? true,
        timestamp: json['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
      );
}
