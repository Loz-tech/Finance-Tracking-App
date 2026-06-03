package com.financetracker.data.repository

import com.financetracker.data.local.db.ExchangeRateDao
import com.financetracker.data.local.entity.ExchangeRateEntity
import com.financetracker.data.remote.api.ExchangeRateApi
import com.financetracker.data.remote.api.FrankfurterApi
import com.financetracker.domain.model.ExchangeRate
import com.financetracker.domain.model.RateSource
import com.financetracker.domain.repository.ExchangeRateRepository
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class ExchangeRateRepositoryImpl @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao,
    private val frankfurterApi: FrankfurterApi,
    private val exchangeRateApi: ExchangeRateApi
) : ExchangeRateRepository {

    override fun getRate(baseCode: String, targetCode: String): Flow<ExchangeRate?> = flow {
        if (baseCode == targetCode) {
            emit(
                ExchangeRate(
                    baseCode = baseCode,
                    targetCode = targetCode,
                    rate = BigDecimal.ONE,
                    source = RateSource.API,
                    fetchedAt = Instant.now()
                )
            )
            return@flow
        }

        val cached = exchangeRateDao.getRate(baseCode, targetCode)
        if (cached != null) {
            emit(
                ExchangeRate(
                    baseCode = cached.baseCode,
                    targetCode = cached.targetCode,
                    rate = cached.rate,
                    source = RateSource.valueOf(cached.source),
                    fetchedAt = cached.fetchedAt
                )
            )
            return@flow
        }

        emit(null)
    }

    override suspend fun refreshRates(baseCode: String): Result<Map<String, BigDecimal>> {
        val supportedCurrencies = listOf("USD", "EUR", "GBP", "JPY", "CNY")
        val targets = supportedCurrencies.filter { it != baseCode }.joinToString(",")

        val frankfurterResult = try {
            val response = frankfurterApi.getLatestRates(baseCode, targets)
            if (response.isSuccessful) {
                response.body()?.rates?.mapValues { BigDecimal.valueOf(it.value) }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }

        val rates = if (frankfurterResult != null) {
            frankfurterResult
        } else {
            try {
                val response = exchangeRateApi.getLatestRates(baseCode)
                if (response.isSuccessful) {
                    response.body()?.rates
                        ?.filterKeys { it in supportedCurrencies }
                        ?.mapValues { BigDecimal.valueOf(it.value) }
                } else {
                    null
                }
            } catch (_: Exception) {
                null
            }
        }

        if (rates == null) {
            return Result.failure(Exception("Failed to fetch rates from all sources"))
        }

        val now = Instant.now()
        rates.forEach { (target, rate) ->
            val existing = exchangeRateDao.getRate(baseCode, target)
            if (existing == null || existing.source != RateSource.MANUAL.name) {
                exchangeRateDao.upsert(
                    ExchangeRateEntity(
                        baseCode = baseCode,
                        targetCode = target,
                        rate = rate,
                        source = RateSource.API.name,
                        fetchedAt = now
                    )
                )
            }
        }

        exchangeRateDao.deleteOldApiRates(Instant.now().minusSeconds(86400))

        return Result.success(rates + (baseCode to BigDecimal.ONE))
    }

    override suspend fun setManualRate(baseCode: String, targetCode: String, rate: BigDecimal) {
        exchangeRateDao.deleteManualRate(baseCode, targetCode)
        exchangeRateDao.upsert(
            ExchangeRateEntity(
                baseCode = baseCode,
                targetCode = targetCode,
                rate = rate,
                source = RateSource.MANUAL.name,
                fetchedAt = Instant.now()
            )
        )
    }

    override suspend fun clearManualRate(baseCode: String, targetCode: String) {
        exchangeRateDao.deleteManualRate(baseCode, targetCode)
    }

    override suspend fun getManualRate(baseCode: String, targetCode: String): BigDecimal? {
        val entity = exchangeRateDao.getRate(baseCode, targetCode)
        return if (entity != null && entity.source == RateSource.MANUAL.name) entity.rate else null
    }
}
