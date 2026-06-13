package com.financetracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
fun AppNavHost(coordinator: NavigationCoordinator = hiltViewModel()) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val chrome = coordinator.chromeState(currentRoute)

    LaunchedEffect(Unit) {
        coordinator.navigationTargets.collect { target ->
            when (target) {
                NavigationTarget.Back -> navController.popBackStack()
                NavigationTarget.AddTransaction -> navController.navigate(Screen.AddTransaction.route)
                is NavigationTarget.EditTransaction -> navController.navigate(
                    "${Screen.AddTransaction.route}/${target.id}"
                )
                NavigationTarget.Budget -> navController.navigate(Screen.Budget.route)
                NavigationTarget.History -> navController.navigate(Screen.History.route)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (chrome.showsTopBar) {
                AppTopBar(
                    title = chrome.titleRes?.let { stringResource(it) } ?: "",
                    showBackButton = true,
                    onBackClick = { coordinator.navigate(NavigationTarget.Back) }
                )
            }
        },
        bottomBar = {
            if (chrome.showsBottomBar) {
                BottomNavBar(
                    state = chrome.bottomNav,
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
            if (chrome.showsFab) {
                AppFAB(onClick = { coordinator.navigate(NavigationTarget.AddTransaction) })
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onAddTransaction = { coordinator.navigate(NavigationTarget.AddTransaction) },
                    onEditTransaction = { id ->
                        coordinator.navigate(NavigationTarget.EditTransaction(id))
                    },
                    onNavigateToBudget = { coordinator.navigate(NavigationTarget.Budget) }
                )
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen()
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onEditTransaction = { id ->
                        coordinator.navigate(NavigationTarget.EditTransaction(id))
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToBudget = { coordinator.navigate(NavigationTarget.Budget) },
                    onNavigateToHistory = { coordinator.navigate(NavigationTarget.History) }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    onEditTransaction = { id ->
                        coordinator.navigate(NavigationTarget.EditTransaction(id))
                    }
                )
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
            composable(
                route = "${Screen.AddTransaction.route}/{transactionId}",
                arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId")?.let {
                    java.util.UUID.fromString(it)
                }

                AddTransactionSheet(
                    onDismiss = { coordinator.navigate(NavigationTarget.Back) },
                    editTransactionId = transactionId
                )
            }
            composable(Screen.AddTransaction.route) {
                AddTransactionSheet(
                    onDismiss = { coordinator.navigate(NavigationTarget.Back) }
                )
            }
        }
    }
}
