package com.sipcount.diary.presentation.week

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sipcount.diary.R
import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.core.di.AppViewModelFactory
import com.sipcount.diary.core.theme.Accent
import com.sipcount.diary.core.theme.BackgroundDeep
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.core.theme.Primary
import com.sipcount.diary.core.theme.Teal
import com.sipcount.diary.core.ui.AppScaffold
import com.sipcount.diary.core.ui.StatCard

private val WeekdayLabels = intArrayOf(
    R.string.day_mon,
    R.string.day_tue,
    R.string.day_wed,
    R.string.day_thu,
    R.string.day_fri,
    R.string.day_sat,
    R.string.day_sun
)

@Composable
fun WeekChartScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: WeekChartViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalAppPalette.current

    AppScaffold(
        title = stringResource(R.string.week),
        onBack = onBack,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        val selected = state.bars.getOrNull(state.selectedIndex)
        Text(
            text = if (selected != null) {
                stringResource(R.string.day_readout, selected.totalLabel)
            } else {
                stringResource(R.string.tap_a_bar)
            },
            style = MaterialTheme.typography.labelLarge,
            color = if (selected != null) Primary else palette.textMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { liveRegion = LiveRegionMode.Polite }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = palette.surface,
            shadowElevation = 3.dp,
            border = BorderStroke(1.dp, Primary.copy(alpha = 0.10f)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 14.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .fillMaxHeight(state.targetShare.coerceIn(0.02f, 1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(Accent.copy(alpha = 0.5f))
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        state.bars.forEachIndexed { index, bar ->
                            WeekBar(
                                bar = bar,
                                index = index,
                                selected = index == state.selectedIndex,
                                onClick = { viewModel.select(index) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.bars.forEach { bar ->
                        Text(
                            text = stringResource(
                                WeekdayLabels[bar.weekdayIndex.coerceIn(0, 6)]
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (bar.isToday) Primary else palette.textMuted,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        if (state.weekTotalMl > 0 && state.averageMl > 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
            ) {
                StatCard(
                    label = stringResource(R.string.week_total),
                    value = state.weekTotalLabel,
                    accent = Primary,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                StatCard(
                    label = stringResource(R.string.day_average),
                    value = state.averageLabel,
                    accent = Teal,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
        Spacer(
            modifier = Modifier
                .height(12.dp)
                .navigationBarsPadding()
        )
    }
}

@Composable
private fun WeekBar(
    bar: WeekBarUi,
    index: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val grow by animateFloatAsState(
        targetValue = bar.heightShare.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = GameConfig.BAR_GROW_MS,
            delayMillis = index * GameConfig.BAR_STAGGER_MS,
            easing = FastOutSlowInEasing
        ),
        label = "barGrow"
    )
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
    ) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            color = Color.Transparent,
            border = if (selected) BorderStroke(2.dp, Primary) else null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(grow.coerceAtLeast(0.02f))
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(BackgroundDeep.copy(alpha = 0.45f))
            ) {
                if (bar.segments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                } else {
                    bar.segments.forEach { segment ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(segment.share.coerceAtLeast(0.01f))
                                .background(Color(segment.colorArgb))
                        )
                    }
                }
            }
        }
    }
}
