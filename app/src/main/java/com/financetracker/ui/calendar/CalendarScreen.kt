package com.financetracker.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val today = LocalDate.now()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Month header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = viewModel::previousMonth) { Icon(Icons.Default.ChevronLeft, "Previous") }
            Text(
                text = DateTimeFormatter.ofPattern("MMMM yyyy").format(uiState.yearMonth),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = viewModel::nextMonth) { Icon(Icons.Default.ChevronRight, "Next") }
        }

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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        DateTimeFormatter.ofPattern("EEEE, MMM d").format(selectedDay.date),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        currencyFormatter.format(selectedDay.total),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val txns = selectedDay.transactions
                    if (txns.isEmpty()) {
                        Text(
                            "No transactions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            txns.forEach { t ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${t.category.emoji} ${t.note.ifBlank {
                                            t.category.name
                                        }}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        currencyFormatter.format(t.amount),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(day: CalendarDay, isToday: Boolean, isSelected: Boolean, isFuture: Boolean, onClick: () -> Unit) {
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
        modifier = Modifier
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
