package com.horology.store.shared.ui.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horology.store.shared.components.HoroButton
import com.horology.store.shared.components.HoroButtonStyle
import com.horology.store.shared.components.Masthead
import com.horology.store.shared.motion.motionDurationScale
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.HoroFonts
import com.horology.store.shared.theme.LocalHoroType
import kotlin.math.abs
import kotlin.math.roundToInt

private data class OnboardPage(val eyebrow: String, val title: String, val body: String)

private val OnboardPages = listOf(
    OnboardPage(
        eyebrow = "WELCOME",
        title = "HOROLOGY",
        body = "A quarter of Swiss horology in eleven references. Cased in paper, ink and signal red.",
    ),
    OnboardPage(
        eyebrow = "CATALOG",
        title = "The Inventory",
        body = "Chronograph, diver, dress — every reference on the railway grid, one page per watch.",
    ),
    OnboardPage(
        eyebrow = "EXPLORE",
        title = "Yours to Wind",
        body = "Collect favorites, hold pieces in your bag and check out — two taps to a cased watch.",
    ),
)

private val GhostNumberStyle = TextStyle(
    fontSize = 200.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = (-12).sp,
    lineHeight = 170.sp,
)

/** M5: 3 parallax pages, ghost numerals, live railway dial, staggered copy, spring indicator. */
@Composable
fun OnboardingScreen(modifier: Modifier = Modifier, onDone: () -> Unit) {
    val motion = remember { motionDurationScale() }
    val pagerState = rememberPagerState(pageCount = { OnboardPages.size })

    Column(
        modifier
            .fillMaxSize()
            .background(HoroColors.paper)
            .statusBarsPadding()
            .padding(20.dp),
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            OnboardPageContent(
                page = page,
                pagerState = pagerState,
                active = pagerState.currentPage == page,
                motion = motion,
                onDone = onDone,
            )
        }

        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.padding(start = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OnboardPages.indices.forEach { i ->
                val selected = pagerState.currentPage == i
                val dotSize by animateDpAsState(
                    targetValue = if (selected) 10.dp else 4.dp,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = 480f),
                    label = "pageDot",
                )
                Box(
                    Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(if (selected) HoroColors.red else HoroColors.hairline),
                )
                if (i != OnboardPages.lastIndex) Spacer(Modifier.size(8.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        HoroButton(
            text = "EXPLORE THE STORE",
            style = HoroButtonStyle.Primary,
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun OnboardPageContent(
    page: Int,
    pagerState: PagerState,
    active: Boolean,
    motion: Float,
    onDone: () -> Unit,
) {
    val entry = OnboardPages[page]

    Column(Modifier.fillMaxSize()) {
        Stagger(index = 0, active = active, motion = motion) {
            Masthead(
                eyebrow = entry.eyebrow,
                title = entry.title,
                trailing = {
                    HoroButton(
                        text = "SKIP",
                        style = HoroButtonStyle.Text,
                        onClick = onDone,
                    )
                },
            )
        }
        Spacer(Modifier.height(14.dp))
        Stagger(index = 1, active = active, motion = motion) {
            Text(
                entry.body,
                style = LocalHoroType.current.MonoCaption,
                color = HoroColors.ink60,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(12.dp))

        Box(Modifier.weight(1f).fillMaxWidth()) {
            // Giant ghost numeral, slowest layer — peeks above the dial like a folio number.
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp)
                    .pagerLayer(pagerState, page, factor = 0.42f),
            ) {
                Stagger(index = 2, active = active, motion = motion) {
                    Text(
                        "0${page + 1}",
                        style = GhostNumberStyle.copy(fontFamily = HoroFonts.grotesk()),
                        color = HoroColors.ink.copy(alpha = 0.07f),
                    )
                }
            }
            // Live dial, slightly faster than the numeral.
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 34.dp)
                    .pagerLayer(pagerState, page, factor = 0.16f),
            ) {
                RailwayDial(Modifier.size(280.dp))
            }
        }
    }
}

/** Staggered fade+slide-in copy block: 60ms cascade per DESIGN.md, halved under reduce-motion. */
@Composable
private fun Stagger(
    index: Int,
    active: Boolean,
    motion: Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(active) {
        if (active) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (420f * motion).roundToInt(),
                    delayMillis = (60f * index * motion).roundToInt(),
                    easing = EaseOutCubic,
                ),
            )
        }
    }
    Box(
        modifier.graphicsLayer {
            alpha = progress.value
            translationY = 26.dp.toPx() * (1f - progress.value)
        },
        content = content,
    )
}

/**
 * Parallax layer inside a pager page: settled positions render exactly at rest;
 * while scrolling the layer lags [factor] of the page width for depth.
 */
private fun Modifier.pagerLayer(
    pagerState: PagerState,
    page: Int,
    factor: Float,
): Modifier = graphicsLayer {
    val fraction = pagerState.currentPageOffsetFraction
    if (page == pagerState.currentPage || page == pagerState.targetPage) {
        translationX = fraction * factor * size.width
        alpha = (1f - abs(fraction) * 1.5f).coerceIn(0f, 1f)
    }
}
