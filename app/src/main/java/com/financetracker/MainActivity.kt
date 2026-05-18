package com.financetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.financetracker.domain.repository.CategoryRepository
import com.financetracker.domain.repository.SettingsRepository
import com.financetracker.ui.navigation.AppNavHost
import com.financetracker.ui.theme.FinanceTrackingAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository

    @Inject lateinit var categoryRepository: CategoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Seed default categories on first launch
        CoroutineScope(Dispatchers.IO).launch {
            categoryRepository.seedDefaultCategories()
        }

        enableEdgeToEdge()
        setContent {
            val userPreferences by settingsRepository.userPreferences.collectAsState(
                initial = com.financetracker.data.local.prefs.UserPreferences()
            )

            FinanceTrackingAppTheme(
                themeMode = userPreferences.themeMode,
                accentColor = userPreferences.accentColorIndex
            ) {
                AppNavHost()
            }
        }
    }
}
