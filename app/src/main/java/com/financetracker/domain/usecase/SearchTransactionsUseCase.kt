package com.financetracker.domain.usecase

import com.financetracker.domain.model.DateFilter
import com.financetracker.domain.model.SearchCriteria
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.TransactionRepository
import com.financetracker.domain.util.TimeProvider
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class SearchTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val timeProvider: TimeProvider
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(criteria: Flow<SearchCriteria>): Flow<List<Transaction>> = criteria.flatMapLatest { c ->
        if (c.query.isBlank() && c.categoryIds.isEmpty() && c.dateFilter is DateFilter.None) {
            flowOf(emptyList())
        } else {
            val (start, end) = when (val df = c.dateFilter) {
                is DateFilter.None -> null to null
                is DateFilter.Quick -> {
                    val range = df.chip.calculateRange(timeProvider.today())
                    range.first to range.second
                }
                is DateFilter.Custom -> df.start to df.end
            }
            transactionRepository.searchTransactions(
                query = c.query.trim(),
                categoryIds = c.categoryIds.toList(),
                start = start,
                end = end
            )
        }
    }
}
