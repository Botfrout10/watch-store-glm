package com.horology.store.shared.ui.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.horology.store.shared.Res
import com.horology.store.shared.data.WatchProduct
import com.horology.store.shared.di.LocalAppContainer
import com.horology.store.shared.ic_back
import com.horology.store.shared.ic_check
import com.horology.store.shared.ic_chevron_down
import com.horology.store.shared.ic_heart
import com.horology.store.shared.ic_heart_filled
import com.horology.store.shared.motion.motionDurationScale
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.HoroShapes
import com.horology.store.shared.theme.LocalHoroType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

private val SpecLabelWidth = 118.dp

/** M8 — gallery, spec accordion, heart burst, add-to-cart morph. */
@Composable
fun ProductDetailScreen(
    productId: String,
    onBack: () -> Unit,
) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    // Pair: (found product, catalog load completed). Null+!loaded = parsing; Null+loaded = unknown ref.
    val state by produceState<Pair<WatchProduct?, Boolean>>(null to false, productId) {
        val product = container.catalog.byId(productId)
        value = product to true
    }
    val product = state.first
    val loaded = state.second
    val favorites by remember(container) { container.favorites.ids() }.collectAsState(emptySet())

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        TopBar(
            favorite = product != null && favorites.contains(product.id),
            onBack = onBack,
            onToggle = { product?.let { scope.launch { container.favorites.toggle(it.id) } } },
        )
        when {
            product != null ->
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Gallery(product)
                    SpecSheet(product)
                    Spacer(Modifier.height(96.dp)) // CTA clearance
                }
            loaded ->
                NotFound(Modifier.weight(1f))
            else ->
                LoadingBlock(Modifier.weight(1f))
        }
        if (product != null) {
            AddToCartBar(
                product = product,
                onAdd = { scope.launch { container.cart.add(product.id) } },
            )
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun TopBar(
    favorite: Boolean,
    onBack: () -> Unit,
    onToggle: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 24.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painterResource(Res.drawable.ic_back),
            contentDescription = "Back",
            tint = HoroColors.ink,
            modifier = Modifier
                .size(40.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBack,
                ),
        )
        Spacer(Modifier.weight(1f))
        BurstHeart(
            favorite = favorite,
            onToggle = onToggle,
        )
    }
}

/** NOT IN REGISTER state. */
@Composable
private fun NotFound(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "NOT IN THE REGISTER",
            style = MaterialTheme.typography.headlineSmall,
            color = HoroColors.ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "This reference left the boutique long ago.",
            style = MaterialTheme.typography.bodyMedium,
            color = HoroColors.ink60,
            textAlign = TextAlign.Center,
        )
    }
}

/** Paper skeleton while the catalog parses. */
@Composable
private fun LoadingBlock(modifier: Modifier = Modifier) {
    val motion = remember { motionDurationScale() }
    val pulse = rememberInfiniteTransition(label = "detailSkeleton")
    val alpha by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween((560 * motion).toInt(), easing = LinearEasing)),
        label = "pulseAlpha",
    )
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .graphicsLayer { this.alpha = alpha },
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
                .clip(HoroShapes.card)
                .background(HoroColors.steelSoft),
        )
        Box(Modifier.fillMaxWidth(0.5f).height(14.dp).clip(HoroShapes.card).background(HoroColors.steelSoft))
        Box(Modifier.fillMaxWidth(0.3f).height(10.dp).clip(HoroShapes.card).background(HoroColors.steelSoft))
    }
}

/** Featured image; Coil crossfades on load. */
@Composable
private fun Gallery(product: WatchProduct) {
    AsyncImage(
        model = product.imageUrl,
        contentDescription = product.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 14.dp)
            .fillMaxWidth()
            .aspectRatio(1.15f)
            .clip(HoroShapes.card)
            .background(HoroColors.steelSoft),
    )
}

