package com.financetracker.domain.usecase

import com.financetracker.domain.model.CategoryBreakdown
import com.financetracker.domain.repository.TransactionRepository
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMonthlySummaryUseCase @Inject constructor(private val transactionRepository: TransactionRepository) {
    data class MonthlySummary(
        val totalSpent: BigDecimal,
        val dailyAverage: BigDecimal,
        val transactionCount: Int,
        val categoryBreakdowns: List<CategoryBreakdown>
    )

    operator fun invoke(yearMonth: YearMonth): Flow<MonthlySummary> {
        val start = yearMonth.atDay(1)
        val end = yearMonth.atEndOfMonth()
        val daysInMonth = yearMonth.lengthOfMonth()

        return transactionRepository.getTransactionsByDateRange(start, end).map { transactions ->
            val totalSpent = transactions.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }
            val dailyAverage = if (daysInMonth > 0) totalSpent / BigDecimal(daysInMonth) else BigDecimal.ZERO

            val categoryGroups = transactions.groupBy { it.category }
            val breakdowns = categoryGroups.entries.mapIndexed { i, (cat, txns) ->
                CategoryBreakdown(
                    name = cat.name,
                    emoji = cat.emoji,
                    amount = txns.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount },
                    colorHex = CHART_COLORS[i % CHART_COLORS.size]
                )
            }

            MonthlySummary(totalSpent, dailyAverage, transactions.size, breakdowns)
        }
    }

    companion object {
        private val CHART_COLORS = listOf(
            "#FF1DBD8E",
            "#FFFF8F5C",
            "#FF9151B8",
            "#FFFF57B0",
            "#FFEEB72B",
            "#FFF24F4F",
            "#FF00D4C6",
            "#FF006ECF"
        )
    }
}
