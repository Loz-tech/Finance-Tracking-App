package com.financetracker.domain.repository

import com.financetracker.domain.model.ExchangeRate
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow

interface ExchangeRateRepository {
    fun getRate(baseCode: String, targetCode: String): Flow<ExchangeRate?>

    suspend fun refreshRates(baseCode: String): Result<Map<String, BigDecimal>>

    suspend fun setManualRate(baseCode: String, targetCode: String, rate: BigDecimal)

    suspend fun clearManualRate(baseCode: String, targetCode: String)

    suspend fun getManualRate(baseCode: String, targetCode: String): BigDecimal?
}
