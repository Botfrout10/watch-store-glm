package com.horology.store.shared.ui.checkout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horology.store.shared.components.Masthead
import com.horology.store.shared.data.formatUsd
import com.horology.store.shared.di.LocalAppContainer
import com.horology.store.shared.theme.HoroColors
import com.horology.store.shared.theme.HoroShapes
import com.horology.store.shared.theme.LocalHoroType
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * M9 checkout confirm — Swiss restraint: red circle draws the check (path progress,
 * spring overshoot), order number ticks in, cart is emptied on entry.
 */
@Composable
fun CheckoutConfirmScreen(
    totalUsd: Long,
    lineCount: Int,
    onDone: () -> Unit,
) {
    val container = LocalAppContainer.current
    LaunchedEffect(Unit) { container.cart.clear() }

    val circle = remember { Animatable(0f) }
    val check = remember { Animatable(0f) }
    val ticker = remember { Animatable(0f) }
    val orderNo = remember { generateOrderNo() }

    LaunchedEffect(Unit) {
        launch {
            circle.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium))
            check.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium))
            ticker.animateTo(1f, tween(520, easing = EaseOutCubic))
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Masthead(
            eyebrow = "HOROLOGY Nº 04",
            title = "The Vault",
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp),
        )
        Spacer(Modifier.weight(1f))
        CheckSeal(circleProgress = circle.value, checkProgress = check.value)
        Spacer(Modifier.height(26.dp))
        Text("THE VAULT IS SEALED", style = MaterialTheme.typography.headlineSmall, color = HoroColors.ink)
        Spacer(Modifier.height(8.dp))
        Text(
            "Your ${if (lineCount == 1) "reference is" else "$lineCount references are"} held under order",
            style = MaterialTheme.typography.bodyMedium,
            color = HoroColors.ink60,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        // Order number ticker: characters reveal left-to-right.
        val chars = orderNo.length
        val visible = (ticker.value * chars).toInt().coerceIn(0, chars)
        Text(
            orderNo.take(visible),
            style = LocalHoroType.current.MonoDisplay,
            color = HoroColors.ink,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "TOTAL ${formatUsd(totalUsd)} — CONCIERGE WILL BE IN TOUCH",
            style = LocalHoroType.current.MonoCaption,
            color = HoroColors.ink60,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
        ReturnBar(onDone = onDone)
        Spacer(Modifier.height(16.dp))
    }
}

/** Red ring draws clockwise w/ overshoot; white-gapped check draws after. */
@Composable
private fun CheckSeal(circleProgress: Float, checkProgress: Float) {
    Canvas(Modifier.size(116.dp)) {
        val stroke = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Butt)
        // Overshoot beyond 1 wraps the ring past its start (Swiss torque).
        val sweep = 360f * circleProgress.coerceAtMost(1.08f)
        drawArc(
            color = HoroColors.red,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            style = stroke,
            size = Size(size.width - stroke.width, size.height - stroke.width),
            topLeft = Offset(stroke.width / 2f, stroke.width / 2f),
        )
        // Check: two-segment path measured by progress.
        if (checkProgress > 0f) {
            val path = Path().apply {
                moveTo(size.width * 0.30f, size.height * 0.52f)
                lineTo(size.width * 0.45f, size.height * 0.66f)
                lineTo(size.width * 0.72f, size.height * 0.36f)
            }
            val measure = PathMeasure().apply { setPath(path, false) }
            val partial = Path()
            measure.getSegment(0f, measure.length * checkProgress, partial, true)
            drawPath(partial, HoroColors.red, style = stroke)
        }
    }
}

/** "HR-4F19K2" — random per order; readable mono. */
private fun generateOrderNo(): String {
    val alphabet = "ABCDEFGHJKMNPQRSTUVWXYZ23456789"
    return "HR-" + buildString {
        repeat(6) { append(alphabet[Random.nextInt(alphabet.length)]) }
    }
}

@Composable
private fun ReturnBar(onDone: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(52.dp)
            .clip(HoroShapes.card)
            .background(HoroColors.ink)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onDone,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text("RETURN TO THE BOUTIQUE", style = MaterialTheme.typography.labelSmall, color = HoroColors.paper)
    }
}
