package com.honeyapps.wordlebattles.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.data.models.UserModel
import com.honeyapps.wordlebattles.ui.components.BottomNavBar
import com.honeyapps.wordlebattles.ui.components.ProfileFlag
import com.honeyapps.wordlebattles.ui.components.ProfilePhoto
import com.honeyapps.wordlebattles.ui.components.SectionHeader
import com.honeyapps.wordlebattles.ui.components.StatsSection
import com.honeyapps.wordlebattles.ui.components.TopBar
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.FriendsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserProfileScreen(
    userId: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    friendsViewModel: FriendsViewModel = koinViewModel()
) {
    var fetchedUser: UserModel? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        fetchedUser = friendsViewModel.fetchUser(userId = userId).await()
    }

    Scaffold(
        topBar = {
            TopBar(
                title = fetchedUser?.username ?: "...",
                navController = navController
            )
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
//            .padding(top = 72.dp),
                .padding(
                    top = paddingValues.calculateTopPadding() + dimens.spacerM,
                    bottom = paddingValues.calculateBottomPadding(),
                    start = dimens.paddingS,
                    end = dimens.paddingS
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfilePhoto(
                photoUrl = fetchedUser?.photoUrl,
                photoSize = dimens.photoL,
                modifier = modifier
            )
            ProfileFlag(
                flag = fetchedUser?.flag,
                flagDim = dimens.flagL,
                modifier = modifier
            )

            Spacer(modifier = Modifier.height(dimens.paddingL))

            SectionHeader(sectionName = stringResource(id = R.string.section_stats))
            StatsSection(
                uid = userId,
            )
        }
    }
}