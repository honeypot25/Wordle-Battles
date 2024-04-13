package com.honeyapps.wordlebattles.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.placeholder
import io.github.fornewid.placeholder.material3.shimmer

@Composable
fun ProfilePhoto(
    photoUrl: String?,
    photoSize: Dp,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = photoUrl,
        contentDescription = null,
        modifier = modifier
            .size(photoSize)
            .clip(CircleShape)
            .placeholder(
                visible = photoUrl == null,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                shape = MaterialTheme.shapes.extraLarge,
                highlight = PlaceholderHighlight.shimmer(),
            ),
        contentScale = ContentScale.Crop
    )
}
