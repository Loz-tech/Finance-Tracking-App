package com.financetracker.ui.navigation

import java.util.UUID

sealed class NavigationTarget {
    data object Back : NavigationTarget()

    data object AddTransaction : NavigationTarget()

    data class EditTransaction(val id: UUID) : NavigationTarget()

    data object Budget : NavigationTarget()

    data object History : NavigationTarget()
}
