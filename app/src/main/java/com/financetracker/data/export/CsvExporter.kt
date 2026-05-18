package com.financetracker.data.export

import android.content.Context
import com.financetracker.domain.model.Transaction
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileWriter
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

        FileWriter(file).use { writer ->
            writer.write("Date,Category,Emoji,Amount,Note\n")
            transactions.forEach { txn ->
                val note = txn.note.replace("\"", "\"\"")
                writer.write("${txn.date},${txn.category.name},${txn.category.emoji},${txn.amount},\"$note\"\n")
            }
        }
        return file
    }
}
