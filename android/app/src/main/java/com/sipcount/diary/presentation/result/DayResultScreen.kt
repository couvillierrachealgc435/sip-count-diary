package com.sipcount.diary.presentation.result

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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
import com.sipcount.diary.core.di.AppViewModelFactory
import com.sipcount.diary.core.theme.Accent
import com.sipcount.diary.core.theme.BackgroundDeep
import com.sipcount.diary.core.theme.BackgroundTop
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.core.theme.Primary
import com.sipcount.diary.core.theme.PrimaryDeep
import com.sipcount.diary.core.ui.AppBackdrop
import com.sipcount.diary.core.ui.PrimaryButton
import com.sipcount.diary.core.ui.SecondaryButton
import com.sipcount.diary.core.ui.StatCard
import com.sipcount.diary.core.ui.unitLabel
import com.sipcount.diary.core.util.Sprites

private val ResultOverlay = listOf(
    Color(0xFFFFF3F6).copy(alpha = 0.95f),
    BackgroundTop.copy(alpha = 0.94f),
    BackgroundDeep.copy(alpha = 0.96f)
)

@Composable
fun DayResultScreen(
    epochDay: Long,
    onAgain: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: DayResultViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalAppPalette.current
    var revealed by remember { mutableStateOf(false) }

    LaunchedEffect(epochDay) {
        viewModel.select(epochDay)
        revealed = true
    }

    val badgeScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.7f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 260f),
        label = "resultBadgeScale"
    )

    Box(modifier = modifier.fillMaxSize()) {
        AppBackdrop(overlay = ResultOverlay)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(128.dp)
                    .graphicsLayer {
                        scaleX = badgeScale
                        scaleY = badgeScale
                    }
                    .background(palette.surface, CircleShape)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(72.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (state.targetReached) {
                    stringResource(R.string.target_reached)
                } else {
                    stringResource(R.string.day_logged)
                },
                style = MaterialTheme.typography.headlineMedium,
                color = if (state.targetReached) Accent else palette.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics {
                    liveRegion = LiveRegionMode.Polite
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = state.totalLabel,
                style = MaterialTheme.typography.displayLarge,
                color = Primary,
                maxLines = 1
            )
            Text(
                text = unitLabel(state.unit),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(
                    R.string.result_subtitle,
                    state.drinkCount,
                    state.typeCount
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.totalMl > 0 && state.biggestMl > 0) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(104.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.stat_total),
                        value = state.totalLabel,
                        accent = Primary,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    StatCard(
                        label = stringResource(R.string.stat_target),
                        value = state.targetLabel,
                        accent = PrimaryDeep,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    StatCard(
                        label = stringResource(R.string.stat_biggest),
                        value = state.biggestLabel,
                        accent = Accent,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.segments.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(BackgroundDeep.copy(alpha = 0.6f))
                ) {
                    state.segments.forEach { segment ->
                        Box(
                            modifier = Modifier
                                .weight(segment.share.coerceAtLeast(0.01f))
                                .fillMaxHeight()
                                .background(Color(segment.colorArgb))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.segments.forEach { segment ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = palette.surface,
                            border = BorderStroke(1.dp, Color(segment.colorArgb).copy(alpha = 0.35f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)
                            ) {
                                Image(
                                    painter = painterResource(Sprites.at(segment.spriteIndex)),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = segment.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = stringResource(R.string.percent_value, segment.percent),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(segment.colorArgb),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.saved_on_phone),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )
            PrimaryButton(
                text = stringResource(R.string.cta_again),
                onClick = onAgain,
                accent = state.targetReached,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                iconPainter = painterResource(R.drawable.icon_home),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
