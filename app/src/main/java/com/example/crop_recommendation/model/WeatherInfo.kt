package com.example.crop_recommendation.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherInfo(
    val location: String,
    val temperatureC: Double,
    val humidityPercent: Int,
    val rainfallMm: Double,
    val condition: String, // e.g. "Sunny", "Partly Cloudy", "Monsoon Showers"
    val windSpeedKmph: Double,
    val soilMoisture: String // e.g. "Optimal", "Dry", "High"
)
