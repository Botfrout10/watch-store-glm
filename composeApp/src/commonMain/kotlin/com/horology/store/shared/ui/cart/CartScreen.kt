package com.horology.store.shared.ui.cart

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.horology.store.shared.Res
import com.horology.store.shared.components.Masthead
import com.horology.store.shared.data.CartItemEntity
import com.horology.store.shared.data.WatchProduct
import com.horology.store.shared.data.formatUsd
import com.horology.store.shared.di.LocalAppContainer
import com.horology.store.shared.ic_close
import com.horology.store.shared.ic_minus
import com.horology.store.shared.ic_plus
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.HoroShapes
import com.horology.store.shared.theme.LocalHoroType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/** M9 — swipe-dismiss rows, springy stepper, count-up total, checkout CTA. */
@Composable
fun CartScreen(
    onCheckout: (Long, Int) -> Unit,
) {    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val lines by container.cart.items.collectAsState(initial = emptyList())
    val catalog by produceState<Map<String, WatchProduct>>(emptyMap()) {
        value = container.catalog.products().associateBy { it.id }
    }

    val joined = lines.mapNotNull { line ->
        catalog[line.productId]?.let { line to it }
    }
    val totalUsd = joined.sumOf { (line, product) -> line.quantity * product.priceUsd }
    val lineCount = joined.size

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Masthead(
            eyebrow = "HOROLOGY Nº 03",
            title = "The Hold",
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp),
        )
        if (joined.isEmpty()) {
            EmptyHold(Modifier.weight(1f))
        } else {
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(joined, key = { it.first.productId }) { (line, product) ->
                    CartRow(
                        line = line,
                        product = product,
                        onRemove = { scope.launch { container.cart.remove(product.id) } },
                        onIncrement = { scope.launch { container.cart.add(product.id) } },
                        onDecrement = { scope.launch { container.cart.decrement(product.id) } },
                        modifier = Modifier.animateItem(
                            fadeInSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
                            placementSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
                        ),
                    )
                }
            }
            Summary(totalUsd = totalUsd, lineCount = lineCount)
            CheckoutBar(
                enabled = lineCount > 0,
                totalUsd = totalUsd,
                onClick = { onCheckout(totalUsd, lineCount) },
            )
            Spacer(Modifier.height(14.dp))
        }
    }
}

/** Swipe-to-dismiss row: redSoft track + close; content sinks (scale/fade) while dragging. */
@Composable
private fun CartRow(
    line: CartItemEntity,
    product: WatchProduct,
    onRemove: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRemove()
                true
            } else {
                false
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.animateContentSize(),
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Row(
                Modifier
                    .fillMaxSize()
                    .clip(HoroShapes.card)
                    .background(HoroColors.redSoft)
                    .border(1.dp, HoroColors.red.copy(alpha = 0.35f), HoroShapes.card)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.weight(1f))
                Icon(
                    painterResource(Res.drawable.ic_close),
                    contentDescription = "Remove ${product.name}",
                    tint = HoroColors.red,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "RELEASE TO RETURN",
                    style = MaterialTheme.typography.labelSmall,
                    color = HoroColors.red,
                )
            }
        },
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(HoroShapes.card)
                .background(HoroColors.card)
                .border(1.dp, HoroColors.hairline, HoroShapes.card)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(84.dp)
                    .clip(HoroShapes.card)
                    .background(HoroColors.steelSoft),
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = HoroColors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(product.reference, style = LocalHoroType.current.Reference, color = HoroColors.ink60)
                Spacer(Modifier.height(6.dp))
                Text(
                    product.priceLabel,
                    style = LocalHoroType.current.PriceM,
                    color = HoroColors.ink,
                )
            }
            Stepper(
                quantity = line.quantity,
                onIncrement = onIncrement,
                onDecrement = onDecrement,
            )
        }
    }
}

/** Springy -/+ roundels around a mono count. */
@Composable
private fun Stepper(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        StepperButton(
            icon = Res.drawable.ic_minus,
            contentDescription = "Remove one",
            onClick = onDecrement,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            "$quantity",
            style = LocalHoroType.current.PriceM,
            color = HoroColors.ink,
            modifier = Modifier.width(22.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.width(6.dp))
        StepperButton(
            icon = Res.drawable.ic_plus,
            contentDescription = "Add one",
            onClick = onIncrement,
        )
    }
}

@Composable
private fun StepperButton(
    icon: org.jetbrains.compose.resources.DrawableResource,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (pressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 700f),
        label = "stepperPress",
    )
    Box(
        Modifier
            .size(30.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(HoroColors.card)
            .border(1.dp, HoroColors.hairline, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painterResource(icon),
            contentDescription = contentDescription,
            tint = HoroColors.ink,
            modifier = Modifier.size(14.dp),
        )
    }
}

/** Mono receipt: lines, hairlines, count-up grand total. */
@Composable
private fun Summary(totalUsd: Long, lineCount: Int) {
    val counter = remember { Animatable(totalUsd.toFloat()) }
    LaunchedEffect(totalUsd) {
        counter.animateTo(
            totalUsd.toFloat(),
            tween(450, easing = EaseOutCubic),
        )
    }
    val shown = counter.value.toLong()
    Column(Modifier.padding(horizontal = 20.dp)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(HoroColors.hairline))
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Column(Modifier.weight(1f)) {
                Text(
                    "$lineCount LINE${if (lineCount == 1) "" else "S"} ON HOLD",
                    style = LocalHoroType.current.MonoCaption,
                    color = HoroColors.ink60,
                )
                Spacer(Modifier.height(2.dp))
                Text("DUTIES & DELIVERY ARRANGED", style = LocalHoroType.current.MonoCaption, color = HoroColors.ink60)
            }
            Text(
                formatUsd(shown),
                style = LocalHoroType.current.PriceXL,
                color = HoroColors.ink,
            )
        }
        Spacer(Modifier.height(10.dp))
    }
}

/** Ink CTA → The Vault. */
@Composable
private fun CheckoutBar(enabled: Boolean, totalUsd: Long, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (pressed && enabled) 0.985f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 600f),
        label = "checkoutPress",
    )
    Box(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (enabled) 1f else 0.4f
            }
            .clip(HoroShapes.card)
            .background(if (enabled) HoroColors.ink else HoroColors.steelSoft)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "RESERVE THE VAULT — ${formatUsd(totalUsd)}",
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) HoroColors.paper else HoroColors.ink60,
        )
    }
}

@Composable
private fun EmptyHold(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("THE HOLD IS EMPTY", style = MaterialTheme.typography.headlineSmall, color = HoroColors.ink)
        Spacer(Modifier.height(6.dp))
        Text(
            "Reserve a reference from the boutique and it will wait for you here.",
            style = MaterialTheme.typography.bodyMedium,
            color = HoroColors.ink60,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}
