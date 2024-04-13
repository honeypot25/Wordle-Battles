package com.honeyapps.wordlebattles.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.data.models.MenuItemModel
import com.honeyapps.wordlebattles.sign_in.GAuthUiClient
import com.honeyapps.wordlebattles.ui.components.BottomNavBar
import com.honeyapps.wordlebattles.ui.components.SectionHeader
import com.honeyapps.wordlebattles.ui.components.TopBar
import com.honeyapps.wordlebattles.ui.dialogs.ConfirmationDialog
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.DialogsViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.FriendsViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.StatsViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MenuScreen(
    gAuthUiClient: GAuthUiClient,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(id = R.string.menu)

    Scaffold(
        topBar = {
            TopBar(title = label)
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
//                .padding(top = 64.dp)
                .padding(
                    top = paddingValues.calculateTopPadding() + dimens.spacerM,
                    bottom = paddingValues.calculateBottomPadding(),
                    start = dimens.paddingS,
                    end = dimens.paddingS
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenuItemsList(
                gAuthUiClient = gAuthUiClient,
                navController = navController,
            )
        }
    }
}

@Composable
fun MenuItemsList(
    gAuthUiClient: GAuthUiClient,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    dialogsViewModel: DialogsViewModel = koinViewModel(),
    friendsViewModel: FriendsViewModel = koinViewModel(),
    statsViewModel: StatsViewModel = koinViewModel(),
) {
    val ctx = LocalContext.current
    val ioScope = rememberCoroutineScope { Dispatchers.IO }
    val friendRequests by friendsViewModel.friendRequests.collectAsStateWithLifecycle()
    val confirmationDialogVisibility by dialogsViewModel.confirmationDialogVisibility.collectAsStateWithLifecycle()

    val gameItems = listOf(
        MenuItemModel.Settings(label = stringResource(id = R.string.settings)),
        MenuItemModel.Friends(
            label = stringResource(id = R.string.friends),
            badgeCount = friendRequests.size
        ),
        MenuItemModel.Shop(label = stringResource(id = R.string.shop))
    )
    val shareItems = listOf(
        MenuItemModel.InviteYourFriends(resId = R.string.invite_your_friends, label = stringResource(id = R.string.invite_your_friends)),
        MenuItemModel.FeedbackAndSupport(label = stringResource(id = R.string.feedback_and_support))
    )
    val infoItems = listOf(
        MenuItemModel.HowToPlay(label = stringResource(id = R.string.how_to_play)),
        MenuItemModel.About(label = stringResource(id = R.string.about)),
    )
    val signOutItem = MenuItemModel.SignOut(label = stringResource(id = R.string.sign_out))
    val deleteAccountItem = MenuItemModel.DeleteAccount(label = stringResource(id = R.string.delete_account))

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimens.spacerM), // between each item
    ) {
        // Game section
        itemsIndexed(gameItems) { idx, gameItem ->
            if (idx == 0) {
                SectionHeader(sectionName = stringResource(id = gameItem.sectionResId!!))
            }
            GameMenuItem(
                item = gameItem,
                navController = navController,
            )
            if (idx == gameItems.size - 1) {
                Spacer(modifier = Modifier.height(dimens.spacerM))
            }
        }
        // Share section
        itemsIndexed(shareItems) { idx, shareItem ->
            if (idx == 0) {
                SectionHeader(sectionName = stringResource(id = shareItem.sectionResId!!))
            }
            ShareMenuItem(
                item = shareItem,
                navController = shareItem.route?.let { navController },
                ctx = ctx
            )
            if (idx == shareItems.size - 1) {
                Spacer(modifier = Modifier.height(dimens.spacerM))
            }
        }
        // Info section
        itemsIndexed(infoItems) { idx, infoItem ->
            if (idx == 0) {
                SectionHeader(sectionName = stringResource(id = infoItem.sectionResId!!))
            }
            InfoMenuItem(
                item = infoItem,
                navController = navController
            )
            if (idx == infoItems.size - 1) {
                Spacer(modifier = Modifier.height(dimens.spacerM))
            }
        }
        // Misc items
        item {
            SectionHeader()
            // Sign out
            MenuItem(
                item = signOutItem,
                onItemClick = {
                    ioScope.launch {
                        gAuthUiClient.signOut(
                            onSuccess = {
                                Toast.makeText(
                                    ctx,
                                    R.string.sign_out_success,
                                    Toast.LENGTH_SHORT
                                ).show()
                                // clear all Koin modules
//                                KoinModules.clearModules(getKoin())
                            },
                        )
                    }
                    // clear the whole back stack (including start destination)
                    navController.navigate(route = navController.graph.startDestinationRoute!!) {
                        popUpTo(navController.graph.startDestinationRoute!!) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        item {
            // Delete account
            MenuItem(
                item = deleteAccountItem,
                onItemClick = {
                    dialogsViewModel.setConfirmationDialogVisibility(true)
                }
            )
        }
    }

    if (confirmationDialogVisibility) {
        ConfirmationDialog(
            icon = Icons.Filled.DeleteSweep,
            title = stringResource(id = R.string.delete_account),
            text = stringResource(id = R.string.delete_account_notice),
            onDismiss = {
                dialogsViewModel.setConfirmationDialogVisibility(false)
            },
            onConfirm = {
                dialogsViewModel.setConfirmationDialogVisibility(false)
                ioScope.launch {
                    gAuthUiClient.deleteAccount(
                        onSuccess = { uid ->
                            Toast.makeText(
                                ctx,
                                R.string.account_deleted_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            // clear the whole back stack (including start destination)
                            navController.navigate(route = navController.graph.startDestinationRoute!!) {
                                popUpTo(navController.graph.startDestinationRoute!!) {
                                    inclusive = true
                                }
                            }
                            // delete user stats
                            statsViewModel.deleteStats(uid = uid)
                        },
                        onFail = {
                            Toast.makeText(
                                ctx,
                                R.string.account_deleted_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun GameMenuItem(
    item: MenuItemModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    MenuItem(
        item = item,
        onItemClick = {
            navController.navigate(item.route!!)
        },
    )
}

@Composable
fun ShareMenuItem(
    item: MenuItemModel,
    navController: NavHostController?,
    ctx: Context,
    userViewModel: UserViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val userState by userViewModel.userState.collectAsStateWithLifecycle()

    MenuItem(
        item = item,
        onItemClick = {
            if (item.resId == R.string.invite_your_friends) {
                val inviteIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        ctx.getString(R.string.invite_your_friends_intent_data, userState.username)
                    )
                    putExtra(
                        Intent.EXTRA_TEXT,
                        ctx.getString(R.string.invite_your_friends_intent_data, userState.username)
                    )
                }
                ctx.startActivity(
                    Intent.createChooser(
                        inviteIntent,
                        ctx.getString(R.string.invite_your_friends)
                    )
                ).apply {
                    // needed since starting a task outside an Activity class
                    Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else navController!!.navigate(item.route!!)
        }
    )
}

@Composable
fun InfoMenuItem(
    item: MenuItemModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    MenuItem(
        item = item,
        onItemClick = {
            navController.navigate(item.route!!)
        },
    )
}

@Composable
fun MenuItem(
    item: MenuItemModel,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.paddingS)
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // leading icon, supporting a notification badge (for Friends item)
        BadgedBox(
            badge = {
                if (item.badgeCount != null && item.badgeCount!! > 0) {
                    Badge {
                        Text(text = item.badgeCount.toString())
                    }
                }
            }
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
//                modifier = Modifier
//                    .size(dimens.iconS)
            )
        }

        Spacer(modifier = Modifier.width(dimens.spacerMS))

        // label
        Text(
            text = item.label,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        // optional trailing icon
        item.navIcon?.let {
            Icon(
                imageVector = it,
                contentDescription = null
            )
        }
    }
}

//@Composable
//fun MenuItemsList(
//    gAuthUiClient: GAuthUiClient,
//    navController: NavHostController,
//    ctx: Context,
//    scope: LifecycleCoroutineScope,
//    modifier: Modifier = Modifier,
//) {
//    val menuItems = listOf(
//        MenuItemModel.Settings(),
//        MenuItemModel.Friends(),
//        MenuItemModel.Shop(),
//        MenuItemModel.HowToPlay(),
//        MenuItemModel.InviteYourFriends(),
//        MenuItemModel.About(),
//        MenuItemModel.FeedbackAndSupport(),
//        MenuItemModel.SignOut()
//    )
//
//    LazyColumn(
//        // before first and after last
//        contentPadding = PaddingValues(4.dp),
//        // between each item
//        verticalArrangement = Arrangement.spacedBy(2.dp),
//        modifier = Modifier.padding(4.dp),
//    ) {
//        items(menuItems) { item ->
//            MenuItem(
//                route = stringResource(id = item.resId),
//                icon = item.icon,
//                navIcon = item.navIcon,
//                modifier = Modifier
//                    .padding(vertical = 8.dp)
//                    .clickable {
//                        when (item.resId) {
//                            // > Sign out
//                            R.string.sign_out -> {
//                                scope.launch {
//                                    gAuthUiClient.signOut()
//                                    Toast
//                                        .makeText(
//                                            ctx,
//                                            R.string.sign_out_success,
//                                            Toast.LENGTH_SHORT
//                                        )
//                                        .show()
//                                }
//                                // back to Sign In screen
//                                navController.popBackStack(
//                                    route = Screens.SignIn.route,
//                                    inclusive = true
//                                )
//                            }
//                            // > Invite your friends
//                            R.string.invite_your_friends -> {
//                                val inviteIntent = Intent(Intent.ACTION_SEND).apply {
//                                    type = "text/plain"
//                                    putExtra(
//                                        Intent.EXTRA_SUBJECT,
//                                        ctx.getString(R.string.invite_your_friends_intent_data)
//                                    )
//                                    putExtra(
//                                        Intent.EXTRA_TEXT,
//                                        ctx.getString(R.string.invite_your_friends_intent_data)
//                                    )
//                                }
//                                ctx.startActivity(
//                                    Intent.createChooser(
//                                        inviteIntent,
//                                        ctx.getString(R.string.invite_your_friends)
//                                    )
//                                )
//                            }
//                            // > otherwise normal navigation (route is not null)
//                            else -> navController.navigate(item.route!!)
//                        }
//                    }
//            )
//        }
//    }
//}
