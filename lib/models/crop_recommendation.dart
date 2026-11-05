class CropRecommendation {
  final String id;
  final String farmerId;
  final String cropName;
  final double confidence;
  final String soilType;
  final double pH;
  final int nitrogen;
  final int phosphorus;
  final int potassium;
  final String advice;
  final int timestamp;

  CropRecommendation({
    required this.id,
    required this.farmerId,
    required this.cropName,
    required this.confidence,
    required this.soilType,
    required this.pH,
    required this.nitrogen,
    required this.phosphorus,
    required this.potassium,
    required this.advice,
    required this.timestamp,
  });

  Map<String, dynamic> toJson() => {
        'id': id,
        'farmerId': farmerId,
        'cropName': cropName,
        'confidence': confidence,
        'soilType': soilType,
        'pH': pH,
        'nitrogen': nitrogen,
        'phosphorus': phosphorus,
        'potassium': potassium,
        'advice': advice,
        'timestamp': timestamp,
      };

  factory CropRecommendation.fromJson(Map<String, dynamic> json) => CropRecommendation(
        id: json['id'] ?? '',
        farmerId: json['farmerId'] ?? '',
        cropName: json['cropName'] ?? '',
        confidence: (json['confidence'] ?? 0.0).toDouble(),
        soilType: json['soilType'] ?? '',
        pH: (json['pH'] ?? 7.0).toDouble(),
        nitrogen: json['nitrogen'] ?? 0,
        phosphorus: json['phosphorus'] ?? 0,
        potassium: json['potassium'] ?? 0,
        advice: json['advice'] ?? '',
        timestamp: json['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
      );
}
