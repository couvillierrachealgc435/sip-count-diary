package com.sipcount.diary.presentation.history

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sipcount.diary.R
import com.sipcount.diary.core.di.AppViewModelFactory
import com.sipcount.diary.core.theme.Accent
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.core.theme.Primary
import com.sipcount.diary.core.ui.AppScaffold
import com.sipcount.diary.core.ui.DiaryRow
import com.sipcount.diary.core.ui.SecondaryButton
import com.sipcount.diary.core.util.Today
import com.sipcount.diary.presentation.dialog.ConfirmDialog
import com.sipcount.diary.presentation.dialog.VesselPickerDialog

private val MonthNames = intArrayOf(
    R.string.month_1,
    R.string.month_2,
    R.string.month_3,
    R.string.month_4,
    R.string.month_5,
    R.string.month_6,
    R.string.month_7,
    R.string.month_8,
    R.string.month_9,
    R.string.month_10,
    R.string.month_11,
    R.string.month_12
)

@Composable
fun PastDaysScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PastDaysViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = LocalAppPalette.current

    AppScaffold(
        title = stringResource(R.string.past_days),
        onBack = onBack,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            IconButton(
                onClick = { viewModel.shiftMonth(-1L) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_left),
                    contentDescription = stringResource(R.string.previous_month),
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = stringResource(
                    R.string.month_year,
                    stringResource(MonthNames[state.monthIndex.coerceIn(0, 11)]),
                    state.year
                ),
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { viewModel.shiftMonth(1L) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = stringResource(R.string.next_month),
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = palette.surface,
            shadowElevation = 3.dp,
            border = BorderStroke(1.dp, Primary.copy(alpha = 0.10f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
            ) {
                items(state.leadingBlanks) {
                    Spacer(modifier = Modifier.size(40.dp))
                }
                items(state.cells.size) { index ->
                    val cell = state.cells[index]
                    CalendarCell(
                        cell = cell,
                        selected = state.selectedDay == cell.epochDay,
                        onClick = { viewModel.selectDay(cell.epochDay) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.selectedDay != null) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = palette.surfaceAlt,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.day_total,
                            state.selectedTotalLabel
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.textSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(state.selectedRows, key = { it.id }) { row ->
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
                    Spacer(modifier = Modifier.height(10.dp))
                    SecondaryButton(
                        text = stringResource(R.string.add_entry),
                        onClick = { viewModel.openPicker() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            Text(
                text = stringResource(R.string.pick_a_day),
                style = MaterialTheme.typography.labelLarge,
                color = palette.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 24.dp)
            )
        }

        Spacer(
            modifier = Modifier
                .height(12.dp)
                .navigationBarsPadding()
        )
    }

    if (state.pickerVisible) {
        VesselPickerDialog(
            vessels = state.pickerVessels,
            timeLabel = Today.clock(state.pickerMinute),
            onShiftMinute = { viewModel.shiftPickerMinute(it) },
            onPick = { viewModel.addEntry(it) },
            onDismiss = { viewModel.dismissPicker() }
        )
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

@Composable
private fun CalendarCell(
    cell: CalendarCellUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Primary.copy(alpha = 0.18f) else Color.Transparent,
        border = if (cell.metTarget) BorderStroke(2.dp, Accent) else null,
        modifier = modifier.size(40.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = cell.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = if (cell.isToday) Primary else palette.textSecondary,
                maxLines = 1
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        Primary.copy(alpha = 0.15f + cell.fill * 0.75f),
                        CircleShape
                    )
            )
        }
    }
}
