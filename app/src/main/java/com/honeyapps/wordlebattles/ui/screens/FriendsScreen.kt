package com.honeyapps.wordlebattles.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.ui.components.BottomNavBar
import com.honeyapps.wordlebattles.ui.components.TabsRow
import com.honeyapps.wordlebattles.ui.components.TopBar

@Composable
fun FriendsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(id = R.string.friends)

    Scaffold(
        topBar = {
            TopBar(
                title = label,
                navController = navController
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
            )
        }
    ) { paddingValues ->
        // important:
        // the page content of the two tabs is handled by the HorizontalPager in the TabsRow composable
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
//                    start = dimens.paddingS,
//                    end = dimens.paddingS
                ),
            horizontalArrangement = Arrangement.Center,
        ) {
            TabsRow(
                navController = navController,
            )
        }
    }
}