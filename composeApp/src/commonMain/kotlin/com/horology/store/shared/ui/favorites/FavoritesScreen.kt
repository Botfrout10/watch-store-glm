package com.horology.store.shared.ui.favorites

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horology.store.shared.Res
import com.horology.store.shared.components.Masthead
import com.horology.store.shared.components.ProductCard
import com.horology.store.shared.data.WatchProduct
import com.horology.store.shared.di.LocalAppContainer
import com.horology.store.shared.ic_heart
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.HoroShapes
import com.horology.store.shared.theme.LocalHoroType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/** M10 — the collector's notes: pre-filled hearts, springy re-layout, shrink on unfill. */
@Composable
fun FavoritesScreen(
    onOpenProduct: (String) -> Unit = {},
    onBrowseCatalog: () -> Unit = {},
) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val notes by remember(container) { container.favorites.full() }.collectAsState(initial = emptyList())
    val catalog by produceState<Map<String, WatchProduct>>(emptyMap()) {
        value = container.catalog.products().associateBy { it.id }
    }
    val favorites by remember(container) { container.favorites.ids() }.collectAsState(emptySet())

    val joined = notes.mapNotNull { note -> catalog[note.productId] }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Masthead(
            eyebrow = "HOROLOGY Nº 04",
            title = "The Horologists' Notes",
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp),
        )
        NotesHeader(count = joined.size)
        if (joined.isEmpty()) {
            EmptyNotes(onBrowseCatalog)
        } else {
            NotesGrid(
                products = joined,
                favorites = favorites,
                onOpenProduct = onOpenProduct,
                onUnfavorite = { id -> scope.launch { container.favorites.toggle(id) } },
            )
        }
    }
}

/** Red mono label + live count, mirroring Home's SELECTION header. */
@Composable
private fun NotesHeader(count: Int) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "NOTES ON FILE",
            style = MaterialTheme.typography.labelSmall,
            color = HoroColors.red,
        )
        Spacer(Modifier.weight(1f))
        Text(
            if (count == 1) "1 REFERENCE" else "$count REFERENCES",
            style = LocalHoroType.current.MonoCaption,
            color = HoroColors.ink60,
        )
    }
    Box(
        Modifier
            .padding(start = 20.dp, end = 20.dp, top = 10.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(HoroColors.hairline),
    )
}

/** 2-col grid of the same product cards; hearts arrive pre-filled. */
@Composable
private fun NotesGrid(
    products: List<WatchProduct>,
    favorites: Set<String>,
    onOpenProduct: (String) -> Unit,
    onUnfavorite: (String) -> Unit,
) {
    val fade = remember { Animatable(0f) }
    LaunchedEffect(products) {
        fade.snapTo(0f)
        fade.animateTo(1f, tween(220))
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = fade.value },
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
                onToggleFavorite = { onUnfavorite(product.id) },
                onClick = { onOpenProduct(product.id) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun EmptyNotes(onBrowseCatalog: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painterResource(Res.drawable.ic_heart),
            contentDescription = null,
            tint = HoroColors.steel,
            modifier = Modifier.size(44.dp),
        )
        Spacer(Modifier.height(14.dp))
        Text(
            "NO NOTES YET",
            style = MaterialTheme.typography.headlineSmall,
            color = HoroColors.ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Mark a reference with the heart and it will be kept on file here.",
            style = MaterialTheme.typography.bodyMedium,
            color = HoroColors.ink60,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            Modifier
                .clip(HoroShapes.card)
                .background(HoroColors.card)
                .border(1.dp, HoroColors.hairline, HoroShapes.card)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onBrowseCatalog,
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Text(
                "BROWSE THE CATALOG",
                style = MaterialTheme.typography.labelSmall,
                color = HoroColors.ink,
            )
        }
    }
}
