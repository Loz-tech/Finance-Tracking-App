package com.financetracker.domain.model

import java.math.BigDecimal
import java.time.Instant

enum class RateSource {
    API,
    MANUAL
}

data class ExchangeRate(
    val baseCode: String,
    val targetCode: String,
    val rate: BigDecimal,
    val source: RateSource,
    val fetchedAt: Instant
)
