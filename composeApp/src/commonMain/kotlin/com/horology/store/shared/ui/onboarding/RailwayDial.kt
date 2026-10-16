package com.horology.store.shared.ui.onboarding

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.horology.store.shared.data.nowMs
import com.horology.store.shared.theme.HoroColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin

private const val TwoPi = (2.0 * PI).toFloat()
private const val SixtySecMs = 60_000L
private const val MinuteMs = 3_600_000L
private const val HourHalfDayMs = 4_320_000L

/** Fraction of each second spent snapping to the next tick; the rest is rest (railway discipline). */
private const val BeatFraction = 0.15f

/**
 * Swiss railway dial, drawn by hand. Second hand beats once per second with a
 * short eased snap (red lollipop disc), minute/hour hands glide continuously —
 * all derived from the real platform clock, so the dial always tells true time.
 */
@Composable
fun RailwayDial(modifier: Modifier = Modifier) {
    val stamp = remember { mutableLongStateOf(nowMs()) }
    LaunchedEffect(Unit) {
        // Frame time is monotonic uptime, not epoch — align once against the wall clock.
        var offsetMs = -1L
        while (true) {
            val frameMs = withFrameMillis { it }
            if (offsetMs < 0) offsetMs = nowMs() - frameMs
            stamp.longValue = frameMs + offsetMs
        }
    }

    Canvas(modifier = modifier) {
        drawFace(RailwayFace(stamp.longValue))
    }
}

private data class RailwayFace(val hourAngle: Float, val minuteAngle: Float, val secondAngle: Float)

private fun RailwayFace(totalMs: Long): RailwayFace {
    // Second hand: discrete railway beat — quick eased snap to the next minute mark,
    // then a rest for the remainder of the second (~150ms sub-tick at high rate).
    val secMs = (totalMs % SixtySecMs).toFloat()
    val tickIndex = floor(secMs / 1000f)
    val secFrac = secMs / 1000f - tickIndex
    val beat = if (secFrac >= BeatFraction) 1f else EaseOutBack.transform(secFrac / BeatFraction)
    return RailwayFace(
        hourAngle = (totalMs % HourHalfDayMs).toFloat() / HourHalfDayMs * TwoPi,
        minuteAngle = (totalMs % MinuteMs).toFloat() / MinuteMs * TwoPi,
        secondAngle = (tickIndex + beat) / 60f * TwoPi,
    )
}

private fun DrawScope.drawFace(face: RailwayFace) {
    val d = min(size.width, size.height)
    val c = d / 2f
    val center = Offset(c, c)

    // Face: white card on paper, single hairline ring.
    drawCircle(color = HoroColors.card, radius = c)
    val hairW = 1.dp.toPx()
    drawCircle(
        color = HoroColors.hairline,
        radius = c - hairW / 2f,
        style = Stroke(width = hairW),
    )

    // Railway minute track: 60 bars hanging inward; majors at the hours.
    val trackOuter = c * 0.88f
    for (i in 0 until 60) {
        val major = i % 5 == 0
        val a = i / 60f * TwoPi
        val dir = point(a)
        val start = center + dir * trackOuter
        val end = start - dir * (c * if (major) 0.115f else 0.068f)
        drawLine(
            color = HoroColors.ink,
            start = start,
            end = end,
            strokeWidth = (if (major) 4.2f else 1.6f).dp.toPx(),
        )
    }

    // Hour + minute: ink, rounded, short tails through center.
    hand(face.hourAngle, startR = c * 0.10f, endR = c * 0.50f, widthDp = 5f, center = center)
    hand(face.minuteAngle, startR = c * 0.10f, endR = c * 0.72f, widthDp = 3.2f, center = center)

    // Second hand: red, thin, tail past center; lollipop disc hangs just before the tip.
    val secDir = point(face.secondAngle)
    drawLine(
        color = HoroColors.red,
        start = center - secDir * (c * 0.16f),
        end = center + secDir * (c * 0.84f),
        strokeWidth = 2.2f.dp.toPx(),
        cap = StrokeCap.Round,
    )
    drawCircle(color = HoroColors.red, radius = c * 0.095f, center = center + secDir * (c * 0.62f))

    // Center: red hub with a pinhole.
    drawCircle(color = HoroColors.red, radius = c * 0.058f, center = center)
    drawCircle(color = HoroColors.card, radius = c * 0.018f, center = center)
}

private fun DrawScope.hand(
    angle: Float,
    startR: Float,
    endR: Float,
    widthDp: Float,
    center: Offset,
) {
    val dir = point(angle)
    drawLine(
        color = HoroColors.ink,
        start = center - dir * startR,
        end = center + dir * endR,
        strokeWidth = widthDp.dp.toPx(),
        cap = StrokeCap.Round,
    )
}

/** Unit vector for [angle] measured clockwise from 12 o'clock (canvas coords). */
private fun point(angle: Float): Offset = Offset(sin(angle), -cos(angle))
