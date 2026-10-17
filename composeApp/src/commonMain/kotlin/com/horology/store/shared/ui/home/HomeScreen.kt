package com.horology.store.shared.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.horology.store.shared.components.Masthead
import com.horology.store.shared.components.ProductCard
import com.horology.store.shared.data.Categories
import com.horology.store.shared.data.FeaturedProducts
import com.horology.store.shared.data.WatchProduct
import com.horology.store.shared.di.LocalAppContainer
import com.horology.store.shared.motion.motionDurationScale
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.HoroShapes
import com.horology.store.shared.theme.LocalHoroType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** M6 — masthead, featured hero w/ scroll parallax, collections rail, staggered reference grid. */
@Composable
fun HomeScreen(
    onOpenProduct: (String) -> Unit,
    onOpenCatalog: () -> Unit,
) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val motion = remember { motionDurationScale() }
    val products by produceState<List<WatchProduct>?>(null) {
        value = container.catalog.products()
    }
    val favorites by remember(container) { container.favorites.ids() }.collectAsState(emptySet())
    val listState = rememberLazyListState()

    val hero = products?.firstOrNull { it.id == FeaturedProducts.first() }
    val grid = products?.filter { it.id != hero?.id }.orEmpty().chunked(2)

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item(key = "masthead") {
            Masthead(
                eyebrow = "HOROLOGY Nº 01",
                title = "The Boutique",
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp),
            )
        }
        item(key = "hero") {
            if (hero != null) {
                HeroBlock(
                    product = hero,
                    listState = listState,
                    onOpen = { onOpenProduct(hero.id) },
                )
            } else {
                // Paper skeleton while the bundled catalog parses (sub-frame in practice).
                Box(
                    Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(HoroShapes.card)
                        .background(HoroColors.steelSoft),
                )
            }
        }
        item(key = "collections") {
            CollectionsRail(
                products.orEmpty(),
                onOpenCatalog = onOpenCatalog,
            )
        }
        item(key = "selection-header") {
            Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("SELECTION", style = LocalHoroType.current.MonoCaption, color = HoroColors.red)
                    Spacer(Modifier.weight(1f))
                    Text(
                        "${grid.sumOf { it.size }} REFERENCES",
                        style = LocalHoroType.current.MonoCaption,
                        color = HoroColors.ink60,
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        itemsIndexed(grid, key = { i, row -> "grid-$i-${row.first().id}" }) { rowIndex, row ->
            Row(
                Modifier.padding(start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEachIndexed { colIndex, product ->
                    val staggerIndex = rowIndex * 2 + colIndex
                    GridStagger(
                        index = staggerIndex,
                        motion = motion,
                        modifier = Modifier.weight(1f),
                    ) {
                        ProductCard(
                            imageUrl = product.imageUrl,
                            name = product.name,
                            reference = product.reference,
                            price = product.priceLabel,
                            favorite = favorites.contains(product.id),
                            onToggleFavorite = {
                                scope.launch { container.favorites.toggle(product.id) }
                            },
                            onClick = { onOpenProduct(product.id) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(14.dp))
        }
    }
}

/** Featured 1:1 hero; the image drifts slower than the scroll (parallax) inside its clip. */
@Composable
private fun HeroBlock(
    product: WatchProduct,
    listState: LazyListState,
    onOpen: () -> Unit,
) {    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.99f else 1f,
        animationSpec = spring(stiffness = 500f, dampingRatio = 0.8f),
        label = "heroPress",
    )

    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(HoroShapes.card)
                .background(HoroColors.steelSoft)
                .clickable(interactionSource = interactionSource, indication = null, onClick = onOpen)
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                },
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Parallax: as long as the masthead+hero item is leaving the top,
                        // the image drifts down at 0.38× while the page scrolls at 1×.
                        // Oversized 1.3× so the clip never shows paper behind it.
                        scaleX = 1f
                        scaleY = 1.3f
                        val scroll = if (listState.firstVisibleItemIndex == 0) {
                            listState.firstVisibleItemScrollOffset.toFloat()
                        } else {
                            size.height
                        }
                        translationY = (scroll * 0.38f).coerceAtMost(size.height * 0.145f)
                    },
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Column(Modifier.weight(1f)) {
                Text(
                    "${product.house} · ${product.collection}",
                    style = LocalHoroType.current.MonoCaption,
                    color = HoroColors.ink60,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = HoroColors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(product.priceLabel, style = LocalHoroType.current.PriceL, color = HoroColors.ink)
        }
        Spacer(Modifier.height(6.dp))
    }
}

/** CATEGORY rail: press-scale 0.97 plates, thin red border glow while the rail is being dragged. */
@Composable
private fun CollectionsRail(
    products: List<WatchProduct>,
    onOpenCatalog: () -> Unit,
) {
    val railState = rememberLazyListState()
    val glow by animateColorAsState(
        targetValue = if (railState.isScrollInProgress) HoroColors.red.copy(alpha = 0.38f) else HoroColors.hairline,
        animationSpec = tween(180),
        label = "railGlow",
    )

    Column(Modifier.padding(top = 24.dp)) {
        Text(
            "COLLECTIONS",
            style = LocalHoroType.current.MonoCaption,
            color = HoroColors.ink60,
            modifier = Modifier.padding(start = 20.dp),
        )
        Spacer(Modifier.height(10.dp))
        LazyRow(
            state = railState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(Categories.filterable) { category ->
                val representative = products.firstOrNull { it.category == category }
                RailPlate(
                    label = category,
                    imageUrl = representative?.imageUrl,
                    glow = glow,
                    onClick = onOpenCatalog,
                )
            }
        }
    }
}

@Composable
private fun RailPlate(
    label: String,
    imageUrl: String?,
    glow: Color,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
        label = "railPress",
    )

    Column(
        Modifier
            .width(128.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(2.dp))
            .background(HoroColors.card)
            .border(1.dp, glow, RoundedCornerShape(2.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(88.dp)
                .background(HoroColors.steelSoft),
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
            }
        }
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.titleSmall,
            color = HoroColors.ink,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** 75ms-per-card cascade entrance for the reference grid (per DESIGN.md). */
@Composable
private fun GridStagger(
    index: Int,
    motion: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay((75L * index.coerceAtMost(5) * motion).roundToInt().toLong())
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween((320f * motion).roundToInt()),
        )
    }
    Box(
        modifier.graphicsLayer {
            alpha = progress.value
            translationY = 24.dp.toPx() * (1f - progress.value)
        },
    ) {
        content()
    }
}
