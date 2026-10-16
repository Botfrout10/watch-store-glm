package com.horology.store.shared.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horology.store.shared.components.HoroButton
import com.horology.store.shared.components.HoroButtonStyle
import com.horology.store.shared.components.Masthead
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.LocalHoroType

/** M4 stub — parallax pages + Canvas Swiss dial land in M5. */
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onDone: () -> Unit,
) {
    Box(Modifier.fillMaxSize().background(HoroColors.paper).statusBarsPadding()) {
        Column(
            Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Spacer(Modifier.height(12.dp))
            Masthead("WELCOME", "HOROLOGY")
            Spacer(Modifier.height(24.dp))
            Text(
                "A quarter of Swiss horology in eleven references. Cased in paper, ink and signal red.",
                style = LocalHoroType.current.MonoCaption,
                color = HoroColors.ink60,
            )
            Spacer(Modifier.weight(1f))
            Text(
                "Onboarding — build M5",
                style = LocalHoroType.current.MonoCaption,
                color = HoroColors.ink60,
            )
            Spacer(Modifier.height(16.dp))
            HoroButton(
                text = "EXPLORE THE STORE",
                style = HoroButtonStyle.Primary,
                onClick = onDone,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(14.dp))
        }
    }
}
