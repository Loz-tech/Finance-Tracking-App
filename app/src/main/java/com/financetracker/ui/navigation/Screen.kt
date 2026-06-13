package com.financetracker.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")

    data object Analytics : Screen("analytics")

    data object Search : Screen("search")

    data object Settings : Screen("settings")

    data object History : Screen("history")

    data object Categories : Screen("categories")

    data object Budget : Screen("budget")

    data object Calendar : Screen("calendar")

    data object AddTransaction : Screen("add_transaction")
}
