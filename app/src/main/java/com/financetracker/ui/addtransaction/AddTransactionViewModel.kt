package com.financetracker.ui.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

data class AddTransactionUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val amount: String = "",
    val note: String = "",
    val date: LocalDate = LocalDate.now(),
    val isEditMode: Boolean = false,
    val editTransactionId: java.util.UUID? = null,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    fun loadTransaction(id: java.util.UUID) {
        viewModelScope.launch {
            val transaction = transactionRepository.getTransactionById(id)
            if (transaction != null) {
                _uiState.value = _uiState.value.copy(
                    selectedCategory = transaction.category,
                    amount = transaction.amount.toPlainString(),
                    note = transaction.note,
                    date = transaction.date,
                    isEditMode = true,
                    editTransactionId = transaction.id
                )
            }
        }
    }

    fun onCategorySelected(category: Category) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            errorMessage = null
        )
    }

    fun onAmountChanged(amount: String) {
        val filtered = amount.filter { it.isDigit() || it == '.' }
        if (filtered.count { it == '.' } <= 1) {
            _uiState.value = _uiState.value.copy(amount = filtered, errorMessage = null)
        }
    }

    fun onNoteChanged(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun saveTransaction() {
        val state = _uiState.value
        val category = state.selectedCategory

        if (category == null) {
            _uiState.value = state.copy(errorMessage = "Please select a category")
            return
        }

        val amount = state.amount.toBigDecimalOrNull()
        if (amount == null || amount <= BigDecimal.ZERO) {
            _uiState.value = state.copy(errorMessage = "Please enter a valid amount")
            return
        }

        viewModelScope.launch {
            val transaction = Transaction(
                id = state.editTransactionId ?: java.util.UUID.randomUUID(),
                amount = amount,
                note = state.note,
                date = state.date,
                category = category
            )
            transactionRepository.saveTransaction(transaction)
            _uiState.value = state.copy(isSaved = true)
        }
    }

    fun reset() {
        _uiState.value = AddTransactionUiState(
            categories = _uiState.value.categories,
            isEditMode = false
        )
    }
}
