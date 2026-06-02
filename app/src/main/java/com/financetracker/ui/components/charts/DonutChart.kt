package com.financetracker.ui.components.charts

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class DonutSegment(val label: String, val iconName: String, val value: Float, val color: Color)

@Composable
fun DonutChart(segments: List<DonutSegment>, modifier: Modifier = Modifier) {
    val total = segments.sumOf { it.value.toDouble() }.toFloat()
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800),
        label = "donut"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val diameter = size.minDimension
            val topLeft = Offset(
                (size.width - diameter) / 2,
                (size.height - diameter) / 2
            )
            val arcSize = Size(diameter, diameter)
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = diameter / 2
            val strokeWidth = diameter * 0.22f

            var startAngle = -90f

            segments.forEach { segment ->
                val sweep = if (total > 0) (segment.value / total) * 360f * animationProgress else 0f
                drawArc(
                    color = segment.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                startAngle += sweep
            }

            startAngle = -90f
            val textSizePx = diameter * 0.08f
            val labelPaint = Paint().apply {
                textAlign = Paint.Align.CENTER
                this.textSize = textSizePx
                isFakeBoldText = true
            }

            segments.forEach { segment ->
                val sweep = if (total > 0) (segment.value / total) * 360f * animationProgress else 0f
                if (sweep > 15f) {
                    val midAngle = startAngle + sweep / 2
                    val rad = Math.toRadians(midAngle.toDouble())
                    val labelX = centerX + radius * cos(rad).toFloat()
                    val labelY = centerY + radius * sin(rad).toFloat()

                    val pct = (segment.value / total * 100).roundToInt()
                    val text = "$pct%"

                    val lum = segment.color.red * 0.299f + segment.color.green * 0.587f + segment.color.blue * 0.114f
                    if (lum > 0.5f) {
                        labelPaint.color = android.graphics.Color.BLACK
                        labelPaint.setShadowLayer(2f, 0f, 0f, android.graphics.Color.WHITE)
                    } else {
                        labelPaint.color = android.graphics.Color.WHITE
                        labelPaint.setShadowLayer(2f, 0f, 0f, android.graphics.Color.BLACK)
                    }

                    val metrics = labelPaint.fontMetrics
                    val offsetY = -(metrics.ascent + metrics.descent) / 2

                    drawContext.canvas.nativeCanvas.drawText(
                        text,
                        labelX,
                        labelY + offsetY,
                        labelPaint
                    )
                }
                startAngle += sweep
            }
        }
    }
}

@Composable
fun DonutLegend(segments: List<DonutSegment>, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        segments.forEach { segment ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(segment.color, CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = segment.label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
