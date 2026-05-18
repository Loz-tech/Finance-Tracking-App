package com.financetracker.data.export

import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

object TransactionFixtures {
    fun cat(name: String = "Food", emoji: String = "🍔") = Category(
        id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        name = name,
        emoji = emoji,
        colorHex = "#FF5722",
        isDefault = false,
        sortOrder = 0
    )

    fun txn(
        amount: String,
        note: String = "",
        date: LocalDate = LocalDate.of(2026, 5, 18),
        category: Category = cat()
    ) = Transaction(
        id = UUID.fromString("00000000-0000-0000-0000-000000000000"),
        amount = BigDecimal(amount),
        note = note,
        date = date,
        category = category,
        createdAt = 0L
    )
}
