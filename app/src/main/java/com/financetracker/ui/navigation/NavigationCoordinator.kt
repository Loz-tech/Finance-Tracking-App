package com.financetracker.ui.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class NavigationCoordinator @Inject constructor() : ViewModel() {

    private val _navigationTargets = Channel<NavigationTarget>(Channel.CONFLATED)
    val navigationTargets: Flow<NavigationTarget> = _navigationTargets.receiveAsFlow()

    private val bottomNavDestinations = destinations.filter { it.showsBottomBar }

    fun chromeState(currentRoute: String?): ChromeState {
        val destination = destinations.find { it.route == currentRoute }
        return ChromeState(
            showsBottomBar = destination?.showsBottomBar ?: false,
            showsTopBar = destination?.showsTopBar ?: false,
            showsFab = destination?.showsFab ?: false,
            titleRes = destination?.titleRes,
            bottomNav = BottomNavState(
                selectedRoute = currentRoute,
                items = bottomNavDestinations.map {
                    BottomNavItemData(
                        screen = it.screen,
                        route = it.route,
                        labelRes = it.bottomNavLabelRes!!,
                        selectedIcon = it.selectedIcon!!,
                        unselectedIcon = it.unselectedIcon!!
                    )
                }
            )
        )
    }

    fun navigate(target: NavigationTarget) {
        _navigationTargets.trySend(target)
    }
}
