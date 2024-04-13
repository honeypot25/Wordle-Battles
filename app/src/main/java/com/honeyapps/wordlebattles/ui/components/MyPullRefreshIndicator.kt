package com.honeyapps.wordlebattles.ui.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyPullRefreshIndicator(
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState,
    modifier: Modifier = Modifier
) {
    PullRefreshIndicator(
        refreshing = isRefreshing,
        state = pullRefreshState,
        backgroundColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.primary,
//        scale = true,
        modifier = modifier
    )
}