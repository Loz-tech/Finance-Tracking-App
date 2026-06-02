package com.financetracker.ui.components.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

data class BarData(val label: String, val value: Double)

@Composable
fun BarChart(bars: List<BarData>, modifier: Modifier = Modifier, maxValue: Double? = null) {
    if (bars.isEmpty()) return

    val barMax = maxValue ?: bars.maxOf { it.value }
    val displayMax = if (barMax == 0.0) 1.0 else barMax * 1.15
    var selectedIndex by remember { mutableStateOf(-1) }
    val animationProgress by animateFloatAsState(1f, tween(600), label = "bars")

    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryColorDim = primaryColor.copy(alpha = 0.6f)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(top = 8.dp)
                .pointerInput(bars) {
                    detectTapGestures { offset ->
                        val barWidth = size.width.toFloat() / bars.size
                        val index = (offset.x / barWidth).toInt().coerceIn(0, bars.size - 1)
                        selectedIndex = if (selectedIndex == index) -1 else index
                    }
                }
        ) {
            val barWidth = size.width / bars.size
            val totalHeight = size.height - 40f

            bars.forEachIndexed { index, bar ->
                val barHeight = if (displayMax >
                    0
                ) {
                    (bar.value / displayMax * totalHeight * animationProgress).toFloat()
                } else {
                    0f
                }
                val x = index * barWidth + barWidth * 0.15f
                val y = totalHeight - barHeight + 10f

                drawRect(
                    color = if (index == selectedIndex) {
                        primaryColor
                    } else {
                        primaryColorDim
                    },
                    topLeft = Offset(x, y),
                    size = Size(barWidth * 0.7f, barHeight)
                )
            }
        }

        // Labels
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bars.forEachIndexed { index, bar ->
                Text(
                    text = bar.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (index == selectedIndex) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        if (selectedIndex in bars.indices) {
            val selected = bars[selectedIndex]
            Text(
                text = "${selected.label}: $${String.format(Locale.getDefault(), "%.2f", selected.value)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
