package com.example.crop_recommendation.model

import kotlinx.serialization.Serializable

@Serializable
enum class RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    COUNTEROFFER_RECEIVED,
    COMPLETED,
    CANCELLED
}
