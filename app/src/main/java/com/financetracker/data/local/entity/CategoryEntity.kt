package com.financetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val name: String,
    val iconName: String,
    val colorHex: String? = null,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0
)
