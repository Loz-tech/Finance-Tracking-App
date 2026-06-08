package com.financetracker.domain.model

import java.time.LocalDate

sealed class DateFilter {
    data object None : DateFilter()

    data class Quick(val chip: QuickChip) : DateFilter()

    data class Custom(val start: LocalDate, val end: LocalDate) : DateFilter()
}
