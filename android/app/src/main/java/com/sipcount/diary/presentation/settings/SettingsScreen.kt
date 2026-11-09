package com.sipcount.diary.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sipcount.diary.R
import com.sipcount.diary.core.di.AppViewModelFactory
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.core.theme.Primary
import com.sipcount.diary.core.ui.AppScaffold
import com.sipcount.diary.core.ui.SectionCard
import com.sipcount.diary.core.ui.SecondaryButton
import com.sipcount.diary.core.ui.unitLabel
import com.sipcount.diary.domain.model.VolumeUnit
import com.sipcount.diary.presentation.dialog.StepperButton

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalAppPalette.current
    val exportLabel = stringResource(R.string.export_label)
    val exportHeader = stringResource(R.string.export_header)
    val unitSuffix = unitLabel(state.unit)

    AppScaffold(
        title = stringResource(R.string.settings),
        onBack = onBack,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        SectionCard(
            title = stringResource(R.string.personal_target),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StepperButton(
                    label = stringResource(R.string.minus),
                    description = stringResource(R.string.decrease),
                    onClick = { viewModel.shiftTarget(-1) }
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.targetLabel,
                        style = MaterialTheme.typography.displayMedium,
                        color = Primary,
                        maxLines = 1
                    )
                    Text(
                        text = unitSuffix,
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                }
                StepperButton(
                    label = stringResource(R.string.plus),
                    description = stringResource(R.string.increase),
                    onClick = { viewModel.shiftTarget(1) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                state.quickTargets.forEachIndexed { index, value ->
                    Surface(
                        onClick = { viewModel.setTarget(value) },
                        shape = RoundedCornerShape(20.dp),
                        color = if (state.targetMl == value) {
                            Primary.copy(alpha = 0.18f)
                        } else {
                            palette.surfaceAlt
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = state.quickLabels.getOrElse(index) { "" },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (state.targetMl == value) Primary else palette.textSecondary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.soft_target),
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        SectionCard(
            title = stringResource(R.string.units),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentOption(
                    label = stringResource(R.string.unit_ml),
                    selected = state.unit == VolumeUnit.ML,
                    onClick = { viewModel.setUnit(VolumeUnit.ML) },
                    modifier = Modifier.weight(1f)
                )
                SegmentOption(
                    label = stringResource(R.string.unit_oz),
                    selected = state.unit == VolumeUnit.OZ,
                    onClick = { viewModel.setUnit(VolumeUnit.OZ) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SectionCard(
            title = stringResource(R.string.theme),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentOption(
                    label = stringResource(R.string.theme_light),
                    selected = !state.darkTheme,
                    onClick = { viewModel.setDarkTheme(false) },
                    modifier = Modifier.weight(1f)
                )
                SegmentOption(
                    label = stringResource(R.string.theme_dark),
                    selected = state.darkTheme,
                    onClick = { viewModel.setDarkTheme(true) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SectionCard(
            title = stringResource(R.string.local_export),
            modifier = Modifier.fillMaxWidth()
        ) {
            SecondaryButton(
                text = stringResource(R.string.copy_diary_text),
                onClick = { viewModel.exportDiary(exportLabel, exportHeader, unitSuffix) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.export_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.about_line),
            style = MaterialTheme.typography.labelSmall,
            color = palette.textMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 14.dp)
        )
    }

    if (state.exportDone) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissExport() },
            shape = RoundedCornerShape(24.dp),
            containerColor = palette.surface,
            title = {
                Text(
                    text = stringResource(R.string.copy_diary_text),
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.export_done),
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissExport() }) {
                    Text(
                        text = stringResource(R.string.ok),
                        style = MaterialTheme.typography.titleSmall,
                        color = Primary
                    )
                }
            }
        )
    }
}

@Composable
private fun SegmentOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    val selectedText = stringResource(R.string.selected)
    val notSelectedText = stringResource(R.string.not_selected)
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = if (selected) Primary.copy(alpha = 0.18f) else palette.surfaceAlt,
        modifier = modifier
            .height(56.dp)
            .semantics {
                stateDescription = if (selected) selectedText else notSelectedText
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = if (selected) Primary else palette.textSecondary,
                maxLines = 1
            )
        }
    }
}
