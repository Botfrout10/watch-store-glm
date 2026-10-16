package com.horology.store.shared.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * "Light minimal Swiss" palette. Source of truth: DESIGN.md.
 * Signal red is rare — never more than two live accents per screen.
 */
object HoroColors {
    val paper = Color(0xFFFAFAF8)
    val card = Color(0xFFFFFFFF)
    val ink = Color(0xFF0F0F0F)
    val inkSoft = Color(0xFF3A3A36)
    val ink60 = Color(0xFF6E6E68)
    val hairline = Color(0xFFE4E2DC)
    val red = Color(0xFFE10600)
    val redDeep = Color(0xFFC00A00)
    val redSoft = Color(0x14E10600) // 8% alpha
    val steel = Color(0xFFB8BCBE)
    val steelSoft = Color(0xFFDFE2E1)
}

/**
 * Material3 mapping is mechanical — product surfaces use HoroColors directly
 * so components read "Swiss" rather than "Material by default".
 */
val SwissScheme = lightColorScheme(
    primary = HoroColors.red,
    onPrimary = Color.White,
    primaryContainer = HoroColors.redSoft,
    onPrimaryContainer = HoroColors.redDeep,
    secondary = HoroColors.ink,
    onSecondary = HoroColors.card,
    background = HoroColors.paper,
    onBackground = HoroColors.ink,
    surface = HoroColors.paper,
    onSurface = HoroColors.ink,
    surfaceVariant = HoroColors.card,
    onSurfaceVariant = HoroColors.ink60,
    surfaceContainer = HoroColors.card,
    surfaceContainerLow = HoroColors.paper,
    surfaceContainerHigh = HoroColors.card,
    surfaceContainerHighest = HoroColors.card,
    outline = HoroColors.hairline,
    outlineVariant = HoroColors.hairline,
    error = Color(0xFFB3261E),
)
