package com.example.crop_recommendation.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val location: String, // e.g. "Vellore", "Chennai", "Coimbatore", "Madurai"
    val phone: String
)
