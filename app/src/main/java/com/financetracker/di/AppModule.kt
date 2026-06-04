package com.financetracker.di

import android.content.Context
import androidx.room.Room
import com.financetracker.data.local.db.AppDatabase
import com.financetracker.data.local.db.BudgetDao
import com.financetracker.data.local.db.CategoryDao
import com.financetracker.data.local.db.ExchangeRateDao
import com.financetracker.data.local.db.MIGRATION_2_3
import com.financetracker.data.local.db.TransactionDao
import com.financetracker.data.remote.api.ExchangeRateApi
import com.financetracker.data.remote.api.FrankfurterApi
import com.financetracker.data.repository.BudgetRepositoryImpl
import com.financetracker.data.repository.CategoryRepositoryImpl
import com.financetracker.data.repository.ExchangeRateRepositoryImpl
import com.financetracker.data.repository.SettingsRepositoryImpl
import com.financetracker.data.repository.TransactionRepositoryImpl
import com.financetracker.domain.repository.BudgetRepository
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.ExchangeRateRepository
import com.financetracker.domain.repository.SettingsRepository
import com.financetracker.domain.repository.TransactionRepository
import com.financetracker.domain.usecase.CalculateBudgetProgressUseCase
import com.financetracker.domain.usecase.ConvertAmountUseCase
import com.financetracker.domain.usecase.GetMonthlySummaryUseCase
import com.financetracker.domain.usecase.SearchTransactionsUseCase
import com.financetracker.domain.util.SystemTimeProvider
import com.financetracker.domain.util.TimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "finance_tracker.db"
    ).addMigrations(MIGRATION_2_3).build()

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideBudgetDao(database: AppDatabase): BudgetDao = database.budgetDao()

    @Provides
    fun provideExchangeRateDao(database: AppDatabase): ExchangeRateDao = database.exchangeRateDao()

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

    @Provides
    @Singleton
    fun provideExchangeRateRepository(impl: ExchangeRateRepositoryImpl): ExchangeRateRepository = impl

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider = SystemTimeProvider()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        return OkHttpClient.Builder().addInterceptor(logging).build()
    }

    @Provides
    @Singleton
    fun provideFrankfurterApi(client: OkHttpClient): FrankfurterApi {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("https://api.frankfurter.dev/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(FrankfurterApi::class.java)
    }

    @Provides
    @Singleton
    fun provideExchangeRateApi(client: OkHttpClient): ExchangeRateApi {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("https://open.er-api.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ExchangeRateApi::class.java)
    }

    @Provides
    @Singleton
    fun provideConvertAmountUseCase(): ConvertAmountUseCase = ConvertAmountUseCase()

    @Provides
    @Singleton
    fun provideSearchTransactionsUseCase(
        transactionRepository: TransactionRepository,
        timeProvider: TimeProvider
    ): SearchTransactionsUseCase = SearchTransactionsUseCase(transactionRepository, timeProvider)

    @Provides
    @Singleton
    fun provideGetMonthlySummaryUseCase(transactionRepository: TransactionRepository): GetMonthlySummaryUseCase =
        GetMonthlySummaryUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideCalculateBudgetProgressUseCase(
        budgetRepository: BudgetRepository,
        transactionRepository: TransactionRepository,
        categoryRepository: CategoryRepository,
        timeProvider: TimeProvider
    ): CalculateBudgetProgressUseCase = CalculateBudgetProgressUseCase(
        budgetRepository,
        transactionRepository,
        categoryRepository,
        timeProvider
    )
}
