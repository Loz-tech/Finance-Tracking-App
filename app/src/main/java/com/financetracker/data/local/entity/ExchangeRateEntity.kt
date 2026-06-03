package com.financetracker.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import java.math.BigDecimal
import java.time.Instant

@Entity(
    tableName = "exchange_rates",
    primaryKeys = ["baseCode", "targetCode"],
    indices = [Index(value = ["baseCode"])]
)
data class ExchangeRateEntity(
    val baseCode: String,
    val targetCode: String,
    val rate: BigDecimal,
    val source: String,
    val fetchedAt: Instant
)
