package com.sipcount.diary.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sipcount.diary.R
import com.sipcount.diary.core.theme.Accent
import com.sipcount.diary.core.theme.PrimaryLight
import com.sipcount.diary.core.theme.Teal

@Composable
fun AppBackdrop(
    overlay: List<Color>,
    modifier: Modifier = Modifier,
    blobAlpha: Float = 1f
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(overlay))
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val unit = size.minDimension
            drawCircle(
                color = PrimaryLight.copy(alpha = 0.10f * blobAlpha),
                radius = unit * 0.52f,
                center = Offset(size.width * 0.12f, size.height * 0.16f)
            )
            drawCircle(
                color = Accent.copy(alpha = 0.07f * blobAlpha),
                radius = unit * 0.44f,
                center = Offset(size.width * 0.92f, size.height * 0.34f)
            )
            drawCircle(
                color = Teal.copy(alpha = 0.08f * blobAlpha),
                radius = unit * 0.60f,
                center = Offset(size.width * 0.78f, size.height * 0.94f)
            )
        }
    }
}
