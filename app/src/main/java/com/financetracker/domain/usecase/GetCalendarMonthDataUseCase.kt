package com.financetracker.domain.usecase

import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.TransactionRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.first

data class CalendarMonthData(
    val dayTotals: Map<LocalDate, Double>,
    val transactionsByDate: Map<LocalDate, List<Transaction>>,
    val monthMax: Double
)

class GetCalendarMonthDataUseCase @Inject constructor(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(yearMonth: YearMonth): CalendarMonthData {
        val start = yearMonth.atDay(1)
        val end = yearMonth.atEndOfMonth()

        val dailyTotals = transactionRepository.getDailyTotals(start, end)
        val monthTransactions = transactionRepository.getTransactionsByDateRange(start, end)
            .first()
        val transactionsByDate = monthTransactions.groupBy { it.date }
        val monthMax = dailyTotals.values.maxOrNull() ?: 1.0

        return CalendarMonthData(
            dayTotals = dailyTotals,
            transactionsByDate = transactionsByDate,
            monthMax = monthMax
        )
    }
}
