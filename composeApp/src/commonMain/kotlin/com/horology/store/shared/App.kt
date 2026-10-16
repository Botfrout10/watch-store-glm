package com.horology.store.shared

import androidx.compose.runtime.Composable
import com.horology.store.shared.theme.HorologyTheme
import com.horology.store.shared.ui.ledger.DesignLedgerScreen

@Composable
fun App() {
    HorologyTheme {
        DesignLedgerScreen()
    }
}
