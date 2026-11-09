package com.sipcount.diary.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sipcount.diary.R
import com.sipcount.diary.domain.model.VolumeUnit

@Composable
fun unitLabel(unit: VolumeUnit): String = stringResource(
    if (unit == VolumeUnit.OZ) R.string.unit_oz else R.string.unit_ml
)
