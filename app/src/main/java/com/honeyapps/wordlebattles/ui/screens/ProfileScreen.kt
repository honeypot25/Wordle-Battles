package com.honeyapps.wordlebattles.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.data.models.ProfileItemModel
import com.honeyapps.wordlebattles.ui.components.BottomNavBar
import com.honeyapps.wordlebattles.ui.components.ProfileFlag
import com.honeyapps.wordlebattles.ui.components.ProfilePhoto
import com.honeyapps.wordlebattles.ui.components.SectionHeader
import com.honeyapps.wordlebattles.ui.components.StatsSection
import com.honeyapps.wordlebattles.ui.components.TopBar
import com.honeyapps.wordlebattles.ui.dialogs.ConfirmationDialog
import com.honeyapps.wordlebattles.ui.dialogs.PhotoSourceDialog
import com.honeyapps.wordlebattles.ui.dialogs.TextFieldDialog
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.DialogsViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.UserViewModel
import com.honeyapps.wordlebattles.utils.LocationUtil
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = koinViewModel()
) {
    val label = stringResource(id = R.string.profile)
    val userState by userViewModel.userState.collectAsStateWithLifecycle()
    val isContentLoading by userViewModel.isContentLoading.collectAsStateWithLifecycle()

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
        // show CircularProgressIndicator during photo/flag update
        if (isContentLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.background,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
//                .padding(top = 72.dp)
                .padding(
                    top = paddingValues.calculateTopPadding() + dimens.spacerM,
                    bottom = paddingValues.calculateBottomPadding(),
                    start = dimens.paddingS,
                    end = dimens.paddingS
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfilePhoto(
                photoUrl = userState.photoUrl,
                photoSize = dimens.photoL,
                modifier = modifier
            )
            ProfileFlag(
                flag = userState.flag,
                flagDim = dimens.flagL,
                modifier = modifier
            )

            Spacer(modifier = Modifier.height(dimens.spacerL))

            SectionHeader(sectionName = stringResource(id = R.string.section_user))
            SignedUserSection(
                userViewModel = userViewModel
            )

            Spacer(modifier = Modifier.height(dimens.spacerL))

            SectionHeader(sectionName = stringResource(id = R.string.section_stats))
            StatsSection(
                uid = userState.uid
            )
        }
    }
}

@Composable
fun SignedUserSection(
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current
    val userState by userViewModel.userState.collectAsStateWithLifecycle()
    var clickedItem by remember {
        mutableStateOf<ProfileItemModel?>(null)
    }

    val userItems = listOf(
        ProfileItemModel.Username(label = stringResource(id = R.string.username)),
        ProfileItemModel.Photo(label = stringResource(id = R.string.photo)),
        ProfileItemModel.Flag(label = stringResource(id = R.string.flag)),
    )
    // used to programmatically to associate an item to its right Toast, used for feedback
    val itemToToastText = hashMapOf<Int, Pair<Int, Int>>(
        R.string.username to (
                R.string.username_update_success to R.string.username_update_fail
                ),
        R.string.photo to (
                R.string.photo_update_success to R.string.photo_update_fail
                ),
        R.string.flag to (
                R.string.flag_update_success to R.string.flag_update_fail
                )
    )

    Column {
        userItems.forEach { item ->
            // set displayedValue
            item.displayedValue = when (item.resId) {
                R.string.username -> userState.username
                R.string.flag -> userState.flag
                else -> null
            }
            SignedUserItem(
                item = item,
                onItemClick = {
                    clickedItem = item
                },
            )
            // since verticalArrangement = Arrangement.spacedBy(dimens.spacerM) isn't used as in a LazyColumn
            Spacer(modifier = Modifier.height(dimens.spacerM))
        }
    }

    // show the dialog corresponding to the clicked user item, and a feedback Toast after the action
    clickedItem?.let { item ->
        SignedUserItemClickHandler(
            item = item,
            onItemClicked = { clickedItem = null },
            onSuccess = {
                Toast.makeText(
                    ctx,
                    itemToToastText[item.resId]!!.first,
                    Toast.LENGTH_SHORT
                ).show()
            },
            onFail = {
                Toast.makeText(
                    ctx,
                    itemToToastText[item.resId]!!.second,
                    Toast.LENGTH_SHORT
                ).show()
            },
            userViewModel = userViewModel
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SignedUserItemClickHandler(
    item: ProfileItemModel,
    onItemClicked: () -> Unit,
    onSuccess: () -> Unit,
    onFail: () -> Unit,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    dialogsViewModel: DialogsViewModel = koinViewModel(),
) {
    val ctx = LocalContext.current
    val locationPermState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    when (item.resId) {
        R.string.username -> {
            dialogsViewModel.setTextFieldDialogVisibility(true)
            TextFieldDialog(
                label = stringResource(id = R.string.username_new),
                placeholder = stringResource(id = R.string.username),
                leadingIcon = Icons.Filled.FormatColorText,
                onDismiss = {
                    dialogsViewModel.setTextFieldDialogVisibility(false)
                    onItemClicked()
                },
                onConfirm = { newUsername ->
                    userViewModel.changeUsername(
                        newUsername = newUsername,
                        onSuccess = onSuccess,
                        onFail = onFail
                    )
                },
            )
        }
        R.string.photo -> {
            dialogsViewModel.setPhotoSourceDialogVisibility(true)
            PhotoSourceDialog(
                onDismiss= {
                    dialogsViewModel.setPhotoSourceDialogVisibility(false)
                    onItemClicked()
                },
                onSuccess = onSuccess,
                onFail = onFail
            )
        }
        R.string.flag -> {
            dialogsViewModel.setConfirmationDialogVisibility(true)
            ConfirmationDialog(
                icon = Icons.Filled.Flag,
                title = stringResource(id = R.string.change_flag),
                text = stringResource(id = R.string.change_flag_text),
                onDismiss = {
                    dialogsViewModel.setConfirmationDialogVisibility(false)
                    onItemClicked()
                },
                onConfirm = {
                    // check location permission
                    if (locationPermState.status.isGranted) {
                        // check GPS state
                        if (LocationUtil.isGpsEnabled(ctx = ctx)) {
                            // then change flag
                            userViewModel.changeFlag(
                                ctx = ctx,
                                onSuccess = onSuccess,
                                onFail = onFail
                            )
                            dialogsViewModel.setConfirmationDialogVisibility(false)
                            onItemClicked()
                            // no GPS
                        } else {
                            Toast.makeText(
                                ctx,
                                R.string.gps_enable,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        // no permission
                    } else {
                        locationPermState.launchPermissionRequest()
                    }
                }
            )
        }
    }
}

@Composable
fun SignedUserItem(
    item: ProfileItemModel,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.paddingS)
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // leading icon
        Icon(
            imageVector = item.icon,
            contentDescription = null,
//            modifier = Modifier
//                .size(dimens.iconS),
//                .padding(end = dimens.spacerSmall)
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(dimens.spacerMS))

        // label
        Text(
            text = item.label,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f) // to take the remaining space
        )

        // optional trailing value
        item.displayedValue?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
