package com.financetracker.domain.usecase

import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Pure-Kotlin state holder for month navigation. Each month-scoped ViewModel owns its own
 * instance (History and Calendar navigate independently). Drives data loading via Flow
 * operators (e.g. flatMapLatest) rather than imperative Job cancellation.
 *
 * Not provided by Hilt: ViewModels instantiate directly. Because ViewModels survive config
 * changes, the navigator state is preserved across rotation.
 */
class MonthNavigatorController(initial: YearMonth = YearMonth.now()) {

    private val _yearMonth: MutableStateFlow<YearMonth> = MutableStateFlow(initial)
    val yearMonth: StateFlow<YearMonth> = _yearMonth.asStateFlow()

    fun previous() {
        _yearMonth.value = _yearMonth.value.minusMonths(1)
    }

    fun next() {
        _yearMonth.value = _yearMonth.value.plusMonths(1)
    }

    fun set(yearMonth: YearMonth) {
        _yearMonth.value = yearMonth
    }
}
