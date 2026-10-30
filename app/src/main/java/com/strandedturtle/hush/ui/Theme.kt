package com.strandedturtle.hush.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Palette for the frosted-glass look: aurora blobs behind translucent, rim-lit panels. */
@Immutable
data class GlassColors(
    val isDark: Boolean,
    val background: Color,
    val blobs: List<Color>,
    val fillTop: Color,
    val fillBottom: Color,
    val rimTop: Color,
    val rimBottom: Color,
    val text: Color,
    val textMuted: Color,
    val accent: Color,
    val accentAlt: Color,
    val danger: Color,
    val dangerAlt: Color,
    val good: Color,
    val warn: Color,
)

private val DarkGlass = GlassColors(
    isDark = true,
    background = Color(0xFF070914),
    blobs = listOf(Color(0xFF4F5DFF), Color(0xFFA35BFF), Color(0xFF1FB6FF)),
    fillTop = Color.White.copy(alpha = 0.11f),
    fillBottom = Color.White.copy(alpha = 0.04f),
    rimTop = Color.White.copy(alpha = 0.32f),
    rimBottom = Color.White.copy(alpha = 0.05f),
    text = Color.White,
    textMuted = Color.White.copy(alpha = 0.62f),
    accent = Color(0xFF8FA8FF),
    accentAlt = Color(0xFFB98CFF),
    danger = Color(0xFFFF6B8A),
    dangerAlt = Color(0xFFFF9466),
    good = Color(0xFF5BE3A5),
    warn = Color(0xFFFFC266),
)

private val LightGlass = GlassColors(
    isDark = false,
    background = Color(0xFFEEF1FB),
    blobs = listOf(Color(0xFF9DADFF), Color(0xFFE0B3FF), Color(0xFF94E3FF)),
    fillTop = Color.White.copy(alpha = 0.72f),
    fillBottom = Color.White.copy(alpha = 0.42f),
    rimTop = Color.White,
    rimBottom = Color.White.copy(alpha = 0.35f),
    text = Color(0xFF0E1230),
    textMuted = Color(0xFF0E1230).copy(alpha = 0.58f),
    accent = Color(0xFF5468FF),
    accentAlt = Color(0xFF9A5BFF),
    danger = Color(0xFFF0476B),
    dangerAlt = Color(0xFFFF7A45),
    good = Color(0xFF17B877),
    warn = Color(0xFFE59A1A),
)

val LocalGlass = staticCompositionLocalOf { DarkGlass }

@Composable
fun HushTheme(content: @Composable () -> Unit) {
    val glass = if (isSystemInDarkTheme()) DarkGlass else LightGlass
    val scheme = if (glass.isDark) {
        darkColorScheme(primary = glass.accent, background = glass.background, surface = glass.background)
    } else {
        lightColorScheme(primary = glass.accent, background = glass.background, surface = glass.background)
    }
    CompositionLocalProvider(LocalGlass provides glass) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
