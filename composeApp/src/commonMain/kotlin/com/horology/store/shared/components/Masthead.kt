package com.horology.store.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.horology.store.shared.theme.HoroColors

private val eyebrowGap = 8.dp
private val titleGap = 10.dp
private val ruleGap = 14.dp
private val dotSize = 6.dp

/** Editorial section header: red dot + mono eyebrow + grotesk headline + hairline rule. */
@Composable
fun Masthead(
    eyebrow: String,
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(HoroColors.red),
            )
            Spacer(Modifier.size(eyebrowGap))
            Text(
                eyebrow,
                style = MaterialTheme.typography.labelMedium,
                color = HoroColors.ink60,
            )
            if (trailing != null) {
                Spacer(Modifier.weight(1f))
                trailing()
            }
        }
        Spacer(Modifier.height(titleGap))
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            color = HoroColors.ink,
        )
        Spacer(Modifier.height(ruleGap))
        HorizontalDivider(thickness = 1.dp, color = HoroColors.hairline)
    }
}
