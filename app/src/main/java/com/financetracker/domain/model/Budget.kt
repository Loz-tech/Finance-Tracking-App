package com.financetracker.domain.model

import java.math.BigDecimal
import java.util.UUID

data class Budget(
    val id: UUID = UUID.randomUUID(),
    val categoryId: UUID? = null,
    val yearMonth: String,
    val limitAmount: BigDecimal,
    val originalLimitAmount: BigDecimal = limitAmount,
    val originalCurrencyCode: String = "USD"
)
