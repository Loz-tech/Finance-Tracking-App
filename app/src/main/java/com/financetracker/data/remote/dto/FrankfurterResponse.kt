package com.financetracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FrankfurterResponse(
    val amount: Double = 1.0,
    val base: String,
    val date: String,
    @SerialName("rates")
    val rates: Map<String, Double>
)
