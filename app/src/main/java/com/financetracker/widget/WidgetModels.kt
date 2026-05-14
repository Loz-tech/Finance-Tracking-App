package com.financetracker.widget

import kotlinx.serialization.Serializable

@Serializable
data class WidgetCategory(
    val id: String,
    val name: String,
    val emoji: String
)
