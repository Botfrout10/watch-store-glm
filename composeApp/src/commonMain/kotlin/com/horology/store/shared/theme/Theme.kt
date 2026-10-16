package com.horology.store.shared.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

/** Swiss edges: sharp cards, pill chips, 2dp buttons. */
object HoroShapes {
    val sharp = RectangleShape
    val card = RoundedCornerShape(2.dp)
    val button = RoundedCornerShape(2.dp)
    val roundel = RoundedCornerShape(50)
}

/**
 * Named type tokens for the current composition.
 * CMP's `Font()` is @Composable, so the bag is built in [HorologyTheme] and
 * surfaced here instead of living as static vals.
 */
val LocalHoroType = staticCompositionLocalOf<HoroType> {
    error("HoroType not provided — content must sit inside HorologyTheme")
}

@Composable
fun HorologyTheme(content: @Composable () -> Unit) {
    val typography = horoTypography()
    val horoType = horoType()
    MaterialTheme(
        colorScheme = SwissScheme,
        typography = typography,
        shapes = MaterialTheme.shapes.copy(
            extraSmall = HoroShapes.roundel,
            small = HoroShapes.card,
            medium = HoroShapes.card,
            large = HoroShapes.card,
            extraLarge = RoundedCornerShape(4.dp),
        ),
    ) {
        CompositionLocalProvider(LocalHoroType provides horoType, content = content)
    }
}
