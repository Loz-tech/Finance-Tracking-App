package com.financetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.UUID

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val categoryId: UUID? = null,
    val yearMonth: String,
    val limitAmount: BigDecimal,
    val originalLimitAmount: BigDecimal,
    val originalCurrencyCode: String
)
