package com.honeyapps.wordlebattles.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.ui.components.BottomNavBar
import com.honeyapps.wordlebattles.ui.components.TopBar
import com.honeyapps.wordlebattles.ui.theme.dimens

@Composable
fun FeedbackAndSupportScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(id = R.string.feedback_and_support)

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = dimens.paddingS,
                    end = dimens.paddingS
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

        }
    }
}