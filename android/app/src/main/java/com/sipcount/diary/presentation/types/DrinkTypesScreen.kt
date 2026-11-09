package com.sipcount.diary.presentation.types

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sipcount.diary.R
import com.sipcount.diary.core.di.AppViewModelFactory
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.core.ui.AppScaffold
import com.sipcount.diary.core.ui.PrimaryButton
import com.sipcount.diary.presentation.dialog.DrinkTypeEditorDialog

@Composable
fun DrinkTypesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: DrinkTypesViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalAppPalette.current

    AppScaffold(
        title = stringResource(R.string.types),
        onBack = onBack,
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(state.rows, key = { it.id }) { row ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = palette.surface,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(row.colorArgb).copy(alpha = 0.26f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(row.colorArgb), CircleShape)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        ) {
                            Text(
                                text = row.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = palette.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stringResource(R.string.today_count, row.todayCount),
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textMuted,
                                maxLines = 1
                            )
                        }
                        IconButton(
                            onClick = { viewModel.openEdit(row.id) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = stringResource(R.string.edit),
                                tint = Color(row.colorArgb),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryButton(
            text = stringResource(R.string.add_type),
            onClick = { viewModel.openNew() },
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp)
        )
    }

    val editor = state.editor
    if (editor != null) {
        DrinkTypeEditorDialog(
            editor = editor,
            onName = { viewModel.changeName(it) },
            onColor = { viewModel.changeColor(it) },
            onSave = { viewModel.save() },
            onDelete = { viewModel.delete() },
            onDismiss = { viewModel.dismissEditor() }
        )
    }
}
