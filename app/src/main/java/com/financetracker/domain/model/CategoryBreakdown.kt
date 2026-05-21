package com.financetracker.domain.model

import java.math.BigDecimal

data class CategoryBreakdown(val name: String, val emoji: String, val amount: BigDecimal, val colorHex: String?)
