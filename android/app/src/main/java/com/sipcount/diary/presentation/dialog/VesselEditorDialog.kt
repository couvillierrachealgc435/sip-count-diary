package com.sipcount.diary.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sipcount.diary.R
import com.sipcount.diary.core.theme.Accent
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.core.theme.Primary
import com.sipcount.diary.core.ui.VesselButton
import com.sipcount.diary.core.util.Sprites
import com.sipcount.diary.presentation.vessels.TypeChipUi
import com.sipcount.diary.presentation.vessels.VesselEditorUi

@Composable
fun VesselEditorDialog(
    editor: VesselEditorUi,
    types: List<TypeChipUi>,
    onName: (String) -> Unit,
    onVolume: (Int) -> Unit,
    onSprite: (Int) -> Unit,
    onType: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val palette = LocalAppPalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = palette.surface,
        title = {
            Text(
                text = stringResource(R.string.vessel_editor),
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = editor.name,
                    onValueChange = onName,
                    singleLine = true,
                    label = {
                        Text(
                            text = stringResource(R.string.vessel_name),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StepperButton(
                        label = stringResource(R.string.minus),
                        description = stringResource(R.string.decrease),
                        onClick = { onVolume(-1) }
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = editor.volumeLabel,
                            style = MaterialTheme.typography.displaySmall,
                            color = Primary
                        )
                        Text(
                            text = stringResource(R.string.vessel_size),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textMuted
                        )
                    }
                    StepperButton(
                        label = stringResource(R.string.plus),
                        description = stringResource(R.string.increase),
                        onClick = { onVolume(1) }
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.vessel_art),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (index in 0 until Sprites.count) {
                        VesselButton(
                            spriteIndex = index,
                            volumeLabel = "",
                            accent = Primary,
                            description = stringResource(R.string.vessel_art),
                            selected = editor.spriteIndex == index,
                            size = 48,
                            onClick = { onSprite(index) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.types),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    types.forEach { type ->
                        Surface(
                            onClick = { onType(type.id) },
                            shape = RoundedCornerShape(20.dp),
                            color = if (editor.typeId == type.id) {
                                Color(type.colorArgb).copy(alpha = 0.26f)
                            } else {
                                palette.surfaceAlt
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = type.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(type.colorArgb),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(
                    text = stringResource(R.string.save),
                    style = MaterialTheme.typography.titleSmall,
                    color = Primary
                )
            }
        },
        dismissButton = {
            Row {
                if (editor.id != null) {
                    TextButton(onClick = onDelete) {
                        Text(
                            text = stringResource(R.string.delete),
                            style = MaterialTheme.typography.titleSmall,
                            color = Accent
                        )
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.titleSmall,
                        color = palette.textMuted
                    )
                }
            }
        }
    )
}

@Composable
internal fun StepperButton(
    label: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = palette.surfaceAlt,
        shadowElevation = 2.dp,
        modifier = modifier
            .size(48.dp)
            .semantics { contentDescription = description }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.headlineSmall,
            color = Primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
