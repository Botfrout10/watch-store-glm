package com.horology.store.shared.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.horology.store.shared.theme.HoroColors

private val chipCorner = RoundedCornerShape(50)
private val chipAnim = tween<Color>(durationMillis = 150)

/** Filter chip: ink fill when selected, hairline outline otherwise. Used in catalog rails. */
@Composable
fun HoroChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val background by animateColorAsState(
        targetValue = if (selected) HoroColors.ink else HoroColors.card,
        animationSpec = chipAnim,
        label = "chipBg",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) HoroColors.card else HoroColors.ink,
        animationSpec = chipAnim,
        label = "chipFg",
    )

    Box(
        modifier = modifier
            .clip(chipCorner)
            .background(if (enabled) background else background.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = if (selected) HoroColors.ink else HoroColors.hairline,
                shape = chipCorner,
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor.copy(alpha = if (enabled) 1f else 0.45f),
        )
    }
}
