package com.financetracker.data.export

import android.content.Context
import com.financetracker.domain.model.Transaction
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun export(transactions: List<Transaction>): File {
        val dir = File(context.getExternalFilesDir(null), "ISpend")
        dir.mkdirs()

        val dateStr = java.time.LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val file = File(dir, "ISpend_export_$dateStr.json")

        val json = StringBuilder()
        json.appendLine("[")
        transactions.forEachIndexed { i, txn ->
            json.append("  {")
            json.append("\"date\": \"${txn.date}\", ")
            json.append("\"category\": \"${txn.category.name}\", ")
            json.append("\"emoji\": \"${txn.category.emoji}\", ")
            json.append("\"amount\": ${txn.amount}, ")
            json.append("\"note\": \"${txn.note.replace("\"", "\\\"")}\"")
            json.append("}")
            if (i < transactions.size - 1) json.appendLine(",") else json.appendLine()
        }
        json.appendLine("]")

        file.writeText(json.toString())
        return file
    }
}
