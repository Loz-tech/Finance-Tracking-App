package com.financetracker.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financetracker.ui.components.DayCell
import com.financetracker.ui.components.DayDetailCard
import com.financetracker.ui.components.MonthNavigator
import java.time.LocalDate

private val HEAT_COLORS = listOf(
    Color(0xFFE0E0E0),
    Color(0xFFB2DFDB),
    Color(0xFF80CBC4),
    Color(0xFF4DB6AC),
    Color(0xFF00897B)
)

@Composable
fun CalendarScreen(modifier: Modifier = Modifier, viewModel: CalendarViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val today = LocalDate.now()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Month header
        MonthNavigator(
            yearMonth = uiState.yearMonth,
            onPrevious = viewModel::previousMonth,
            onNext = viewModel::nextMonth
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Day headers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Calendar grid
        val firstDayOfWeek = uiState.yearMonth.atDay(1).dayOfWeek.value
        val offset = if (firstDayOfWeek == 7) 0 else firstDayOfWeek

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Empty cells
            items(offset) {
                Box(modifier = Modifier.aspectRatio(1f))
            }
            items(uiState.days.size) { index ->
                val day = uiState.days[index]
                val isToday = day.date == today
                val isSelected = day.date == uiState.selectedDay?.date
                val isFuture = day.date.isAfter(today)

                DayCell(
                    day = day,
                    isToday = isToday,
                    isSelected = isSelected,
                    isFuture = isFuture,
                    onClick = { if (!isFuture) viewModel.onDayClicked(day) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Legend
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("Less", style = MaterialTheme.typography.labelSmall)
            HEAT_COLORS.forEach { color ->
                Box(modifier = Modifier.size(16.dp).padding(2.dp).background(color, RoundedCornerShape(2.dp)))
            }
            Text("More", style = MaterialTheme.typography.labelSmall)
        }

        // Selected day detail
        val selectedDay = uiState.selectedDay
        if (selectedDay != null) {
            Spacer(modifier = Modifier.height(12.dp))
            DayDetailCard(day = selectedDay)
        }
    }
}
