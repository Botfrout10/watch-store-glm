package com.horology.store.shared.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.horology.store.shared.theme.HoroColors

/**
 * Boutique shell: full-bleed content above the tab bar. Screens pad their own
 * status bars; the bar owns the bottom edge. Detached routes (detail, checkout
 * confirm) render full-screen instead.
 */
@Composable
fun ShellScreen(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    content: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxSize().background(HoroColors.paper)) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            content()
        }
        HoroNavBar(selected = selectedTab, onSelect = onSelectTab)
    }
}
