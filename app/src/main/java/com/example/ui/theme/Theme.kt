package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = CeremonialGold,
    onPrimary = CeremonialGoldTextOnGold,
    primaryContainer = CeremonialGoldMuted,
    onPrimaryContainer = CeremonialGold,
    secondary = EmeraldAccent,
    onSecondary = Color.White,
    secondaryContainer = EmeraldContainerDark,
    onSecondaryContainer = EmeraldTextLight,
    tertiary = EmeraldTextLight,
    onTertiary = RegimentalBgDark,
    background = RegimentalBgDark,
    onBackground = TextPrimaryLight,
    surface = RegimentalCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = RegimentalFieldBg,
    onSurfaceVariant = TextSecondaryMuted,
    outline = RegimentalBorder,
    error = AlertCrimson,
    errorContainer = AlertCrimsonContainer
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryEmerald,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF0E6),
    onPrimaryContainer = LightPrimaryEmerald,
    secondary = LightGoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF2CE),
    onSecondaryContainer = Color(0xFF3E2B00),
    background = LightBg,
    onBackground = LightInk,
    surface = LightSurface,
    onSurface = LightInk,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightMuted,
    outline = LightOutline,
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFFDECEC)
)

val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp)
)

@Composable
fun JsiPayCalculatorTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
