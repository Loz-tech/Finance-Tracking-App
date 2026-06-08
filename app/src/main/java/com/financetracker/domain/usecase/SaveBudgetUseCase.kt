package com.financetracker.domain.usecase

import com.financetracker.domain.model.Budget
import com.financetracker.domain.repository.BudgetRepository
import com.financetracker.domain.repository.SettingsRepository
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class SaveBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(categoryId: UUID?, amount: BigDecimal, yearMonth: String): Budget {
        val existing = if (categoryId == null) {
            budgetRepository.getTotalBudget(yearMonth)
        } else {
            budgetRepository.getCategoryBudget(yearMonth, categoryId)
        }
        val prefs = settingsRepository.userPreferences.first()
        val budget = Budget(
            id = existing?.id ?: UUID.randomUUID(),
            categoryId = categoryId,
            yearMonth = yearMonth,
            limitAmount = amount,
            originalLimitAmount = existing?.originalLimitAmount ?: amount,
            originalCurrencyCode = existing?.originalCurrencyCode ?: prefs.currencyCode
        )
        budgetRepository.saveBudget(budget)
        return budget
    }
}
