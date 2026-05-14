package com.financetracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
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
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Analytics, "Analytics", Icons.Filled.Assessment, Icons.Outlined.Assessment),
    BottomNavItem(Screen.Search, "Search", Icons.Filled.Search, Icons.Outlined.Search),
    BottomNavItem(Screen.Settings, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
)
