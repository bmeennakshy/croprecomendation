package com.example.crop_recommendation.model

import kotlinx.serialization.Serializable

@Serializable
data class CropRecommendation(
    val id: String,
    val farmerId: String,
    val cropName: String,
    val confidence: Float,
    val soilType: String,
    val pH: Float,
    val nitrogen: Int,
    val phosphorus: Int,
    val potassium: Int,
    val advice: String,
    val timestamp: Long = System.currentTimeMillis()
)
