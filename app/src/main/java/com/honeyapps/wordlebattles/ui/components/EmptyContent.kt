package com.honeyapps.wordlebattles.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.honeyapps.wordlebattles.R

@Composable
fun EmptyContent(
    verticalArrangement: Arrangement.Vertical,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    // made as a Lazy column (a scrolling container) to make the .pullRefresh modifier receive scroll events
    LazyColumn(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                text = stringResource(id = R.string.empty_content),
                textAlign = TextAlign.Center,
                color = textColor
            )
//            Spacer(modifier = Modifier.height(dimens.spacerXS)) if verticalArrangement = Arrangement.spacedBy(dimens.spacerXS)
            Text(
                text = stringResource(id = R.string.empty_content_ascii_art),
                textAlign = TextAlign.Center,
                color = textColor
            )
        }
    }
}