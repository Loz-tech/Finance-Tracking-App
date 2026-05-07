package com.financetracker.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.financetracker.ui.addtransaction.AddTransactionSheet
import com.financetracker.ui.analytics.AnalyticsScreen
import com.financetracker.ui.budget.BudgetScreen
import com.financetracker.ui.calendar.CalendarScreen
import com.financetracker.ui.categories.CategoriesScreen
import com.financetracker.ui.history.HistoryScreen
import com.financetracker.ui.home.HomeScreen
import com.financetracker.ui.search.SearchScreen
import com.financetracker.ui.settings.SettingsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        bottomNavItems.any { it.screen.route == dest.route }
    } == true

    val showTopBar = currentDestination?.route in listOf(
        Screen.History.route,
        Screen.Categories.route,
        Screen.Budget.route,
        Screen.Calendar.route
    )

    val showFAB = currentDestination?.route == Screen.Home.route || currentDestination?.route == Screen.Categories.route

    val topBarTitle = when (currentDestination?.route) {
        Screen.History.route -> "History"
        Screen.Categories.route -> "Categories"
        Screen.Budget.route -> "Budget"
        Screen.Calendar.route -> "Calendar"
        else -> ""
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (showTopBar) {
                AppTopBar(
                    title = topBarTitle,
                    showBackButton = true,
                    onBackClick = { navController.popBackStack() }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentDestination?.route,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (showFAB) {
                AppFAB(onClick = { navController.navigate(Screen.AddTransaction.route) })
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onAddTransaction = { navController.navigate(Screen.AddTransaction.route) })
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen()
            }
            composable(Screen.Search.route) {
                SearchScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onExportCsv = { /* handled via ViewModel */ },
                    onExportJson = { /* handled via ViewModel */ }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(onEditTransaction = { id ->
                    navController.navigate("${Screen.AddTransaction.route}/$id")
                })
            }
            composable(Screen.Categories.route) {
                CategoriesScreen()
            }
            composable(Screen.Budget.route) {
                BudgetScreen()
            }
            composable(Screen.Calendar.route) {
                CalendarScreen()
            }
            composable(Screen.AddTransaction.route) {
                AddTransactionSheet(onDismiss = { navController.popBackStack() })
            }
        }
    }
}
