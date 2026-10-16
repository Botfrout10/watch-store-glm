package com.horology.store.shared.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.horology.store.shared.Res
import com.horology.store.shared.ibm_plex_mono_400
import com.horology.store.shared.ibm_plex_mono_600
import com.horology.store.shared.space_grotesk_400
import com.horology.store.shared.space_grotesk_500
import com.horology.store.shared.space_grotesk_700
import org.jetbrains.compose.resources.Font

/**
 * Space Grotesk + IBM Plex Mono, per DESIGN.md.
 * CMP's `Font()` is @Composable, so families are provided in composition
 * (bundle once in [HorologyTheme], never as static top-level vals).
 */
object HoroFonts {
    @Composable
    fun grotesk(): FontFamily = FontFamily(
        Font(Res.font.space_grotesk_400, FontWeight.Normal),
        Font(Res.font.space_grotesk_500, FontWeight.Medium),
        Font(Res.font.space_grotesk_700, FontWeight.Bold),
    )

    @Composable
    fun mono(): FontFamily = FontFamily(
        Font(Res.font.ibm_plex_mono_400, FontWeight.Normal),
        Font(Res.font.ibm_plex_mono_600, FontWeight.SemiBold),
    )
}

/** Material slot mapping; built in composition and passed to MaterialTheme. */
@Composable
fun horoTypography(): Typography {
    val grotesk = HoroFonts.grotesk()
    return Typography(
        displayLarge = TextStyle(
            fontFamily = grotesk,
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-1.2).sp,
            lineHeight = 60.sp,
        ),
        displayMedium = TextStyle(
            fontFamily = grotesk,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.8).sp,
            lineHeight = 38.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = grotesk,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
            lineHeight = 32.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = grotesk,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.4).sp,
            lineHeight = 28.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = grotesk,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.2).sp,
            lineHeight = 26.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = grotesk,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 24.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = grotesk,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 22.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = grotesk,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = grotesk,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 26.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = grotesk,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = grotesk,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.2.sp,
            lineHeight = 20.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = grotesk,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
            lineHeight = 18.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = grotesk,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 3.sp,
            lineHeight = 14.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = grotesk,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.4.sp,
            lineHeight = 12.sp,
        ),
    )
}

/** Named, non-Material tokens — numerals in IBM Plex Mono. Immutable per-composition bag. */
class HoroType(
    val PriceXL: TextStyle,
    val PriceL: TextStyle,
    val PriceM: TextStyle,
    val Reference: TextStyle,
    val MonoCaption: TextStyle,
    val MonoDisplay: TextStyle,
)

/** Builds [HoroType] for the current composition. */
@Composable
fun horoType(): HoroType {
    val mono = HoroFonts.mono()
    return HoroType(
        PriceXL = TextStyle(
            fontFamily = mono,
            fontSize = 34.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.5).sp,
            lineHeight = 38.sp,
        ),
        PriceL = TextStyle(
            fontFamily = mono,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 26.sp,
        ),
        PriceM = TextStyle(
            fontFamily = mono,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 22.sp,
        ),
        Reference = TextStyle(
            fontFamily = mono,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 1.sp,
            lineHeight = 18.sp,
        ),
        MonoCaption = TextStyle(
            fontFamily = mono,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.6.sp,
            lineHeight = 16.sp,
        ),
        MonoDisplay = TextStyle(
            fontFamily = mono,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.3).sp,
            lineHeight = 34.sp,
        ),
    )
}
