package com.financetracker.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create exchange_rates table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS exchange_rates (
                baseCode TEXT NOT NULL,
                targetCode TEXT NOT NULL,
                rate TEXT NOT NULL,
                source TEXT NOT NULL,
                fetchedAt INTEGER NOT NULL,
                PRIMARY KEY(baseCode, targetCode)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_exchange_rates_baseCode ON exchange_rates(baseCode)")

        // Add originalAmount and originalCurrencyCode to transactions
        db.execSQL("ALTER TABLE transactions ADD COLUMN originalAmount TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE transactions ADD COLUMN originalCurrencyCode TEXT NOT NULL DEFAULT 'USD'")
        db.execSQL("UPDATE transactions SET originalAmount = amount, originalCurrencyCode = 'USD'")

        // Add originalLimitAmount and originalCurrencyCode to budgets
        db.execSQL("ALTER TABLE budgets ADD COLUMN originalLimitAmount TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE budgets ADD COLUMN originalCurrencyCode TEXT NOT NULL DEFAULT 'USD'")
        db.execSQL("UPDATE budgets SET originalLimitAmount = limitAmount, originalCurrencyCode = 'USD'")
    }
}
