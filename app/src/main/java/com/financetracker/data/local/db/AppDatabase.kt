package com.financetracker.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.financetracker.data.local.entity.BudgetEntity
import com.financetracker.data.local.entity.CategoryEntity
import com.financetracker.data.local.entity.ExchangeRateEntity
import com.financetracker.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        ExchangeRateEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    abstract fun categoryDao(): CategoryDao

    abstract fun budgetDao(): BudgetDao

    abstract fun exchangeRateDao(): ExchangeRateDao
}
