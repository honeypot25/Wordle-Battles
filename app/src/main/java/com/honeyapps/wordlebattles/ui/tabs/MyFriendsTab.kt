package com.honeyapps.wordlebattles.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.honeyapps.wordlebattles.ui.components.EmptyContent
import com.honeyapps.wordlebattles.ui.components.UserItem
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.FriendsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyFriendsList(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    friendsViewModel: FriendsViewModel = koinViewModel(),
) {
    val friendsList by friendsViewModel.friendsList.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (friendsList.isEmpty()) {
            EmptyContent(
                verticalArrangement = Arrangement.Center,
                textColor = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                modifier = Modifier
                    .fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(top = dimens.paddingM),
            ) {
                items(
                    items = friendsList,
                    key = { it.uid } // to make only the interested UserModel recompose, if friendsList changes
                ) {
                    UserItem(
                        user = it,
                        navController = navController,
                        isMyFriendsItem = true,
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
    }
}