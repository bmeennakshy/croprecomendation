package com.example.crop_recommendation.model

import kotlinx.serialization.Serializable

@Serializable
data class CropRecommendationDetail(
    val id: String,
    val cropName: String,
    val category: String, // Cereals, Vegetables, Fruits, Spices, Cash Crops, Flowers
    val suitabilityScore: Int, // 0 to 100
    val soilTypes: List<String>,
    val preferredTempMin: Double,
    val preferredTempMax: Double,
    val preferredRainfallMin: Double,
    val preferredRainfallMax: Double,
    val season: String, // Kharif, Rabi, Zaid, All-Season
    val waterRequirements: String, // Low, Moderate, High
    val growingDurationDays: Int,
    val rationale: String,
    val fertilizerAdvice: String,
    val isFavorite: Boolean = false
)
