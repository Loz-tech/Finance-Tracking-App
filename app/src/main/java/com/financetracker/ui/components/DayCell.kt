package com.financetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financetracker.ui.calendar.CalendarDay
import com.financetracker.ui.theme.FinanceTrackingAppTheme
import java.time.LocalDate

private val HEAT_COLORS = listOf(
    Color(0xFFE0E0E0),
    Color(0xFFB2DFDB),
    Color(0xFF80CBC4),
    Color(0xFF4DB6AC),
    Color(0xFF00897B)
)

@Composable
fun DayCell(
    day: CalendarDay,
    isToday: Boolean,
    isSelected: Boolean,
    isFuture: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseColor = HEAT_COLORS.getOrElse(day.intensity) { HEAT_COLORS[0] }
    val bgColor = if (isFuture) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else baseColor
    val borderMod = if (isToday) {
        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
    } else if (isSelected) {
        Modifier.border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(6.dp))
    } else {
        Modifier.border(1.dp, Color.Transparent, RoundedCornerShape(6.dp))
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .then(borderMod)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .clickable(enabled = !isFuture, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = if (day.intensity <= 1 || isFuture) MaterialTheme.colorScheme.onSurface else Color.White
        )
    }
}

@Preview
@Composable
private fun DayCellPreview() {
    FinanceTrackingAppTheme {
        val today = LocalDate.now()
        DayCell(
            day = CalendarDay(today, 45.0, emptyList(), 2),
            isToday = true,
            isSelected = false,
            isFuture = false,
            onClick = {}
        )
    }
}
