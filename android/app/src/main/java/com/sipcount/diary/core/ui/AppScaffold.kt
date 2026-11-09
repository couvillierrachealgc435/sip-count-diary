package com.sipcount.diary.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sipcount.diary.R
import com.sipcount.diary.core.theme.BackgroundDeep
import com.sipcount.diary.core.theme.BackgroundMid
import com.sipcount.diary.core.theme.BackgroundTop
import com.sipcount.diary.core.theme.LocalAppPalette

val SecondaryOverlay: List<Color> = listOf(
    BackgroundTop.copy(alpha = 0.93f),
    BackgroundMid.copy(alpha = 0.95f),
    BackgroundDeep.copy(alpha = 0.96f)
)

@Composable
fun AppScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    overlay: List<Color> = SecondaryOverlay,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = LocalAppPalette.current
    Box(modifier = modifier.fillMaxSize()) {
        AppBackdrop(overlay = overlay)
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                color = palette.surfaceTranslucent,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .safeDrawingPadding()
                        .height(72.dp)
                        .padding(horizontal = 12.dp)
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Image(
                            painter = painterResource(R.drawable.icon_home),
                            contentDescription = stringResource(R.string.menu),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (trailing != null) trailing()
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
            ) {
                content()
            }
        }
    }
}
