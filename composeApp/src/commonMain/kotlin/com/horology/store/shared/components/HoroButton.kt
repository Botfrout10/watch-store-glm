package com.horology.store.shared.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.horology.store.shared.theme.HoroColors

enum class HoroButtonStyle { Primary, Secondary, Text }

/**
 * Swiss button: sharp 2dp corners, height >= 52dp, plate-press scale animation.
 * Red only on [HoroButtonStyle.Primary].
 */
@Composable
fun HoroButton(
    text: String,
    modifier: Modifier = Modifier,
    style: HoroButtonStyle = HoroButtonStyle.Primary,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
        label = "buttonPress",
    )

    val background = when (style) {
        HoroButtonStyle.Primary -> HoroColors.red
        HoroButtonStyle.Secondary -> HoroColors.card
        HoroButtonStyle.Text -> Color.Transparent
    }
    val contentColor = when (style) {
        HoroButtonStyle.Primary -> Color.White
        else -> HoroColors.ink
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(2.dp))
            .background(if (enabled) background else background.copy(alpha = 0.38f))
            .let { base ->
                if (style == HoroButtonStyle.Secondary) {
                    base.border(1.dp, HoroColors.hairline, RoundedCornerShape(2.dp))
                } else base
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .heightIn(min = 52.dp)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor.copy(alpha = if (enabled) 1f else 0.55f),
        )
    }
}
