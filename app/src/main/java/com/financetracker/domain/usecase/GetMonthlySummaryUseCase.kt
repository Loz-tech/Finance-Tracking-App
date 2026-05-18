package com.financetracker.domain.usecase

import com.financetracker.domain.repository.TransactionRepository
import com.financetracker.ui.components.DonutSegment
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetMonthlySummaryUseCase @Inject constructor(private val transactionRepository: TransactionRepository) {
    data class MonthlySummary(
        val totalSpent: BigDecimal,
        val dailyAverage: BigDecimal,
        val transactionCount: Int,
        val categorySegments: List<DonutSegment>
    )

    suspend operator fun invoke(yearMonth: YearMonth): Flow<MonthlySummary> {
        val start = yearMonth.atDay(1)
        val end = yearMonth.atEndOfMonth()

        return transactionRepository.getTransactionsByDateRange(start, end).combine(
            kotlinx.coroutines.flow.flowOf(Unit)
        ) { transactions, _ ->
            val totalSpent = transactions.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }
            val daysInMonth = yearMonth.lengthOfMonth()
            val dailyAverage = if (daysInMonth > 0) totalSpent / BigDecimal(daysInMonth) else BigDecimal.ZERO

            val colors = listOf(
                0xFF006874,
                0xFF496364,
                0xFF634186,
                0xFFBA1A1A,
                0xFF8B4A00,
                0xFF006E28,
                0xFF90416A,
                0xFF005CBB
            )
                .map { androidx.compose.ui.graphics.Color(it) }
            val categoryGroups = transactions.groupBy { it.category }
            val segments = categoryGroups.entries.mapIndexed { i, (cat, txns) ->
                DonutSegment(
                    cat.name,
                    cat.emoji,
                    txns.sumOf {
                        it.amount.toDouble()
                    }.toFloat(),
                    colors[i % colors.size]
                )
            }

            MonthlySummary(totalSpent, dailyAverage, transactions.size, segments)
        }
    }
}
