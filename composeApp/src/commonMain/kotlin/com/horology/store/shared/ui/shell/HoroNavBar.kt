package com.horology.store.shared.ui.shell

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.horology.store.shared.Res
import com.horology.store.shared.di.LocalAppContainer
import com.horology.store.shared.ic_cart
import com.horology.store.shared.ic_heart
import com.horology.store.shared.ic_heart_filled
import com.horology.store.shared.ic_home
import com.horology.store.shared.ic_search
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.LocalHoroType
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.absoluteValue

/**
 * Swiss tab bar per DESIGN.md: flat white on a hairline, mono labels, ONE
 * sliding red indicator dot (the only live accent), cart badge with pop.
 */
@Composable
fun HoroNavBar(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cartCount by LocalAppContainer.current.cart.count.collectAsState(0)
    val dotX = remember { Animatable(selected.toFloat()) }

    LaunchedEffect(selected) {
        dotX.animateTo(
            selected.toFloat(),
            spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow),
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(HoroColors.card)
            .navigationBarsPadding(),
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(HoroColors.hairline))
        BoxWithConstraints(Modifier.fillMaxWidth().height(56.dp)) {
            val tabW = maxWidth / 4f
            Box(
                Modifier
                    .offset(x = tabW * dotX.value + tabW / 2f - 2.dp)
                    .size(4.dp)
                    .background(HoroColors.red),
            )
            Row(Modifier.fillMaxSize()) {
                TabCell(
                    label = "Home",
                    icon = Res.drawable.ic_home,
                    selected = selected == 0,
                    badgeCount = 0,
                    onClick = { onSelect(0) },
                    modifier = Modifier.weight(1f),
                )
                TabCell(
                    label = "Catalog",
                    icon = Res.drawable.ic_search,
                    selected = selected == 1,
                    badgeCount = 0,
                    onClick = { onSelect(1) },
                    modifier = Modifier.weight(1f),
                )
                TabCell(
                    label = "Favorites",
                    icon = Res.drawable.ic_heart,
                    selectedIcon = Res.drawable.ic_heart_filled,
                    selected = selected == 2,
                    badgeCount = 0,
                    onClick = { onSelect(2) },
                    modifier = Modifier.weight(1f),
                )
                TabCell(
                    label = "Cart",
                    icon = Res.drawable.ic_cart,
                    selected = selected == 3,
                    badgeCount = cartCount,
                    onClick = { onSelect(3) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun TabCell(
    label: String,
    icon: DrawableResource,
    selectedIcon: DrawableResource? = null,
    selected: Boolean,
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(13.dp))
        Box {
            Icon(
                painter = painterResource(if (selected) selectedIcon ?: icon else icon),
                contentDescription = label,
                tint = if (selected) HoroColors.ink else HoroColors.ink60,
                modifier = Modifier.size(22.dp),
            )
            if (badgeCount > 0) {
                CartBadge(
                    count = badgeCount,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 7.dp, y = (-3).dp),
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = LocalHoroType.current.MonoCaption,
            color = if (selected) HoroColors.ink else HoroColors.ink60,
        )
        Spacer(Modifier.height(2.dp))
    }
}

/** Red count pill — pops (spring overshoot) whenever the count changes. */
@Composable
private fun CartBadge(count: Int, modifier: Modifier = Modifier) {
    val pop = remember { Animatable(1f) }
    LaunchedEffect(count) {
        if (count > 0) {
            pop.snapTo(0.55f)
            pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
        }
    }
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pop.value
                scaleY = pop.value
            }
            .clip(CircleShape)
            .background(HoroColors.red)
            .height(15.dp)
            .width(15.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "$count".take(3),
            style = LocalHoroType.current.MonoCaption,
            color = Color.White,
        )
    }
}
