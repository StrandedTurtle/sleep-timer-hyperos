package com.strandedturtle.hush.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/** Slowly drifting, heavily blurred colour blobs; the "wallpaper" the glass panels sit on. */
@Composable
fun AuroraBackground(content: @Composable BoxScope.() -> Unit) {
    val glass = LocalGlass.current
    val t by rememberInfiniteTransition(label = "aurora").animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(36_000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase",
    )
    Box(Modifier.fillMaxSize().background(glass.background)) {
        Canvas(Modifier.fillMaxSize().blur(110.dp)) {
            val w = size.width
            val h = size.height
            val r = maxOf(w, h) * 0.42f
            val alpha = if (glass.isDark) 0.55f else 0.75f
            val centers = listOf(
                Offset(w * (0.22f + 0.10f * cos(t)), h * (0.18f + 0.06f * sin(t))),
                Offset(w * (0.85f + 0.08f * sin(t * 2)), h * (0.42f + 0.08f * cos(t))),
                Offset(w * (0.35f + 0.12f * sin(t)), h * (0.88f + 0.05f * cos(t * 2))),
            )
            centers.forEachIndexed { i, c ->
                val color = glass.blobs[i]
                drawCircle(
                    brush = Brush.radialGradient(listOf(color.copy(alpha = alpha), Color.Transparent), c, r),
                    radius = r,
                    center = c,
                )
            }
        }
        content()
    }
}

private fun Modifier.glassSurface(shape: Shape, glass: GlassColors): Modifier = this
    .clip(shape)
    .background(Brush.verticalGradient(listOf(glass.fillTop, glass.fillBottom)))
    .border(1.dp, Brush.verticalGradient(listOf(glass.rimTop, glass.rimBottom)), shape)

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(32.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val glass = LocalGlass.current
    Column(modifier.glassSurface(shape, glass).padding(20.dp), content = content)
}

@Composable
fun GlassIconButton(iconRes: Int, label: String, onClick: () -> Unit, size: Dp = 64.dp) {
    val glass = LocalGlass.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(size)
                .glassSurface(CircleShape, glass)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(iconRes), contentDescription = label, tint = glass.text, modifier = Modifier.size(26.dp))
        }
        Text(label, color = glass.textMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
fun GradientPill(text: String, colors: List<Color>, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .background(Brush.horizontalGradient(colors))
            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 34.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun GlassChip(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val glass = LocalGlass.current
    val shape = RoundedCornerShape(50)
    val base = if (selected) {
        modifier.clip(shape).background(Brush.horizontalGradient(listOf(glass.accent, glass.accentAlt)))
    } else {
        modifier.glassSurface(shape, glass)
    }
    Box(
        base
            .clickable(remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = if (selected) Color.White else glass.text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text.uppercase(),
        color = LocalGlass.current.textMuted,
        fontSize = 12.sp,
        letterSpacing = 1.4.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 12.dp),
    )
}

@Composable
fun ChipRow(content: @Composable () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        content()
    }
}
