enum RequestStatus { pending, accepted, rejected, countered, completed }

class PurchaseRequest {
  final String id;
  final String listingId;
  final String cropName;
  final String vendorId;
  final String vendorName;
  final String farmerId;
  final double quantityKg;
  final double offeredPricePerKg;
  final RequestStatus status;
  final double? counterOfferPricePerKg;
  final int timestamp;

  PurchaseRequest({
    required this.id,
    required this.listingId,
    required this.cropName,
    required this.vendorId,
    required this.vendorName,
    required this.farmerId,
    required this.quantityKg,
    required this.offeredPricePerKg,
    this.status = RequestStatus.pending,
    this.counterOfferPricePerKg,
    required this.timestamp,
  });

  Map<String, dynamic> toJson() => {
        'id': id,
        'listingId': listingId,
        'cropName': cropName,
        'vendorId': vendorId,
        'vendorName': vendorName,
        'farmerId': farmerId,
        'quantityKg': quantityKg,
        'offeredPricePerKg': offeredPricePerKg,
        'status': status.name,
        'counterOfferPricePerKg': counterOfferPricePerKg,
        'timestamp': timestamp,
      };

  factory PurchaseRequest.fromJson(Map<String, dynamic> json) {
    RequestStatus parseStatus(String? statusStr) {
      switch (statusStr?.toLowerCase()) {
        case 'accepted':
          return RequestStatus.accepted;
        case 'rejected':
          return RequestStatus.rejected;
        case 'countered':
          return RequestStatus.countered;
        case 'completed':
          return RequestStatus.completed;
        default:
          return RequestStatus.pending;
      }
    }

    return PurchaseRequest(
      id: json['id'] ?? '',
      listingId: json['listingId'] ?? '',
      cropName: json['cropName'] ?? '',
      vendorId: json['vendorId'] ?? '',
      vendorName: json['vendorName'] ?? '',
      farmerId: json['farmerId'] ?? '',
      quantityKg: (json['quantityKg'] ?? 0.0).toDouble(),
      offeredPricePerKg: (json['offeredPricePerKg'] ?? 0.0).toDouble(),
      status: parseStatus(json['status']),
      counterOfferPricePerKg: json['counterOfferPricePerKg'] != null
          ? (json['counterOfferPricePerKg'] as num).toDouble()
          : null,
      timestamp: json['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
    );
  }
}
