package com.sipcount.diary.presentation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sipcount.diary.R
import com.sipcount.diary.core.theme.Accent
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.core.theme.Primary
import com.sipcount.diary.presentation.types.DrinkTypeEditorUi
import com.sipcount.diary.presentation.types.DrinkTypesUiState

@Composable
fun DrinkTypeEditorDialog(
    editor: DrinkTypeEditorUi,
    onName: (String) -> Unit,
    onColor: (Long) -> Unit,
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
                text = stringResource(R.string.type_editor),
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
                            text = stringResource(R.string.type_name),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.type_color),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DrinkTypesUiState.palette.chunked(4).forEach { chunk ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            chunk.forEach { value ->
                                Surface(
                                    onClick = { onColor(value) },
                                    shape = CircleShape,
                                    color = Color(value),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .semantics {
                                            contentDescription = editor.name
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (editor.colorArgb == value) {
                                            Box(
                                                modifier = Modifier
                                                    .size(26.dp)
                                                    .background(
                                                        Color.White.copy(alpha = 0.28f),
                                                        CircleShape
                                                    )
                                            )
                                            Icon(
                                                painter = painterResource(R.drawable.ic_check),
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
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
                TextButton(onClick = onDelete, enabled = editor.canDelete) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (editor.canDelete) Accent else palette.textMuted
                    )
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
