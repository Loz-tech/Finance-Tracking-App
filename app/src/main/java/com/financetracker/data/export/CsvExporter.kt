package com.financetracker.data.export

import android.content.Context
import com.financetracker.domain.model.Transaction
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CsvExporter @Inject constructor(@ApplicationContext private val context: Context) {
    fun export(transactions: List<Transaction>): File {
        val dir = File(context.getExternalFilesDir(null), "ISpend")
        dir.mkdirs()

        val dateStr = java.time.LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val file = File(dir, "ISpend_export_$dateStr.csv")

        file.writeText(buildCsvContent(transactions))
        return file
    }

    companion object {
        internal fun buildCsvContent(transactions: List<Transaction>): String {
            val csv = StringBuilder()
            csv.appendLine("Date,Category,Icon,Amount,Note")
            transactions.forEach { txn ->
                csv.appendLine(
                    "${txn.date},${csvField(
                        txn.category.name
                    )},${csvField(txn.category.iconName)},${txn.amount},${csvField(txn.note)}"
                )
            }
            return csv.toString()
        }

        private fun csvField(value: String): String = "\"${value.replace("\"", "\"\"")}\""
    }
}
