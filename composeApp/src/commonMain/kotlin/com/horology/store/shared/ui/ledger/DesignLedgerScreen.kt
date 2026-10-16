package com.horology.store.shared.ui.ledger

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.horology.store.shared.Res
import com.horology.store.shared.components.HoroButton
import com.horology.store.shared.components.HoroButtonStyle
import com.horology.store.shared.components.HoroChip
import com.horology.store.shared.components.Masthead
import com.horology.store.shared.components.ProductCard
import com.horology.store.shared.ic_back
import com.horology.store.shared.ic_cart
import com.horology.store.shared.ic_check
import com.horology.store.shared.ic_close
import com.horology.store.shared.ic_heart_filled
import com.horology.store.shared.ic_search
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.LocalHoroType
import org.jetbrains.compose.resources.painterResource

private val screenMargin = 20.dp
private val sectionGap = 40.dp
private val itemGap = 12.dp

const val SPEC_URL =
    "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1" + "?auto=format&fit=crop&w=1200&q=80"

/**
 * Design ledger: temp screen proving palette / type / components before real screens land.
 * Replaced by Home at the navigation milestone.
 */
@Composable
fun DesignLedgerScreen() {
    var chipSelected by remember { mutableStateOf("CHRONOGRAPH") }
    var favorite by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(HoroColors.paper)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = screenMargin, vertical = 28.dp),
    ) {
        // --- type specimen ---
        Masthead("Nº 01 — TYPE", "Light minimal Swiss")
        Spacer(Modifier.height(itemGap))
        Text("HOROLOGY", style = MaterialTheme.typography.displayLarge, color = HoroColors.ink)
        Text("$12,480", style = LocalHoroType.current.PriceXL, color = HoroColors.ink)
        Text("REF. MG.1941/SB", style = LocalHoroType.current.Reference, color = HoroColors.ink60)
        Text(
            "Precision before ornament. Every numeral is set in IBM Plex Mono; every headline in Space Grotesk.",
            style = MaterialTheme.typography.bodyMedium,
            color = HoroColors.inkSoft,
        )

        Spacer(Modifier.height(sectionGap))
        Masthead("ACTIONS", "Buttons")
        Spacer(Modifier.height(itemGap))
        HoroButton("Explore the collection", Modifier.fillMaxWidth(), onClick = {})
        Spacer(Modifier.height(itemGap))
        HoroButton("Add to cart", Modifier.fillMaxWidth(), style = HoroButtonStyle.Secondary, onClick = {})
        Spacer(Modifier.height(itemGap))
        HoroButton("Skip for now", Modifier.fillMaxWidth(), style = HoroButtonStyle.Text, onClick = {})

        Spacer(Modifier.height(sectionGap))
        Masthead("FILTERS", "Collections")
        Spacer(Modifier.height(itemGap))
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("DIVER", "CHRONOGRAPH", "MINIMAL", "FIELD", "TOURBILLON").forEach { label ->
                HoroChip(label, selected = chipSelected == label, onClick = { chipSelected = label })
            }
        }

        Spacer(Modifier.height(sectionGap))
        Masthead("CATALOG", "Featured")
        Spacer(Modifier.height(itemGap))
        ProductCard(
            imageUrl = SPEC_URL,
            name = "Meridian Chronograph",
            reference = "REF. MR.1968/ST",
            price = "$18,240",
            favorite = favorite,
            onToggleFavorite = { favorite = !favorite },
            onClick = {},
        )

        Spacer(Modifier.height(sectionGap))
        Masthead("SYSTEM", "Iconography")
        Spacer(Modifier.height(itemGap))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            listOf(
                Res.drawable.ic_back,
                Res.drawable.ic_search,
                Res.drawable.ic_heart_filled,
                Res.drawable.ic_cart,
                Res.drawable.ic_check,
                Res.drawable.ic_close,
            ).forEach { icon ->
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    tint = HoroColors.ink,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}
