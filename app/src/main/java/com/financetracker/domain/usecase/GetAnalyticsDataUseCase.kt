package com.financetracker.domain.usecase

import com.financetracker.domain.model.CategoryBreakdown
import com.financetracker.domain.model.Period
import com.financetracker.domain.repository.TransactionRepository
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class AnalyticsData(
    val totalSpent: BigDecimal,
    val dailyAverage: BigDecimal,
    val transactionCount: Int,
    val categoryBreakdowns: List<CategoryBreakdown>,
    val weekdayAverages: Map<DayOfWeek, Double>
)

class GetAnalyticsDataUseCase @Inject constructor(private val transactionRepository: TransactionRepository) {
    operator fun invoke(period: Period): Flow<AnalyticsData> {
        val today = LocalDate.now()
        val (start, end) = when (period) {
            Period.WEEK -> today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) to today
            Period.MONTH -> today.withDayOfMonth(1) to today.withDayOfMonth(today.lengthOfMonth())
            Period.YEAR -> today.withDayOfYear(1) to today.withDayOfYear(today.lengthOfYear())
        }
        val daysSpan = if (end > start) (end.toEpochDay() - start.toEpochDay()).toInt() + 1 else 1

        return transactionRepository.getTransactionsByDateRange(start, end).map { transactions ->
            val totalSpent = transactions.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }
            val dailyAverage = totalSpent / BigDecimal(daysSpan)

            val categoryGroups = transactions.groupBy { it.category }
            val categoryBreakdowns = categoryGroups.entries.map { (cat, txns) ->
                CategoryBreakdown(
                    name = cat.name,
                    iconName = cat.iconName,
                    amount = txns.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount },
                    colorHex = cat.colorHex
                )
            }

            val weekdayAverages = DayOfWeek.entries.associateWith { day ->
                val dayTotal = transactions
                    .filter { it.date.dayOfWeek == day }
                    .sumOf { it.amount.toDouble() }
                val count = (start.toEpochDay()..end.toEpochDay()).count {
                    LocalDate.ofEpochDay(it).dayOfWeek == day
                }
                if (count > 0) dayTotal / count else 0.0
            }

            AnalyticsData(
                totalSpent = totalSpent,
                dailyAverage = dailyAverage,
                transactionCount = transactions.size,
                categoryBreakdowns = categoryBreakdowns,
                weekdayAverages = weekdayAverages
            )
        }
    }
}
