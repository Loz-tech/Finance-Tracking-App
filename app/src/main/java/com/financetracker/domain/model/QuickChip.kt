package com.financetracker.domain.model

import java.time.LocalDate

enum class QuickChip(val label: String) {
    TODAY("Today"),
    LAST_7_DAYS("Last 7 Days"),
    LAST_30_DAYS("Last 30 Days"),
    THIS_MONTH("This Month"),
    THIS_YEAR("This Year");

    fun calculateRange(today: LocalDate): Pair<LocalDate, LocalDate> = when (this) {
        TODAY -> today to today
        LAST_7_DAYS -> today.minusDays(6) to today
        LAST_30_DAYS -> today.minusDays(29) to today
        THIS_MONTH -> today.withDayOfMonth(1) to today
        THIS_YEAR -> today.withDayOfYear(1) to today
    }
}
