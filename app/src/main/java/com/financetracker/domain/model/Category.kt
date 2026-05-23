package com.financetracker.domain.model

import java.util.UUID

data class Category(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val iconName: String,
    val colorHex: String? = null,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0
)
