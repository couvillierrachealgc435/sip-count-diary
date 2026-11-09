package com.sipcount.diary.presentation.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sipcount.diary.R
import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.core.di.AppViewModelFactory
import com.sipcount.diary.core.theme.Accent
import com.sipcount.diary.core.theme.BackgroundDeep
import com.sipcount.diary.core.theme.BackgroundTop
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.core.theme.Primary
import com.sipcount.diary.core.ui.AppBackdrop
import com.sipcount.diary.core.ui.GlassMeter
import com.sipcount.diary.core.ui.PrimaryButton
import com.sipcount.diary.core.ui.SecondaryButton
import com.sipcount.diary.core.ui.VesselButton
import com.sipcount.diary.core.ui.unitLabel
import com.sipcount.diary.domain.model.VolumeUnit
import com.sipcount.diary.presentation.dialog.ConfirmDialog
import kotlin.math.cos
import kotlin.math.sin

private val GameOverlay = listOf(
    BackgroundTop.copy(alpha = 0.95f),
    BackgroundTop.copy(alpha = 0.88f),
    BackgroundDeep.copy(alpha = 0.90f)
)

@Composable
fun TodayGlassScreen(
    onBack: () -> Unit,
    onCloseDay: (Long) -> Unit,
    onManageVessels: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: GameViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalAppPalette.current
    val haptics = LocalHapticFeedback.current
    var confirmVisible by remember { mutableStateOf(false) }

    val ripple = remember { Animatable(0f) }
    val droplet = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is GameEvent.TargetReached) {
                ripple.snapTo(0f)
                ripple.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = GameConfig.TARGET_RIPPLE_MS.toInt(),
                        easing = LinearEasing
                    )
                )
            }
        }
    }
    LaunchedEffect(state.sloshToken) {
        if (state.sloshToken > 0) {
            droplet.snapTo(0f)
            droplet.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = GameConfig.DROPLET_MS.toInt(),
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.pause()
        }
    }

    val animatedTotal by animateIntAsState(
        targetValue = state.totalMl,
        animationSpec = tween(durationMillis = 450),
        label = "totalCountUp"
    )

    Box(modifier = modifier.fillMaxSize()) {
        AppBackdrop(overlay = GameOverlay)
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                color = palette.surfaceTranslucent,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .safeDrawingPadding()
                        .height(72.dp)
                        .padding(horizontal = 12.dp)
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Image(
                            painter = painterResource(R.drawable.icon_home),
                            contentDescription = stringResource(R.string.menu),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.today),
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.textPrimary
                        )
                        Text(
                            text = stringResource(
                                R.string.total_line,
                                state.totalLabel,
                                unitLabel(state.unit)
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = { confirmVisible = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icon_pause),
                            contentDescription = stringResource(R.string.close_day),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 132.dp, start = 20.dp, end = 20.dp)
                ) {
                    Text(
                        text = if (state.unit == VolumeUnit.ML) {
                            animatedTotal.toString()
                        } else {
                            state.totalLabel
                        },
                        style = MaterialTheme.typography.displayLarge,
                        color = if (state.targetReached) Accent else Primary,
                        modifier = Modifier.semantics {
                            liveRegion = LiveRegionMode.Polite
                        }
                    )
                    Text(
                        text = unitLabel(state.unit),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    GlassMeter(
                        fraction = state.fraction,
                        sloshToken = state.sloshToken,
                        label = stringResource(R.string.glass),
                        stateText = stringResource(
                            R.string.glass_state,
                            state.totalLabel,
                            state.targetLabel
                        ),
                        targetReached = state.targetReached,
                        outlined = !state.hasVessels,
                        modifier = Modifier
                            .width(210.dp)
                            .height(280.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (state.targetReached) {
                            stringResource(R.string.target_reached)
                        } else {
                            stringResource(R.string.remaining_line, state.remainingLabel)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (state.targetReached) Accent else palette.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
                if (droplet.value > 0f && droplet.value < 1f) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val progress = droplet.value
                        for (index in 0 until 6) {
                            val angle = index * 60f * 0.0174533f
                            val distance = 34f * density * progress
                            drawCircle(
                                color = Primary.copy(alpha = 1f - progress),
                                radius = 4f * density,
                                center = Offset(
                                    x = size.width / 2f + cos(angle) * distance,
                                    y = size.height / 2f + sin(angle) * distance
                                )
                            )
                        }
                    }
                }
                if (ripple.value > 0f && ripple.value < 1f) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Accent.copy(alpha = 0.35f * (1f - ripple.value)),
                            radius = size.maxDimension * ripple.value,
                            center = Offset(size.width / 2f, size.height / 2f)
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SecondaryButton(
                        text = stringResource(R.string.undo_last),
                        onClick = { viewModel.undoLast() },
                        enabled = state.canUndo,
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = stringResource(R.string.close_day),
                        onClick = { confirmVisible = true },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = palette.surfaceTranslucent,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.hasVessels) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.vessels, key = { it.id }) { vessel ->
                                VesselButton(
                                    spriteIndex = vessel.spriteIndex,
                                    volumeLabel = vessel.volumeLabel,
                                    accent = Color(vessel.colorArgb),
                                    description = vessel.name,
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.onVesselTapped(vessel.id)
                                    }
                                )
                            }
                            item {
                                SecondaryButton(
                                    text = stringResource(R.string.add_vessel),
                                    onClick = onManageVessels
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            PrimaryButton(
                                text = stringResource(R.string.create_first_vessel),
                                onClick = onManageVessels,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    if (confirmVisible) {
        ConfirmDialog(
            title = stringResource(R.string.close_day),
            message = stringResource(R.string.close_day_message),
            confirmText = stringResource(R.string.close_day),
            onConfirm = {
                confirmVisible = false
                onCloseDay(state.epochDay)
            },
            onDismiss = { confirmVisible = false }
        )
    }
}
