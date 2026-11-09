package com.sipcount.diary.presentation.menu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
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
import com.sipcount.diary.core.navigation.Screen
import com.sipcount.diary.core.theme.Accent
import com.sipcount.diary.core.theme.BackgroundDeep
import com.sipcount.diary.core.theme.BackgroundMid
import com.sipcount.diary.core.theme.BackgroundTop
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.core.theme.Primary
import com.sipcount.diary.core.theme.SurfaceAlt
import com.sipcount.diary.core.theme.Teal
import com.sipcount.diary.core.ui.AppBackdrop
import com.sipcount.diary.core.ui.GlassMeter
import com.sipcount.diary.core.ui.PrimaryButton
import com.sipcount.diary.core.ui.StatCard
import com.sipcount.diary.core.ui.unitLabel

private val MenuOverlay = listOf(
    BackgroundTop.copy(alpha = 0.90f),
    BackgroundMid.copy(alpha = 0.95f),
    BackgroundDeep.copy(alpha = 0.98f)
)

@Composable
fun MenuScreen(
    onStart: () -> Unit,
    onOpen: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: MenuViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    MenuContent(state = state, onStart = onStart, onOpen = onOpen, modifier = modifier)
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onStart: () -> Unit,
    onOpen: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        revealed = true
    }
    Box(modifier = modifier.fillMaxSize()) {
        AppBackdrop(overlay = MenuOverlay)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.splash_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = palette.surface,
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = stringResource(R.string.today),
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                IconButton(
                    onClick = { onOpen(Screen.Settings) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.settings),
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            HeroTile(
                state = state,
                revealed = revealed,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.30f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (state.hasActivity) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.72f)
                        .alpha(enterAlpha(revealed, 1))
                ) {
                    StatCard(
                        label = stringResource(R.string.drinks_today),
                        value = state.drinkCount.toString(),
                        accent = Primary,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    StatCard(
                        label = stringResource(R.string.last_sip),
                        value = state.lastSipTime,
                        accent = Accent,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = palette.surface,
                    shadowElevation = 3.dp,
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.12f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.72f)
                        .alpha(enterAlpha(revealed, 1))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.nothing_logged),
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.textMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.66f)
                    .alpha(enterAlpha(revealed, 2))
            ) {
                NavTile(
                    label = stringResource(R.string.diary),
                    iconRes = R.drawable.ic_list,
                    accent = Primary,
                    onClick = { onOpen(Screen.Diary) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                NavTile(
                    label = stringResource(R.string.week),
                    iconRes = R.drawable.ic_calendar,
                    accent = Teal,
                    onClick = { onOpen(Screen.WeekChart) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                NavTile(
                    label = stringResource(R.string.rack),
                    iconRes = R.drawable.ic_star,
                    accent = Accent,
                    onClick = { onOpen(Screen.VesselRack) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.48f)
                    .alpha(enterAlpha(revealed, 3))
            ) {
                SlimTile(
                    label = stringResource(R.string.types),
                    onClick = { onOpen(Screen.DrinkTypes) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                SlimTile(
                    label = stringResource(R.string.past_days),
                    onClick = { onOpen(Screen.PastDays) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.tap_a_vessel),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.cta_start),
                onClick = onStart,
                iconPainter = painterResource(R.drawable.ic_play),
                minHeight = 64,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(enterAlpha(revealed, 4))
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun enterAlpha(revealed: Boolean, index: Int): Float {
    val alpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 320, delayMillis = index * 55),
        label = "tileEnter"
    )
    return alpha
}

@Composable
private fun HeroTile(
    state: MenuUiState,
    revealed: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = palette.surface,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.10f)),
        modifier = modifier.alpha(enterAlpha(revealed, 0))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(listOf(SurfaceAlt, palette.surface))
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 20.dp)
            ) {
                GlassMeter(
                    fraction = state.fraction,
                    sloshToken = 0,
                    label = stringResource(R.string.glass),
                    stateText = stringResource(
                        R.string.glass_state,
                        state.totalLabel,
                        state.targetLabel
                    ),
                    compact = true,
                    targetReached = state.targetReached,
                    modifier = Modifier
                        .width(92.dp)
                        .fillMaxHeight()
                )
                Spacer(modifier = Modifier.width(18.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = state.totalLabel,
                        style = MaterialTheme.typography.displayMedium,
                        color = Primary,
                        maxLines = 1,
                        modifier = Modifier.semantics {
                            liveRegion = LiveRegionMode.Polite
                        }
                    )
                    Text(
                        text = unitLabel(state.unit),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.target_line, state.targetLabel),
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(6.dp)
                    .padding(horizontal = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(BackgroundDeep.copy(alpha = 0.6f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(state.fraction.coerceIn(0.001f, 1f))
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (state.targetReached) {
                                Brush.horizontalGradient(listOf(Accent, Accent))
                            } else {
                                Brush.horizontalGradient(listOf(Color(0xFF5FC9F0), Primary))
                            }
                        )
                )
            }
        }
    }
}

@Composable
private fun NavTile(
    label: String,
    iconRes: Int,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 900f),
        label = "navTileScale"
    )
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = palette.surface,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
        interactionSource = interactionSource,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SlimTile(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 900f),
        label = "slimTileScale"
    )
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = palette.surfaceAlt,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.14f)),
        interactionSource = interactionSource,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
