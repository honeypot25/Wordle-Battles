package com.honeyapps.wordlebattles.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.data.models.TabItemModel
import com.honeyapps.wordlebattles.ui.tabs.FriendRequestsTab
import com.honeyapps.wordlebattles.ui.tabs.MyFriendsList
import com.honeyapps.wordlebattles.ui.tabs.UserSearchTab
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.FriendsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TabsRow(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    friendsViewModel: FriendsViewModel = koinViewModel()
) {
    val friendRequests by friendsViewModel.friendRequests.collectAsStateWithLifecycle()

    val tabItems = listOf(
        TabItemModel.MyFriends(label = stringResource(id = R.string.friends_tab_myfriends)),
        TabItemModel.Requests(
            label = stringResource(id = R.string.friends_tab_requests),
            badgeCount = friendRequests.size
        ),
        TabItemModel.Search(label = stringResource(id = R.string.friends_tab_search)),
    )
    var selectedTabIdx by rememberSaveable {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        tabItems.size // page count
    }

    // an update of the selectedTabIdx for TabRow updates the page content
    LaunchedEffect(key1 = selectedTabIdx) {
        pagerState.animateScrollToPage(
            page = selectedTabIdx,
        )
    }
    // a tab click/swipe or a page swipe update the selectedTabIdx for TabRow
    LaunchedEffect(key1 = pagerState.currentPage, key2 = pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIdx = pagerState.currentPage
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row with tabs
        TabRow(
            selectedTabIndex = selectedTabIdx,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            // tabs
            tabItems.forEachIndexed { idx, tabItem ->
                Tab(
                    selected = idx == selectedTabIdx,
                    onClick = { selectedTabIdx = idx },
                    text = {
                        Text(text = tabItem.label)
                    },
                    // Icon supporting a notification badge (for Requests tab)
                    icon = {
                        BadgedBox(
                            badge = {
                                if (tabItem.badgeCount != null && tabItem.badgeCount!! > 0) {
                                    Badge {
                                        Text(text = tabItem.badgeCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (idx == selectedTabIdx)
                                    tabItem.selectedIcon
                                else tabItem.unselectedIcon,
                                contentDescription = null,
                                tint = if (idx == selectedTabIdx) {
                                    MaterialTheme.colorScheme.primary
                                } else MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                )
            }
        }

        // an horizontal pager that handles the actual content of each tab page
        HorizontalPager(
            state = pagerState,
            pageSpacing = dimens.spacerXXL,
//            userScrollEnabled = false, // disable swipe gesture
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.paddingS) // to have the same effect of the padding used for each screen, as usual
                .weight(1f), // so that the pager takes the whole remaining space
        ) {
            // "it" is the page index
            when (it) {
                // My Friends
                0 -> {
                    MyFriendsList(
                        navController = navController,
                    )
                }
                // Requests
                1 -> {
                    FriendRequestsTab(
                        navController = navController,
                    )

                }
                // Search
                2 -> {
                    UserSearchTab(
                        navController = navController
                    )
                }
            }
        }
    }
}
