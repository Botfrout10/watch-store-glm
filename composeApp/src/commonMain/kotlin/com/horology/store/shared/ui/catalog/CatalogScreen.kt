package com.horology.store.shared.ui.catalog

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.horology.store.shared.Res
import com.horology.store.shared.components.Masthead
import com.horology.store.shared.components.ProductCard
import com.horology.store.shared.data.Categories
import com.horology.store.shared.data.WatchProduct
import com.horology.store.shared.di.LocalAppContainer
import com.horology.store.shared.ic_close
import com.horology.store.shared.ic_search
import com.horology.store.shared.motion.motionDurationScale
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.HoroShapes
import com.horology.store.shared.theme.LocalHoroType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/** M7 — sticky search w/ morphing underline, filter chips, debounced refine + skeleton pulse. */
@Composable
fun CatalogScreen(
    onOpenProduct: (String) -> Unit,
) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val motion = remember { motionDurationScale() }
    val products by produceState<List<WatchProduct>?>(null) {
        value = container.catalog.products()
    }
    val favorites by remember(container) { container.favorites.ids() }.collectAsState(emptySet())

    var search by rememberSaveable { mutableStateOf("") }
    var effectiveSearch by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(Categories.All) }

    // Debounced query: the visible list follows the raw input after 260ms of quiet.
    LaunchedEffect(search) {
        if (search == effectiveSearch) return@LaunchedEffect
        delay(260)
        effectiveSearch = search
    }

    val refining = remember { mutableStateOf(false) }
    LaunchedEffect(effectiveSearch, category, products) {
        if (products == null) return@LaunchedEffect
        refining.value = true
        delay((240 * motion).toLong())
        refining.value = false
    }

    val all = products.orEmpty()
    val filtered = all.filter { product ->
        (category == Categories.All || product.category == category) &&
            matchesQuery(product, effectiveSearch)
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding(),
    ) {
        Masthead(
            eyebrow = "HOROLOGY Nº 02",
            title = "The Inventory",
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp),
        )
        SearchField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp),
        )
        FilterChipsRow(
            selected = category,
            onSelect = { category = it },
            modifier = Modifier.padding(top = 12.dp),
        )
        AnimatedContent(
            targetState = refining.value,
            transitionSpec = {
                fadeIn(tween((150 * motion).toInt())) togetherWith fadeOut(tween((150 * motion).toInt()))
            },
            label = "catalogGridSwap",
            modifier = Modifier.weight(1f),
        ) { refiningNow ->
            if (refiningNow) {
                SkeletonGrid(Modifier.fillMaxSize())
            } else if (filtered.isEmpty()) {
                EmptyState(
                    query = effectiveSearch,
                    onClear = {
                        search = ""
                        effectiveSearch = ""
                        category = Categories.All
                    },
                )
            } else {
                CatalogGrid(
                    products = filtered,
                    favorites = favorites,
                    onOpenProduct = onOpenProduct,
                    onToggleFavorite = { id ->
                        scope.launch { container.favorites.toggle(id) }
                    },
                )
            }
        }
    }
}

/** Case-insensitive match on name, house, collection and reference. */
private fun matchesQuery(product: WatchProduct, query: String): Boolean {
    if (query.isBlank()) return true
    val q = query.trim().lowercase()
    return product.name.lowercase().contains(q) ||
        product.house.lowercase().contains(q) ||
        product.collection.lowercase().contains(q) ||
        product.reference.lowercase().contains(q)
}

/** Hairline-ruled search field; a red underline grows under it while focused. */
@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }
    val motion = remember { motionDurationScale() }
    val underline by animateFloatAsState(
        targetValue = if (focused) 1f else 0f,
        animationSpec = tween((220 * motion).toInt()),
        label = "searchUnderline",
    )

    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painterResource(Res.drawable.ic_search),
                contentDescription = null,
                tint = if (focused) HoroColors.ink else HoroColors.ink60,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        "Search inventory",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HoroColors.ink60,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = HoroColors.ink),
                    cursorBrush = SolidColor(HoroColors.red),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focused = it.isFocused },
                )
            }
            if (value.isNotEmpty()) {
                Spacer(Modifier.width(10.dp))
                Icon(
                    painterResource(Res.drawable.ic_close),
                    contentDescription = "Clear search",
                    tint = HoroColors.ink60,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onValueChange("") },
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(1.dp)) {
            Box(Modifier.fillMaxSize().background(HoroColors.hairline))
            Box(
                Modifier
                    .fillMaxWidth(underline.coerceAtLeast(0.001f))
                    .height(1.dp)
                    .background(HoroColors.red),
            )
        }
    }
}

