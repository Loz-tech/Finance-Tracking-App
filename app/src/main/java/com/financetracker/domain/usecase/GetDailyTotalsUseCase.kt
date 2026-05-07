package com.financetracker.domain.usecase

import com.financetracker.domain.repository.TransactionRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class GetDailyTotalsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(yearMonth: YearMonth): Map<LocalDate, Double> {
        val start = yearMonth.atDay(1)
        val end = yearMonth.atEndOfMonth()
        return transactionRepository.getDailyTotals(start, end)
    }
}
