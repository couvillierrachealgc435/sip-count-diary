package com.sipcount.diary.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.core.theme.Accent
import com.sipcount.diary.core.theme.OutlineBlue
import com.sipcount.diary.core.theme.Primary
import com.sipcount.diary.core.theme.WaterBottom
import com.sipcount.diary.core.theme.WaterTop
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun GlassMeter(
    fraction: Float,
    sloshToken: Int,
    label: String,
    stateText: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    targetReached: Boolean = false,
    outlined: Boolean = false
) {
    val transition = rememberInfiniteTransition(label = "glassIdle")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glassPhase"
    )
    val sway by transition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glassSway"
    )
    val level by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = GameConfig.FILL_ANIM_MS, easing = FastOutSlowInEasing),
        label = "glassLevel"
    )
    val amplitude = remember { Animatable(2f) }
    LaunchedEffect(sloshToken) {
        if (sloshToken > 0) {
            amplitude.snapTo(14f)
            amplitude.animateTo(
                targetValue = 2f,
                animationSpec = tween(
                    durationMillis = GameConfig.SLOSH_MS.toInt(),
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    val waveColorTop = if (targetReached) Accent else WaterTop
    val waveColorBottom = if (targetReached) Primary else WaterBottom
    Canvas(
        modifier = modifier
            .graphicsLayer { rotationZ = sway }
            .semantics {
                contentDescription = label
                stateDescription = stateText
                liveRegion = LiveRegionMode.Polite
            }
    ) {
        val width = size.width
        val height = size.height
        val strokeWidth = if (compact) 3f * density else 4f * density
        val taper = width * 0.11f
        val corner = width * 0.09f
        val glass = Path().apply {
            moveTo(taper * 0.2f, 0f)
            lineTo(width - taper * 0.2f, 0f)
            lineTo(width - taper, height - corner)
            lineTo(width - taper - corner, height)
            lineTo(taper + corner, height)
            lineTo(taper, height - corner)
            close()
        }
        clipPath(glass) {
            drawPath(path = glass, color = Color.White.copy(alpha = 0.55f))
            val liquidTop = height * (1f - level * 0.94f) - height * 0.02f
            val amp = amplitude.value * density
            if (level > 0.001f) {
                val liquid = Path().apply {
                    moveTo(0f, height)
                    lineTo(0f, liquidTop)
                    var x = 0f
                    while (x <= width) {
                        lineTo(x, liquidTop + amp * sin(phase + x / width * 2.4f * PI.toFloat()))
                        x += width / 28f
                    }
                    lineTo(width, liquidTop)
                    lineTo(width, height)
                    close()
                }
                drawPath(
                    path = liquid,
                    brush = Brush.verticalGradient(
                        colors = listOf(waveColorTop, waveColorBottom),
                        startY = liquidTop,
                        endY = height
                    )
                )
            }
        }
        for (step in 1..4) {
            val y = height * (1f - step * 0.235f)
            val isTarget = step == 4
            drawLine(
                color = if (isTarget) Accent.copy(alpha = 0.85f) else OutlineBlue.copy(alpha = 0.7f),
                start = Offset(taper + width * 0.08f, y),
                end = Offset(width - taper - width * 0.08f, y),
                strokeWidth = if (isTarget) 2f * density else 1f * density
            )
        }
        drawPath(
            path = glass,
            color = if (targetReached) Accent.copy(alpha = 0.7f) else Primary.copy(alpha = 0.55f),
            style = if (outlined) {
                Stroke(
                    width = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(10f * density, 8f * density)
                    )
                )
            } else {
                Stroke(width = strokeWidth)
            }
        )
    }
}
