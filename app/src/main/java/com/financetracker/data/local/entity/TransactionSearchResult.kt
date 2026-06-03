package com.financetracker.data.local.entity

import androidx.room.ColumnInfo
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

/**
 * Projection result from JOIN between transactions and categories.
 * Room maps this from a @Query that SELECTs both tables' columns.
 */
data class TransactionSearchResult(
    val id: UUID,
    val amount: BigDecimal,
    val originalAmount: BigDecimal,
    val originalCurrencyCode: String,
    val note: String,
    val date: LocalDate,
    val categoryId: UUID,
    val createdAt: Instant,
    @ColumnInfo(name = "categoryName") val categoryName: String,
    @ColumnInfo(name = "categoryIconName") val categoryIconName: String,
    @ColumnInfo(name = "categoryColorHex") val categoryColorHex: String?
)
