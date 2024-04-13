package com.honeyapps.wordlebattles.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.BottomSheetViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.FriendsViewModel
import com.honeyapps.wordlebattles.utils.ShakeDetector
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendsBottomSheet(
    navController: NavHostController,
    bottomSheetViewModel: BottomSheetViewModel = koinViewModel()
) {
    val ctx = LocalContext.current
    var shakeDetector: ShakeDetector? by remember { mutableStateOf(null) }

    // upon the first composition of FriendsBottomSheet
    LaunchedEffect(Unit) {
        shakeDetector = ShakeDetector(
            context = ctx,
            onShake = { bottomSheetViewModel.handleShake() }
        )
        shakeDetector?.registerListener()
    }

    DisposableEffect(Unit) {
        onDispose {
            shakeDetector?.unregisterListener()
        }
    }

    AnimatedVisibility(
        visible = bottomSheetViewModel.isSheetVisible.value,
        enter = fadeIn() + slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ),
//        exit = fadeOut() + slideOutHorizontally(),
    ) {
        BottomSheet(
            navController = navController
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    navController: NavHostController,
    bottomSheetViewModel: BottomSheetViewModel = koinViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    // required for rememberModalBottomSheetState
    val isSheetVisible = bottomSheetViewModel.isSheetVisible.value

    if (isSheetVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { bottomSheetViewModel.toggleSheetVisibility() },
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.small
        ) {
            // header
            Text(
                text = stringResource(id = R.string.friend_choose),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            // Bottom sheet content
            BottomSheetFriendsList(navController = navController)
        }
    }
}

@Composable
fun BottomSheetFriendsList(
    navController: NavHostController,
    friendsViewModel: FriendsViewModel = koinViewModel()
) {
    val friendsList by friendsViewModel.friendsList.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (friendsList.isEmpty()) {
            EmptyContent(
                verticalArrangement = Arrangement.Top,
                textColor = MaterialTheme.colorScheme.onSecondary.copy(0.7f),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = dimens.paddingEmptyContent)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(top = dimens.paddingM)
                    .padding(horizontal = dimens.paddingS) // to have the same effect of the padding used for each screen, as usual
            ) {
                items(
                    items = friendsList,
                    key = { it.uid } // to make only the interested UserModel recompose, if friendsList changes
                ) {
                    UserItem(
                        user = it,
                        navController = navController,
                        isSheetItem = true,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    )
                    Spacer(modifier = Modifier.padding(vertical = dimens.spacerXXS))
//                    HorizontalDivider(
//                        modifier = Modifier
//                            .padding(vertical = dimens.paddingS),
//                        thickness = dimens.dividerS,
//                        color = MaterialTheme.colorScheme.surface
//                    )
                }
            }
        }
    }
}
