package com.honeyapps.wordlebattles.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.StatsViewModel
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.foundation.placeholder
import io.github.fornewid.placeholder.material3.shimmer
import org.koin.androidx.compose.koinViewModel

@Composable
fun StatsSection(
    uid: String,
    modifier: Modifier = Modifier,
    statsViewModel: StatsViewModel = koinViewModel()
) {
    val statsState by statsViewModel.statsState.collectAsStateWithLifecycle()

    // fetch stats only when uid is populated
    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            statsViewModel.fetchStats(
                uid = uid
            )
        }
    }

    val statsItems: List<Triple<String, Int, Color>> = listOf(
        Triple("Matches", statsState.matches, Color.LightGray),
        Triple("Wins", statsState.wins, Color.Green.copy(alpha = 0.7f)),
        Triple("Ties", statsState.ties, Color.Yellow.copy(alpha = 0.7f)),
        Triple("Losses", statsState.losses, Color.Red.copy(alpha = 0.7f)),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        statsItems.forEach { (label, value, bgColor) ->
            StatsItem(
                label = label,
                value = value,
                bgColor = bgColor,
                modifier = Modifier
                    .placeholder(
                        visible = uid.isBlank(),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                        shape = MaterialTheme.shapes.extraLarge,
                        highlight = PlaceholderHighlight.shimmer(),
                    )
            )
        }
    }
}

@Composable
fun StatsItem(
    label: String,
    value: Int,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .padding(dimens.paddingM),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircleShape(value = value, bgColor = bgColor)

        Spacer(modifier = Modifier.height(dimens.spacerXS))

        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            fontStyle = FontStyle.Italic,
        )
    }
}

@Composable
fun CircleShape(
    value: Int,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .size(dimens.circleShape)
            .background(
                color = bgColor,
                shape = MaterialTheme.shapes.medium
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}