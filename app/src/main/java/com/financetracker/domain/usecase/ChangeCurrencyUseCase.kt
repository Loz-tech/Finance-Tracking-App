package com.financetracker.domain.usecase

import com.financetracker.domain.repository.ExchangeRateRepository
import com.financetracker.domain.repository.SettingsRepository
import java.math.BigDecimal
import javax.inject.Inject

class ChangeCurrencyUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val exchangeRateRepository: ExchangeRateRepository,
    private val recalculateTransactionsUseCase: RecalculateTransactionsUseCase
) {

    suspend operator fun invoke(
        currentCode: String,
        newCode: String,
        manualRateText: String,
        useManualRate: Boolean
    ): Result<String> {
        if (currentCode == newCode) {
            return Result.success(newCode)
        }

        val manualRate = if (useManualRate && manualRateText.isNotBlank()) {
            manualRateText.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO }
        } else {
            null
        }

        manualRate?.let { rate ->
            exchangeRateRepository.setManualRate(currentCode, newCode, rate)
        }

        val recalc = recalculateTransactionsUseCase(currentCode, newCode)
        if (recalc.isFailure) {
            return Result.failure(recalc.exceptionOrNull() ?: Exception("Recalculation failed"))
        }

        settingsRepository.setCurrencyCode(newCode)
        return Result.success(newCode)
    }
}
