package com.financetracker.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financetracker.ui.components.calendar.CalendarGrid
import com.financetracker.ui.components.calendar.DayDetailCard
import com.financetracker.ui.components.calendar.WeekdayHeader
import com.financetracker.ui.components.charts.HeatmapLegend
import com.financetracker.ui.components.input.MonthNavigator
import java.time.LocalDate

@Composable
fun CalendarScreen(modifier: Modifier = Modifier, viewModel: CalendarViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val today = LocalDate.now()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        MonthNavigator(
            yearMonth = uiState.yearMonth,
            onPrevious = viewModel::previousMonth,
            onNext = viewModel::nextMonth
        )

        Spacer(modifier = Modifier.height(8.dp))

        WeekdayHeader()

        Spacer(modifier = Modifier.height(4.dp))

        CalendarGrid(
            days = uiState.days,
            yearMonth = uiState.yearMonth,
            selectedDay = uiState.selectedDay,
            onDayClick = viewModel::onDayClicked,
            today = today
        )

        Spacer(modifier = Modifier.height(8.dp))

        HeatmapLegend()

        val selectedDay = uiState.selectedDay
        if (selectedDay != null) {
            Spacer(modifier = Modifier.height(12.dp))
            DayDetailCard(day = selectedDay)
        }
    }
}
