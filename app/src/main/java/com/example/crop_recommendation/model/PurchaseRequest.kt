package com.example.crop_recommendation.model

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseRequest(
    val id: String,
    val listingId: String,
    val cropName: String,
    val vendorId: String,
    val vendorName: String,
    val farmerId: String,
    val quantityKg: Double,
    val offeredPricePerKg: Double,
    val status: RequestStatus = RequestStatus.PENDING,
    val counterOfferPricePerKg: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)
