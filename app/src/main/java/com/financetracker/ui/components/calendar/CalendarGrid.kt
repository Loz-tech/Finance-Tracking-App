package com.financetracker.ui.components.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.financetracker.ui.calendar.CalendarDay
import com.financetracker.ui.preview.PreviewData
import com.financetracker.ui.theme.FinanceTrackingAppTheme
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarGrid(
    days: List<CalendarDay>,
    yearMonth: YearMonth,
    selectedDay: CalendarDay?,
    onDayClick: (CalendarDay) -> Unit,
    today: LocalDate = LocalDate.now(),
    modifier: Modifier = Modifier
) {
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value
    val offset = if (firstDayOfWeek == 7) 0 else firstDayOfWeek

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier.fillMaxWidth()
    ) {
        repeat(offset) {
            item {
                Box(modifier = Modifier.aspectRatio(1f))
            }
        }
        items(days.size) { index ->
            val day = days[index]
            val isToday = day.date == today
            val isSelected = day.date == selectedDay?.date
            val isFuture = day.date.isAfter(today)

            DayCell(
                day = day,
                isToday = isToday,
                isSelected = isSelected,
                isFuture = isFuture,
                onClick = { if (!isFuture) onDayClick(day) }
            )
        }
    }
}

@Preview
@Composable
private fun CalendarGridPreview() {
    FinanceTrackingAppTheme {
        CalendarGrid(
            days = PreviewData.calendarDays,
            yearMonth = YearMonth.now(),
            selectedDay = null,
            onDayClick = {}
        )
    }
}