/** Identity block + expandable dial spec accordion (chevron rotate + animateContentSize). */
@Composable
private fun SpecSheet(product: WatchProduct) {
    var expanded by remember { mutableStateOf(false) }
    val chevron by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "chevron",
    )

    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp)) {
        Text(
            "${product.house} · ${product.collection} · ${product.year}",
            style = LocalHoroType.current.MonoCaption,
            color = HoroColors.ink60,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            product.name,
            style = MaterialTheme.typography.headlineSmall,
            color = HoroColors.ink,
        )
        Text(product.reference, style = LocalHoroType.current.Reference, color = HoroColors.ink60)
        Spacer(Modifier.height(6.dp))
        Text(product.priceLabel, style = LocalHoroType.current.PriceXL, color = HoroColors.ink)
        Spacer(Modifier.height(10.dp))
        Text(
            product.description,
            style = MaterialTheme.typography.bodyMedium,
            color = HoroColors.ink60,
        )
        Spacer(Modifier.height(14.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .clip(HoroShapes.card)
                .background(HoroColors.card)
                .border(1.dp, HoroColors.hairline, HoroShapes.card)
                .animateContentSize(),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("SPECIFICATIONS", style = MaterialTheme.typography.labelSmall, color = HoroColors.ink)
                Spacer(Modifier.weight(1f))
                Icon(
                    painterResource(Res.drawable.ic_chevron_down),
                    contentDescription = if (expanded) "Collapse specifications" else "Expand specifications",
                    tint = HoroColors.ink60,
                    modifier = Modifier.size(18.dp).rotate(chevron),
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)) {
                    SpecRow("Movement", product.movement)
                    SpecRow("Case", product.caseSpecs)
                    SpecRow("Water resistance", product.waterResistance)
                    SpecRow("Dial", product.dialColor)
                    SpecRow("Strap", product.strapMaterial)
                    SpecRow("Year", "${product.year}")
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 9.dp)) {
        HorizontalDivider(thickness = 1.dp, color = HoroColors.hairline)
        Spacer(Modifier.height(9.dp))
        Row {
            Text(
                label.uppercase(),
                style = LocalHoroType.current.MonoCaption,
                color = HoroColors.ink60,
                modifier = Modifier.width(SpecLabelWidth),
            )
            Text(
                value,
                style = LocalHoroType.current.MonoCaption,
                color = HoroColors.ink,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Heart burst: spring 1 -> 1.35 -> 1 plus a redSoft ring pulse. */
@Composable
private fun BurstHeart(
    favorite: Boolean,
    onToggle: () -> Unit,
) {
    val motion = remember { motionDurationScale() }
    val scale = remember { Animatable(1f) }
    val ring = remember { Animatable(0f) }

    LaunchedEffect(favorite) {
        if (favorite) {
            scale.snapTo(1f)
            scale.animateTo(1.35f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
            scale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
            ring.snapTo(0f)
            ring.animateTo(1f, tween((420 * motion).toInt()))
        } else {
            scale.snapTo(1f)
            ring.snapTo(0f)
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    Box(contentAlignment = Alignment.Center) {
        if (favorite) {
            // Ring pulse fades outward around the heart.
            Box(
                Modifier
                    .size(36.dp)
                    .graphicsLayer {
                        val s = 0.6f + ring.value * 0.9f
                        scaleX = s
                        scaleY = s
                        alpha = 1f - ring.value
                    }
                    .border(2.dp, HoroColors.red.copy(alpha = 0.35f), CircleShape),
            )
        }
        Icon(
            painterResource(if (favorite) Res.drawable.ic_heart_filled else Res.drawable.ic_heart),
            contentDescription = if (favorite) "Remove from favorites" else "Add to favorites",
            tint = if (favorite) HoroColors.red else HoroColors.ink,
            modifier = Modifier
                .size(26.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onToggle,
                ),
        )
    }
}

/** Ink bar that morphs to a redSoft ADDED ✓ chip and reverts after a beat. */
@Composable
private fun AddToCartBar(
    product: WatchProduct,
    onAdd: () -> Unit,
) {
    val motion = remember { motionDurationScale() }
    var added by remember { mutableStateOf(false) }
    LaunchedEffect(added) {
        if (added) {
            delay(1400)
            added = false
        }
    }
    val interactionSource = remember { MutableInteractionSource() }
    AnimatedContent(
        targetState = added,
        transitionSpec = {
            fadeIn(tween((160 * motion).toInt())) togetherWith fadeOut(tween((160 * motion).toInt()))
        },
        label = "addToCartMorph",
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
    ) { isAdded ->
        Box(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(HoroShapes.card)
                .background(if (isAdded) HoroColors.redSoft else HoroColors.ink)
                .border(
                    1.dp,
                    if (isAdded) HoroColors.red else HoroColors.ink,
                    HoroShapes.card,
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) { if (!isAdded) { added = true; onAdd() } },
            contentAlignment = Alignment.Center,
        ) {
            if (isAdded) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painterResource(Res.drawable.ic_check),
                        contentDescription = null,
                        tint = HoroColors.red,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "ADDED TO CART",
                        style = MaterialTheme.typography.labelSmall,
                        color = HoroColors.red,
                    )
                }
            } else {
                Text(
                    "ADD TO CART — ${product.priceLabel}",
                    style = MaterialTheme.typography.labelSmall,
                    color = HoroColors.paper,
                )
            }
        }
    }
}
