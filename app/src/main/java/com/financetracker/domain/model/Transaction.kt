package com.financetracker.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class Transaction(
    val id: UUID = UUID.randomUUID(),
    val amount: BigDecimal,
    val originalAmount: BigDecimal = amount,
    val originalCurrencyCode: String = "USD",
    val note: String = "",
    val date: LocalDate = LocalDate.now(),
    val category: Category,
    val createdAt: Long = System.currentTimeMillis()
)
