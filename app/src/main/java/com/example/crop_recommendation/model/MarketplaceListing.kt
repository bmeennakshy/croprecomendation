package com.example.crop_recommendation.model

import kotlinx.serialization.Serializable

@Serializable
data class MarketplaceListing(
    val id: String,
    val farmerId: String,
    val farmerName: String,
    val cropName: String,
    val category: String = "Cereals", // Cereals, Vegetables, Fruits, Spices, Cash Crops, Flowers
    val quantityKg: Double,
    val pricePerKg: Double,
    val location: String,
    val description: String,
    val imageUrl: String = "",
    val active: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