/** Flat chips; the active one inverts to ink. */
@Composable
private fun FilterChipsRow(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(listOf(Categories.All) + Categories.filterable) { chip ->
            CategoryChip(
                label = chip,
                selected = chip == selected,
                onClick = { onSelect(chip) },
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
        label = "chipPress",
    )
    Box(
        Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(2.dp))
            .background(if (selected) HoroColors.ink else HoroColors.card)
            .border(
                1.dp,
                if (selected) HoroColors.ink else HoroColors.hairline,
                RoundedCornerShape(2.dp),
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) HoroColors.paper else HoroColors.ink60,
        )
    }
}

/** 2-col editorial grid; whole grid fades in over 150ms after each refine. */
@Composable
private fun CatalogGrid(
    products: List<WatchProduct>,
    favorites: Set<String>,
    onOpenProduct: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
) {
    val fade = remember { Animatable(1f) }
    val motion = remember { motionDurationScale() }
    LaunchedEffect(products) {
        fade.snapTo(0f)
        fade.animateTo(1f, tween((150 * motion).toInt()))
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().graphicsLayer { alpha = fade.value },
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        items(products, key = { it.id }) { product ->
            ProductCard(
                imageUrl = product.imageUrl,
                name = product.name,
                reference = product.reference,
                price = product.priceLabel,
                favorite = favorites.contains(product.id),
                onToggleFavorite = { onToggleFavorite(product.id) },
                onClick = { onOpenProduct(product.id) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** Ghost grid: steel blocks pulsing while the list refines. */
@Composable
private fun SkeletonGrid(modifier: Modifier = Modifier) {
    val motion = remember { motionDurationScale() }
    val pulse = rememberInfiniteTransition(label = "skeletonPulse")
    val alpha by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween((560 * motion).toInt(), easing = LinearEasing)),
        label = "pulseAlpha",
    )
    Column(
        modifier.fillMaxSize().graphicsLayer { this.alpha = alpha },
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        repeat(2) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(2) {
                    Box(
                        Modifier
                            .weight(1f)
                            .aspectRatio(1.22f)
                            .clip(HoroShapes.card)
                            .background(HoroColors.steelSoft),
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(2) {
                    Column(Modifier.weight(1f)) {
                        Bar(0.62f)
                        Spacer(Modifier.height(6.dp))
                        Bar(0.4f)
                        Spacer(Modifier.height(6.dp))
                        Bar(0.3f)
                    }
                }
            }
        }
    }
}

@Composable
private fun Bar(widthFraction: Float) {
    Box(
        Modifier
            .fillMaxWidth(widthFraction)
            .height(10.dp)
            .clip(HoroShapes.card)
            .background(HoroColors.steelSoft),
    )
}

@Composable
private fun EmptyState(
    query: String,
    onClear: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Text(
            "NO REFERENCES",
            style = MaterialTheme.typography.headlineSmall,
            color = HoroColors.ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            if (query.isBlank()) {
                "This collection is empty — adjust the chips above or clear the filters."
            } else {
                "Nothing in the register matches \"$query\"."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = HoroColors.ink60,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.height(14.dp))
        Box(
            Modifier
                .clip(HoroShapes.card)
                .background(HoroColors.card)
                .border(1.dp, HoroColors.hairline, HoroShapes.card)
                .clickable { onClear() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Text(
                "CLEAR FILTERS",
                style = MaterialTheme.typography.labelSmall,
                color = HoroColors.ink,
            )
        }
        Spacer(Modifier.weight(1f))
    }
}
