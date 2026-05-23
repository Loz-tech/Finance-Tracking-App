package com.financetracker.domain.model

import java.util.UUID

data class SearchCriteria(
    val query: String = "",
    val categoryIds: Set<UUID> = emptySet(),
    val dateFilter: DateFilter = DateFilter.Quick(QuickChip.LAST_7_DAYS)
)
