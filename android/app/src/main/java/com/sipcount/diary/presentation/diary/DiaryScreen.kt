package com.sipcount.diary.presentation.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sipcount.diary.R
import com.sipcount.diary.core.di.AppViewModelFactory
import com.sipcount.diary.core.theme.BackgroundDeep
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.core.ui.AppScaffold
import com.sipcount.diary.core.ui.DiaryRow
import com.sipcount.diary.core.ui.PrimaryButton
import com.sipcount.diary.presentation.dialog.ConfirmDialog

@Composable
fun DiaryScreen(
    onBack: () -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: DiaryViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalAppPalette.current

    AppScaffold(
        title = stringResource(R.string.day_diary),
        onBack = onBack,
        modifier = modifier
    ) {
        if (state.rows.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = palette.surface,
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.no_drinks_today),
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp, horizontal = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                PrimaryButton(
                    text = stringResource(R.string.cta_start),
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .offset(x = 31.dp)
                        .padding(vertical = 18.dp)
                        .background(BackgroundDeep)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.rows, key = { it.id }) { row ->
                        DiaryRow(
                            spriteIndex = row.spriteIndex,
                            title = row.title,
                            subtitle = row.subtitle,
                            time = row.time,
                            volume = row.volumeLabel,
                            accent = Color(row.colorArgb),
                            onDelete = { viewModel.askDelete(row.id) }
                        )
                    }
                }
            }
        }
    }

    if (state.pendingDeleteId != null) {
        ConfirmDialog(
            title = stringResource(R.string.delete_entry),
            message = stringResource(R.string.delete_entry_message),
            confirmText = stringResource(R.string.delete),
            destructive = true,
            onConfirm = { viewModel.confirmDelete() },
            onDismiss = { viewModel.dismissDelete() }
        )
    }
}
