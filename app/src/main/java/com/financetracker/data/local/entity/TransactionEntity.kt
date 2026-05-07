package com.financetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val amount: BigDecimal,
    val note: String = "",
    val date: LocalDate,
    val categoryId: UUID,
    val createdAt: Instant = Instant.now()
)
