package com.financetracker.domain.usecase

import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.TransactionRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class HistoryData(val dateGroups: Map<LocalDate, List<Transaction>>)

class GetHistoryForMonthUseCase @Inject constructor(private val transactionRepository: TransactionRepository) {
    operator fun invoke(yearMonth: YearMonth): Flow<HistoryData> {
        val monthStart = yearMonth.atDay(1)
        val monthEnd = yearMonth.atEndOfMonth()

        return transactionRepository.getTransactionsByDateRange(monthStart, monthEnd).map { allTransactions ->
            val monthTransactions = allTransactions
                .sortedWith(compareByDescending<Transaction> { it.date }.thenByDescending { it.createdAt })

            val dateGroups = monthTransactions.groupBy { it.date }
            HistoryData(dateGroups)
        }
    }
}
