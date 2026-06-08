package com.financetracker.domain.usecase

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.Currency
import javax.inject.Inject

class ConvertAmountUseCase @Inject constructor() {

    operator fun invoke(
        amount: BigDecimal,
        fromCurrency: String,
        toCurrency: String,
        rates: Map<String, BigDecimal>,
        manualRate: BigDecimal? = null
    ): Result<BigDecimal> {
        if (fromCurrency == toCurrency) {
            return Result.success(amount.setScale(targetFractionDigits(toCurrency), RoundingMode.HALF_UP))
        }

        if (manualRate != null) {
            val result = amount.multiply(manualRate)
            return Result.success(result.setScale(targetFractionDigits(toCurrency), RoundingMode.HALF_UP))
        }

        val fromRate = rates[fromCurrency]
        val toRate = rates[toCurrency]
        if (fromRate == null) {
            return Result.failure(IllegalStateException("Missing rate for currency: $fromCurrency"))
        }
        if (toRate == null) {
            return Result.failure(IllegalStateException("Missing rate for currency: $toCurrency"))
        }

        val crossRate = toRate.divide(fromRate, MathContext.DECIMAL128)
        val result = amount.multiply(crossRate)

        return Result.success(result.setScale(targetFractionDigits(toCurrency), RoundingMode.HALF_UP))
    }

    private fun targetFractionDigits(currencyCode: String): Int = try {
        Currency.getInstance(currencyCode).defaultFractionDigits
    } catch (_: IllegalArgumentException) {
        2
    }
}
