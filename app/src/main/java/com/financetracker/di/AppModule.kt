package com.financetracker.di

import android.content.Context
import androidx.room.Room
import com.financetracker.data.local.db.AppDatabase
import com.financetracker.data.local.db.BudgetDao
import com.financetracker.data.local.db.CategoryDao
import com.financetracker.data.local.db.TransactionDao
import com.financetracker.data.repository.BudgetRepositoryImpl
import com.financetracker.data.repository.CategoryRepositoryImpl
import com.financetracker.data.repository.SettingsRepositoryImpl
import com.financetracker.data.repository.TransactionRepositoryImpl
import com.financetracker.domain.repository.BudgetRepository
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.SettingsRepository
import com.financetracker.domain.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "finance_tracker.db"
    ).build()

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideBudgetDao(database: AppDatabase): BudgetDao = database.budgetDao()

    @Provides
    @Singleton
    fun provideTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository = impl

    @Provides
    @Singleton
    fun provideCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository = impl

    @Provides
    @Singleton
    fun provideBudgetRepository(impl: BudgetRepositoryImpl): BudgetRepository = impl

    @Provides
    @Singleton
    fun provideSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository = impl
}
