package com.horology.store.shared.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.horology.store.shared.Res
import com.horology.store.shared.ic_heart
import com.horology.store.shared.ic_heart_filled
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.HoroShapes
import com.horology.store.shared.theme.LocalHoroType
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource

/** 1:1 editorial product card: image, red-dot favorite roundel, grotesk name, mono price. */
@Composable
fun ProductCard(
    imageUrl: String,
    name: String,
    reference: String,
    price: String,
    favorite: Boolean,
    modifier: Modifier = Modifier,
    onToggleFavorite: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 600f),
        label = "cardPress",
    )

    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .clip(HoroShapes.card)
            .background(HoroColors.card)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(
                    Brush.linearGradient(
                        listOf(HoroColors.steelSoft, HoroColors.paper),
                    ),
                ),
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
            FavoriteBadge(
                favorite = favorite,
                onToggle = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
            )
        }
        Column(Modifier.padding(14.dp)) {
            Text(
                name,
                style = MaterialTheme.typography.titleMedium,
                color = HoroColors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.size(2.dp))
            Text(reference, style = LocalHoroType.current.Reference, color = HoroColors.ink60)
            Spacer(Modifier.size(6.dp))
            Text(price, style = LocalHoroType.current.PriceM, color = HoroColors.ink)
        }
    }
}

/** White roundel + heart; burst-springs and fills red when favorited. */
@Composable
private fun FavoriteBadge(
    favorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pop = remember { Animatable(1f) }
    LaunchedEffect(favorite) {
        if (favorite) {
            pop.snapTo(0.7f)
            pop.animateTo(
                1f,
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            )
        }
    }
    val tint by animateColorAsState(
        targetValue = if (favorite) HoroColors.red else HoroColors.ink,
        animationSpec = tween(150),
        label = "heartTint",
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.88f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painterResource(if (favorite) Res.drawable.ic_heart_filled else Res.drawable.ic_heart),
            contentDescription = if (favorite) "Remove from favorites" else "Add to favorites",
            tint = tint,
            modifier = Modifier
                .size(18.dp)
                .graphicsLayer {
                    scaleX = pop.value
                    scaleY = pop.value
                },
        )
    }
}
