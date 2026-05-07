package com.financetracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Bottom nav destinations
    data object Home : Screen("home")
    data object Analytics : Screen("analytics")
    data object Search : Screen("search")
    data object Settings : Screen("settings")

    // Sub-screens
    data object History : Screen("history")
    data object Categories : Screen("categories")
    data object Budget : Screen("budget")
    data object Calendar : Screen("calendar")

    // Overlays
    data object AddTransaction : Screen("add_transaction")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val emoji: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Default.Home, "🏠"),
    BottomNavItem(Screen.Analytics, "Analytics", Icons.Default.Analytics, "📊"),
    BottomNavItem(Screen.Search, "Search", Icons.Default.Search, "🔍"),
    BottomNavItem(Screen.Settings, "Settings", Icons.Default.Settings, "⚙️")
)
