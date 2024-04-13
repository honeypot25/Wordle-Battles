package com.honeyapps.wordlebattles.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.honeyapps.wordlebattles.ui.components.EmptyContent
import com.honeyapps.wordlebattles.ui.components.MyPullRefreshIndicator
import com.honeyapps.wordlebattles.ui.components.UserItem
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.FriendsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FriendRequestsTab(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    friendsViewModel: FriendsViewModel = koinViewModel(),
) {
    // In the future, friend requests could modified by a FCM method when sending a friend request
    val friendRequests by friendsViewModel.friendRequests.collectAsStateWithLifecycle()
    val isRefreshing by friendsViewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        isRefreshing,
        onRefresh = { friendsViewModel.refreshScreen() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.TopCenter
//        horizontalAlignment = Alignment.CenterHorizontally, // Column
    ) {
        if (friendRequests.isEmpty()) {
            EmptyContent(
                verticalArrangement = Arrangement.Center,
                textColor = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(top = dimens.paddingM),
            ) {
                items(
                    items = friendRequests,
                    key = { it.uid } // to make only the interested UserModel recompose, if friendsRequests changes
                ) {
                    UserItem(
                        user = it,
                        navController = navController,
                        isFriendRequestItem = true,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = dimens.paddingS),
                        thickness = dimens.dividerS,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        MyPullRefreshIndicator(
            isRefreshing = isRefreshing,
            pullRefreshState = pullRefreshState,
        )
    }
}