package com.financetracker.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.financetracker.data.local.entity.ExchangeRateEntity
import java.time.Instant

@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates WHERE baseCode = :baseCode AND targetCode = :targetCode")
    suspend fun getRate(baseCode: String, targetCode: String): ExchangeRateEntity?

    @Query("SELECT * FROM exchange_rates WHERE baseCode = :baseCode")
    suspend fun getAllForBase(baseCode: String): List<ExchangeRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rate: ExchangeRateEntity)

    @Query("DELETE FROM exchange_rates WHERE fetchedAt < :timestamp AND source = 'API'")
    suspend fun deleteOldApiRates(timestamp: Instant)

    @Query("DELETE FROM exchange_rates WHERE baseCode = :baseCode AND targetCode = :targetCode AND source = 'MANUAL'")
    suspend fun deleteManualRate(baseCode: String, targetCode: String)
}
