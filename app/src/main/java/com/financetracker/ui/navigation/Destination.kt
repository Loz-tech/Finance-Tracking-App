package com.financetracker.ui.navigation

import androidx.annotation.StringRes
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
import com.financetracker.R

data class Destination(
    val screen: Screen,
    @param:StringRes val titleRes: Int? = null,
    @param:StringRes val bottomNavLabelRes: Int? = null,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null,
    val showsBottomBar: Boolean = false,
    val showsTopBar: Boolean = false,
    val showsFab: Boolean = false
) {
    val route: String get() = screen.route
}

data class BottomNavItemData(
    val screen: Screen,
    val route: String,
    @param:StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

data class ChromeState(
    val showsBottomBar: Boolean = false,
    val showsTopBar: Boolean = false,
    val showsFab: Boolean = false,
    @param:StringRes val titleRes: Int? = null,
    val bottomNav: BottomNavState = BottomNavState()
)

data class BottomNavState(val selectedRoute: String? = null, val items: List<BottomNavItemData> = emptyList())

val destinations = listOf(
    Destination(
        screen = Screen.Home,
        bottomNavLabelRes = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        showsBottomBar = true,
        showsFab = true
    ),
    Destination(
        screen = Screen.Analytics,
        bottomNavLabelRes = R.string.nav_analytics,
        selectedIcon = Icons.Filled.Assessment,
        unselectedIcon = Icons.Outlined.Assessment,
        showsBottomBar = true
    ),
    Destination(
        screen = Screen.Search,
        bottomNavLabelRes = R.string.nav_search,
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
        showsBottomBar = true
    ),
    Destination(
        screen = Screen.Settings,
        bottomNavLabelRes = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        showsBottomBar = true
    ),
    Destination(
        screen = Screen.History,
        titleRes = R.string.title_history,
        showsTopBar = true
    ),
    Destination(
        screen = Screen.Categories,
        titleRes = R.string.title_categories,
        showsTopBar = true,
        showsFab = true
    ),
    Destination(
        screen = Screen.Budget,
        titleRes = R.string.title_budget,
        showsTopBar = true
    ),
    Destination(
        screen = Screen.Calendar,
        titleRes = R.string.title_calendar,
        showsTopBar = true
    ),
    Destination(
        screen = Screen.AddTransaction
    )
)
