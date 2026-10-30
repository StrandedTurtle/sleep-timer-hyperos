package com.strandedturtle.hush.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/** Glass ring showing the fraction of the timer that is left, with the countdown in the middle. */
@Composable
fun TimerDial(
    fraction: Float,
    timeText: String,
    caption: String,
    modifier: Modifier = Modifier,
) {
    val glass = LocalGlass.current
    val sweep by animateFloatAsState(fraction.coerceIn(0f, 1f), tween(600), label = "sweep")

    Box(modifier.fillMaxWidth().aspectRatio(1f), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxWidth().aspectRatio(1f).padding(12.dp)) {
            val stroke = 16.dp.toPx()
            val inset = stroke / 2 + 10.dp.toPx()
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
            val topLeft = Offset(inset, inset)

            // Inner glass disc.
            drawCircle(
                brush = Brush.verticalGradient(listOf(glass.fillTop, glass.fillBottom)),
                radius = arcSize.width / 2 - stroke,
            )
            drawCircle(
                brush = Brush.verticalGradient(listOf(glass.rimTop, glass.rimBottom)),
                radius = arcSize.width / 2 - stroke,
                style = Stroke(1.dp.toPx()),
            )
            // Track.
            drawArc(
                color = glass.text.copy(alpha = if (glass.isDark) 0.08f else 0.07f),
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(stroke),
            )
            if (sweep > 0f) {
                rotate(-90f) {
                    drawArc(
                        brush = Brush.sweepGradient(listOf(glass.accentAlt, glass.accent, glass.accentAlt)),
                        startAngle = 0f, sweepAngle = 360f * sweep, useCenter = false,
                        topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Round),
                    )
                }
                // Glowing head of the arc.
                val angle = Math.toRadians((360.0 * sweep) - 90.0)
                val r = arcSize.width / 2
                val head = Offset(center.x + (r * cos(angle)).toFloat(), center.y + (r * sin(angle)).toFloat())
                drawCircle(
                    brush = Brush.radialGradient(listOf(glass.accent.copy(alpha = 0.7f), Color.Transparent), head, stroke * 1.6f),
                    radius = stroke * 1.6f, center = head,
                )
                drawCircle(Color.White, radius = stroke * 0.32f, center = head)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                timeText,
                color = glass.text,
                fontSize = 60.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-1).sp,
                style = androidx.compose.ui.text.TextStyle(fontFeatureSettings = "tnum"),
            )
            Text(caption, color = glass.textMuted, fontSize = 15.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
