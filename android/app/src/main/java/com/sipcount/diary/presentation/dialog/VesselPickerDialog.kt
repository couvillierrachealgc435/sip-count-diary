package com.sipcount.diary.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sipcount.diary.R
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.core.theme.Primary
import com.sipcount.diary.core.ui.VesselButton
import com.sipcount.diary.presentation.vessels.VesselCardUi

@Composable
fun VesselPickerDialog(
    vessels: List<VesselCardUi>,
    timeLabel: String,
    onShiftMinute: (Int) -> Unit,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val palette = LocalAppPalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = palette.surface,
        title = {
            Text(
                text = stringResource(R.string.add_entry),
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StepperButton(
                        label = stringResource(R.string.minus),
                        description = stringResource(R.string.decrease),
                        onClick = { onShiftMinute(-15) }
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timeLabel,
                            style = MaterialTheme.typography.displaySmall,
                            color = Primary
                        )
                        Text(
                            text = stringResource(R.string.entry_time),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textMuted
                        )
                    }
                    StepperButton(
                        label = stringResource(R.string.plus),
                        description = stringResource(R.string.increase),
                        onClick = { onShiftMinute(15) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.pick_vessel),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(vessels.size) { index ->
                        val vessel = vessels[index]
                        VesselButton(
                            spriteIndex = vessel.spriteIndex,
                            volumeLabel = vessel.volumeLabel,
                            accent = Color(vessel.colorArgb),
                            description = vessel.name,
                            size = 64,
                            onClick = { onPick(vessel.id) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.titleSmall,
                    color = palette.textMuted
                )
            }
        }
    )
}
