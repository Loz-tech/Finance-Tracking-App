package com.financetracker.widget

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.financetracker.R
import com.financetracker.data.local.prefs.UserPreferences
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.SettingsRepository
import com.financetracker.domain.repository.TransactionRepository
import com.financetracker.ui.theme.FinanceTrackingAppTheme
import com.financetracker.util.rememberCurrencySymbol
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuickAddTransactionActivity : AppCompatActivity() {

    @Inject lateinit var transactionRepository: TransactionRepository

    @Inject lateinit var categoryRepository: CategoryRepository

    @Inject lateinit var settingsRepository: SettingsRepository

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val preselectedId = intent.getStringExtra("categoryId")?.let { UUID.fromString(it) }
        val preselectedName = intent.getStringExtra("categoryName") ?: ""
        val preselectedIconName = intent.getStringExtra("categoryIconName") ?: ""

        setContent {
            val prefs by settingsRepository.userPreferences.collectAsState(initial = UserPreferences())
            FinanceTrackingAppTheme(
                themeMode = prefs.themeMode,
                accentColor = prefs.accentColorIndex
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    QuickAddContent(
                        preselectedId = preselectedId,
                        preselectedName = preselectedName,
                        preselectedIconName = preselectedIconName,
                        categoryRepository = categoryRepository,
                        onSave = { amount, note, category ->
                            lifecycleScope.launch {
                                saveTransaction(amount, note, category)
                            }
                        },
                        onDismiss = { finish() }
                    )
                }
            }
        }
    }

    private suspend fun saveTransaction(amount: BigDecimal, note: String, category: Category) {
        val prefs = settingsRepository.userPreferences.first()
        val transaction = Transaction(
            amount = amount,
            originalAmount = amount,
            originalCurrencyCode = prefs.currencyCode,
            note = note,
            date = LocalDate.now(),
            category = category
        )
        transactionRepository.saveTransaction(transaction)
        Toast.makeText(this, getString(R.string.msg_transaction_saved), Toast.LENGTH_SHORT).show()
        finish()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickAddContent(
    preselectedId: UUID?,
    preselectedName: String,
    preselectedIconName: String,
    categoryRepository: CategoryRepository,
    onSave: (BigDecimal, String, Category) -> Unit,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var allCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val errorSelectCategory = stringResource(R.string.error_select_category)
    val errorValidAmount = stringResource(R.string.error_valid_amount)

    LaunchedEffect(preselectedId) {
        if (preselectedId != null) {
            val cat = categoryRepository.getCategoryById(preselectedId)
            if (cat != null) {
                selectedCategory = cat
            } else {
                // Fallback to a synthetic category if id is no longer in db
                selectedCategory = Category(
                    id = preselectedId,
                    name = preselectedName,
                    iconName = preselectedIconName.ifBlank { "MoreHoriz" }
                )
            }
        }
        categoryRepository.getAllCategories().collect { categories ->
            allCategories = categories
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.add_transaction_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        // Amount field
        OutlinedTextField(
            value = amount,
            onValueChange = { input ->
                val filtered = input.filter { it.isDigit() || it == '.' }
                if (filtered.count { it == '.' } <= 1) {
                    amount = filtered
                    errorMessage = null
                }
            },
            label = { Text(stringResource(R.string.input_amount)) },
            prefix = {
                val symbol = rememberCurrencySymbol()
                Text("$symbol  ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage != null
        )

        // Category selection
        val category = selectedCategory
        if (category != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Note: widget launch doesn't have iconStyle preference so we default to Filled
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                TextButton(onClick = { selectedCategory = null }) {
                    Text(stringResource(R.string.action_change))
                }
            }
        } else {
            Text(
                text = stringResource(R.string.category_label),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allCategories.forEach { cat ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // Note field
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text(stringResource(R.string.note_optional)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(stringResource(R.string.action_cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Button(
                onClick = {
                    if (category == null) {
                        errorMessage = errorSelectCategory
                        return@Button
                    }
                    val amt = amount.toBigDecimalOrNull()
                    if (amt == null || amt <= BigDecimal.ZERO) {
                        errorMessage = errorValidAmount
                        return@Button
                    }
                    isSaving = true
                    onSave(amt, note, category)
                },
                modifier = Modifier.weight(1f),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.action_save))
                }
            }
        }
    }
}
