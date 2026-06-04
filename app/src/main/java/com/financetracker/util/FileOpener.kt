package com.financetracker.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.financetracker.domain.model.ExportFormat
import java.io.File

object FileOpener {

    fun openExport(context: Context, relativePath: String, format: ExportFormat, chooserTitle: String): Result<Unit> {
        val baseDir = File(context.getExternalFilesDir(null), "ISpend").canonicalFile
        val targetFile = File(baseDir, relativePath).canonicalFile

        if (!targetFile.startsWith(baseDir)) {
            return Result.failure(SecurityException("Path traversal"))
        }
        if (!targetFile.exists()) {
            return Result.failure(IllegalStateException("File not found"))
        }

        val uri = try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                targetFile
            )
        } catch (e: IllegalArgumentException) {
            return Result.failure(e)
        }

        val mimeType = when (format) {
            ExportFormat.CSV -> "text/csv"
            ExportFormat.JSON -> "application/json"
        }

        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(viewIntent, chooserTitle).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(chooser)
            Result.success(Unit)
        } catch (e: ActivityNotFoundException) {
            Result.failure(e)
        }
    }
}
