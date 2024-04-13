package com.honeyapps.wordlebattles.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.placeholder
import io.github.fornewid.placeholder.material3.shimmer

@Composable
fun ProfileFlag(
    flag: String?,
    flagDim: Int,
    modifier: Modifier = Modifier,
) {
    // North-East icon
    Canvas(
        modifier = modifier
            .offset(x = (flagDim/2).dp, y = -(flagDim*0.5).dp)
            .placeholder(
                visible = flag == null,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                shape = MaterialTheme.shapes.extraLarge,
                highlight = PlaceholderHighlight.shimmer(),
            )
    ) {
        drawIntoCanvas {
            val paint = Paint().apply {
                // either width or height
                textSize = flagDim / 2.0f
            }
            it.nativeCanvas.drawText(
                flag ?: "",
                0f,
                size.height,
                paint
            )
        }
    }
}
