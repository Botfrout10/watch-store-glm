package com.horology.store.shared.ui.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horology.store.shared.components.Masthead
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.LocalHoroType

/** M4 stub — search field, filter chips and the refined grid land in M7. */
@Composable
fun CatalogScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Masthead(
            eyebrow = "HOROLOGY Nº 02",
            title = "The Inventory",
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp),
        )
        Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text("Catalog — build M7", style = LocalHoroType.current.MonoCaption, color = HoroColors.ink60)
            Spacer(Modifier.height(12.dp))
        }
    }
}
