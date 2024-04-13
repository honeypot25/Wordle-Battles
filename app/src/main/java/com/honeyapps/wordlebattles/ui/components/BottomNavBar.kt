package com.honeyapps.wordlebattles.ui.components

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.data.models.BottomNavBarItemModel
import com.honeyapps.wordlebattles.ui.viewmodels.FriendsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    friendsViewModel: FriendsViewModel = koinViewModel()
) {
    val friendRequests by friendsViewModel.friendRequests.collectAsStateWithLifecycle()

    // set labels
    val items = listOf(
        BottomNavBarItemModel.Menu(
            label = stringResource(id = R.string.menu),
            badgeCount = friendRequests.size
        ),
        BottomNavBarItemModel.Home(label = stringResource(id = R.string.home)),
        BottomNavBarItemModel.Profile(label = stringResource(id = R.string.profile)),
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
    ) {
        items.fastForEach { item ->
            NavigationBarItem(
                // set as selected if it matches the route of the current destination in the graph
//                selected = item.label == currentRoute,
                selected = false, // since I want to default to the tint color
                onClick = {
                    navController.navigate(item.route) {
                        // clean up to the startDestinationRoute of the graph
                        popUpTo(navController.graph.startDestinationRoute!!) {
//                            saveState = true // save state of screen, to be restored
                        }
                        launchSingleTop = true // no duplicates
//                        restoreState = true // restore state of previous screens
                    }
                },
                // Icon supporting a notification badge
                icon = {
                    BadgedBox(
                        badge = {
                            // for Menu item
                            if (item.badgeCount != null && item.badgeCount!! > 0) {
                                Badge {
                                    Text(text = item.badgeCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (item.route == currentRoute) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = null,
                            tint = if (item.route == currentRoute) {
                                MaterialTheme.colorScheme.primary
                            } else MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            )
        }
    }
}
