package com.financetracker.data.local.prefs

import com.financetracker.domain.model.ExportFormat
import kotlinx.serialization.Serializable

@Serializable
data class RecentExport(
    val relativePath: String,
    val format: ExportFormat,
    val timestamp: Long = System.currentTimeMillis()
)
