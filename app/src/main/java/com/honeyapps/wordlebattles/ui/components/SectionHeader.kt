package com.honeyapps.wordlebattles.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import com.honeyapps.wordlebattles.ui.theme.dimens

@Composable
fun SectionHeader(
    sectionName: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = dimens.paddingXS)
    ) {
        sectionName?.let {
            Text(
                text = sectionName,
                style = MaterialTheme.typography.titleLarge,
                fontStyle = FontStyle.Normal,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = dimens.paddingS),
            thickness = dimens.dividerM,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

