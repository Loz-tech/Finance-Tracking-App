package com.financetracker.domain.usecase

import com.financetracker.data.export.CsvExporter
import com.financetracker.data.export.JsonExporter
import com.financetracker.domain.model.ExportFormat
import com.financetracker.domain.repository.TransactionRepository
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class ExportTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val csvExporter: CsvExporter,
    private val jsonExporter: JsonExporter
) {

    suspend operator fun invoke(format: ExportFormat): Result<File> {
        val transactions = transactionRepository.getAllTransactions().first()
        val file = when (format) {
            ExportFormat.CSV -> csvExporter.export(transactions)
            ExportFormat.JSON -> jsonExporter.export(transactions)
        }
        return Result.success(file)
    }
}
