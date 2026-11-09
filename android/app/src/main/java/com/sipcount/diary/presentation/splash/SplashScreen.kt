package com.sipcount.diary.presentation.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sipcount.diary.R
import com.sipcount.diary.core.di.AppViewModelFactory
import com.sipcount.diary.core.theme.PrimaryLight
import com.sipcount.diary.core.theme.SplashDeep
import com.sipcount.diary.core.theme.SplashIndicator
import com.sipcount.diary.core.theme.SplashMid
import com.sipcount.diary.core.theme.SplashSubtitle
import com.sipcount.diary.core.theme.SplashTitle
import com.sipcount.diary.core.theme.SplashTop
import com.sipcount.diary.core.ui.AppBackdrop
import kotlin.math.PI
import kotlin.math.sin

private val SplashOverlay = listOf(
    SplashTop.copy(alpha = 0.90f),
    SplashMid.copy(alpha = 0.90f),
    SplashDeep.copy(alpha = 0.94f)
)

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SplashViewModel = viewModel(factory = AppViewModelFactory.factory)
    val finished by viewModel.finished.collectAsStateWithLifecycle()
    var revealed by remember { mutableStateOf(false) }
    var handled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        revealed = true
    }
    LaunchedEffect(finished) {
        if (finished && !handled) {
            handled = true
            onFinished()
        }
    }

    val logoScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.86f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 120f),
        label = "splashLogoScale"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 250),
        label = "splashTitleAlpha"
    )

    val transition = rememberInfiniteTransition(label = "splashLoop")
    val bubbles = (0 until 9).map { index ->
        transition.animateFloat(
            initialValue = 1.05f,
            targetValue = -0.10f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3200 + index * 300,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(index * 420)
            ),
            label = "splashBubble"
        )
    }
    val bandPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "splashBand"
    )
    val float by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splashFloat"
    )

    Box(modifier = modifier.fillMaxSize()) {
        AppBackdrop(overlay = SplashOverlay, blobAlpha = 0.5f)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val band = Path()
            var x = 0f
            val baseY = size.height * 0.62f
            band.moveTo(0f, baseY)
            while (x <= size.width) {
                band.lineTo(
                    x,
                    baseY + 18f * density * sin(bandPhase + x / size.width * 2.2f * PI.toFloat())
                )
                x += size.width / 40f
            }
            drawPath(
                path = band,
                color = PrimaryLight.copy(alpha = 0.35f),
                style = Stroke(width = 2f * density)
            )
            bubbles.forEachIndexed { index, state ->
                val radius = (4f + index * 1.1f) * density
                drawCircle(
                    color = Color.White.copy(alpha = 0.10f + index * 0.014f),
                    radius = radius,
                    center = Offset(
                        x = size.width * (0.08f + index * 0.105f),
                        y = size.height * state.value
                    )
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 32.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(148.dp)
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                        translationY = float * density
                    }
            )
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = stringResource(R.string.splash_title),
                style = MaterialTheme.typography.displaySmall,
                color = SplashTitle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(titleAlpha)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelLarge,
                color = SplashSubtitle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(titleAlpha)
            )
            Spacer(modifier = Modifier.height(28.dp))
            LinearProgressIndicator(
                color = SplashIndicator,
                trackColor = Color.White.copy(alpha = 0.20f),
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .width(180.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = SplashSubtitle.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
