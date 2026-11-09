package com.sipcount.diary.presentation.vessels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sipcount.diary.R
import com.sipcount.diary.core.di.AppViewModelFactory
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.core.theme.Primary
import com.sipcount.diary.core.ui.AppScaffold
import com.sipcount.diary.core.ui.PrimaryButton
import com.sipcount.diary.core.util.Sprites
import com.sipcount.diary.presentation.dialog.VesselEditorDialog

@Composable
fun VesselRackScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: VesselRackViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    AppScaffold(
        title = stringResource(R.string.rack),
        onBack = onBack,
        modifier = modifier
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(state.vessels, key = { it.id }) { vessel ->
                VesselCard(
                    card = vessel,
                    onEdit = { viewModel.openEdit(vessel.id) }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryButton(
            text = stringResource(R.string.add_vessel),
            onClick = { viewModel.openNew() },
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp)
        )
    }

    val editor = state.editor
    if (editor != null) {
        VesselEditorDialog(
            editor = editor,
            types = state.types,
            onName = { viewModel.changeName(it) },
            onVolume = { viewModel.changeVolume(it) },
            onSprite = { viewModel.changeSprite(it) },
            onType = { viewModel.changeType(it) },
            onSave = { viewModel.save() },
            onDelete = { viewModel.delete() },
            onDismiss = { viewModel.dismissEditor() }
        )
    }
}

@Composable
private fun VesselCard(
    card: VesselCardUi,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onEdit,
        shape = RoundedCornerShape(24.dp),
        color = palette.surface,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, Color(card.colorArgb).copy(alpha = 0.26f)),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Image(
                    painter = painterResource(Sprites.at(card.spriteIndex)),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = card.volumeLabel,
                    style = MaterialTheme.typography.displaySmall,
                    color = Primary,
                    maxLines = 1
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = stringResource(R.string.edit),
                tint = palette.textMuted,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(20.dp)
            )
        }
    }
}
