package com.example.crop_recommendation.model

import kotlinx.serialization.Serializable

@Serializable
data class EducationalArticle(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val content: String,
    val author: String,
    val imageUrl: String = "",
    val readTimeMin: Int = 3,
    val timestamp: Long = System.currentTimeMillis()
)
