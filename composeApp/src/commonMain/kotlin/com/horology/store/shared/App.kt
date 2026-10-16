package com.horology.store.shared

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.horology.store.shared.di.AppContainer
import com.horology.store.shared.di.LocalAppContainer
import com.horology.store.shared.motion.motionDurationScale
import com.horology.store.shared.navigation.HorologyNavHost
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.HorologyTheme
import com.horology.store.shared.ui.onboarding.OnboardingScreen

@Composable
fun App(container: AppContainer = remember { AppContainer() }) {
    HorologyTheme {
        androidx.compose.runtime.CompositionLocalProvider(LocalAppContainer provides container) {
            val scope = rememberCoroutineScope()
            val gate = container.onboarding.completed.collectAsState(null).value
            AnimatedContent(
                targetState = gate,
                transitionSpec = {
                    // Shared-axis exit (DESIGN.md): sixth-width slide + crossfade, reduce-motion halved.
                    val s = motionDurationScale()
                    (slideInHorizontally(tween((360 * s).roundToInt())) { it / 6 } +
                        fadeIn(tween((300 * s).roundToInt()))) togetherWith
                        (slideOutHorizontally(tween((280 * s).roundToInt())) { -(it / 6) } +
                            fadeOut(tween((240 * s).roundToInt())))
                },
                label = "gate",
            ) { g ->
                when (g) {
                    null -> PaperLoading()
                    true -> HorologyNavHost()
                    else -> OnboardingScreen(
                        onDone = { scope.launch { container.onboarding.complete() } },
                    )
                }
            }
        }
    }
}

/** Blank paper with a single breathing red dot — the gate's loading state. */
@Composable
private fun PaperLoading() {
    val pop = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) {
        while (true) {
            pop.animateTo(1.2f, tween(620))
            pop.animateTo(0.6f, tween(620))
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(HoroColors.paper),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .graphicsLayer {
                    scaleX = pop.value
                    scaleY = pop.value
                    alpha = 0.9f
                }
                .size(8.dp)
                .clip(CircleShape)
                .background(HoroColors.red),
        )
    }
}
