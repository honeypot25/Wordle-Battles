package com.honeyapps.wordlebattles.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.data.models.RecentMatchModel
import com.honeyapps.wordlebattles.ui.components.BottomNavBar
import com.honeyapps.wordlebattles.ui.components.EmptyContent
import com.honeyapps.wordlebattles.ui.components.FriendsBottomSheet
import com.honeyapps.wordlebattles.ui.components.MyPullRefreshIndicator
import com.honeyapps.wordlebattles.ui.components.ProfileFlag
import com.honeyapps.wordlebattles.ui.components.ProfilePhoto
import com.honeyapps.wordlebattles.ui.components.SectionHeader
import com.honeyapps.wordlebattles.ui.components.TopBar
import com.honeyapps.wordlebattles.ui.components.UserItem
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.ChallengeViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.RecentMatchViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.UserViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = koinViewModel()
) {
    val userState by userViewModel.userState.collectAsStateWithLifecycle()
    val label = stringResource(id = R.string.home)

    Scaffold(
        topBar = {
            TopBar(
                title = label
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
                    top = paddingValues.calculateTopPadding() + dimens.spacerM,
                    bottom = paddingValues.calculateBottomPadding(),
                    start = dimens.paddingS,
                    end = dimens.paddingS
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfilePhoto(
                photoUrl = userState.photoUrl,
                photoSize = dimens.photoM,
                modifier = modifier
            )
            ProfileFlag(
                flag = userState.flag,
                flagDim = dimens.flagM,
                modifier = modifier
            )

            Spacer(modifier = Modifier.height(dimens.spacerL))

            ShakingNotice()

            Spacer(modifier = Modifier.height(dimens.spacerL))

            SectionHeader(sectionName = stringResource(id = R.string.section_challenge_requests))
            ChallengeRequests(
                navController = navController
            )

            Spacer(modifier = Modifier.height(dimens.spacerM))

            Spacer(modifier = Modifier.height(dimens.spacerM))

            // Recent matches
            SectionHeader(sectionName = stringResource(id = R.string.section_recent_matches))
            RecentMatches(
                navController = navController,
            )
        }

        FriendsBottomSheet(
            navController = navController,
        )
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun ShakingNotice(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_phone_shake),
            contentDescription = null,
            modifier = Modifier
                .size(dimens.animatedIcon)
                .offset(shakingAnimation().value.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
        )
        Text(
            text = stringResource(id = R.string.action_shake),
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(dimens.paddingS),
        )
    }
}

@Composable
fun shakingAnimation(): State<Float> {
    val shakeAnimation = rememberInfiniteTransition(label = "")
    val initialValue = 0f
    val targetValue = 15f

    return shakeAnimation.animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            // total duration for 3 shakes and a delay
            animation = keyframes {
                durationMillis = 1200
                initialValue at 0 using FastOutSlowInEasing // start shake
                targetValue at 300 // end shake
                initialValue at 600 // reset for next shake
                initialValue at 900 // start delay
                initialValue at 1200 // end delay, to reset for next iteration
            },
//            animation = tween(durationMillis = 300, delayMillis = 150, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse // revert the direction upon reaching targetValue,
        ), label = ""
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChallengeRequests(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    challengeViewModel: ChallengeViewModel = koinViewModel(),
) {
    val challengeRequests by challengeViewModel.challengeRequests.collectAsStateWithLifecycle()
    val isRefreshing by challengeViewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        isRefreshing,
        onRefresh = { challengeViewModel.refreshScreen() }
    )

    Box(
        modifier = Modifier
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.TopCenter
//        horizontalAlignment = Alignment.CenterHorizontally, // if Column instead
    ) {
        if (challengeRequests.isEmpty()) {
            EmptyContent(
                verticalArrangement = Arrangement.Center,
                textColor = MaterialTheme.colorScheme.onBackground.copy(0.7f),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .height(125.dp)
//                    .padding(top = dimens.paddingXS)
                    .padding(horizontal = dimens.paddingXS)
            ) {
                items(
                    items = challengeRequests,
                    key = { it.id } // to make only the interested ChallengeModel recompose
                ) {
                    UserItem(
                        user = it.friend,
                        navController = navController,
                        isChallengeRequestItem = true,
                        challengeId = it.id,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    )
//                    HorizontalDivider(
//                        modifier = Modifier
//                            .padding(vertical = dimens.paddingS),
//                        thickness = dimens.dividerS,
//                        color = MaterialTheme.colorScheme.secondary
//                    )
                    Spacer(modifier = Modifier.padding(vertical = dimens.spacerXXS))
                }
            }
        }
        // top-layer
        MyPullRefreshIndicator(
            isRefreshing = isRefreshing,
            pullRefreshState = pullRefreshState,
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecentMatches(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    recentMatchViewModel: RecentMatchViewModel = koinViewModel(),
) {
    val recentMatches by recentMatchViewModel.recentMatches.collectAsStateWithLifecycle()
    val isRefreshing by recentMatchViewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        isRefreshing,
        onRefresh = { recentMatchViewModel.refreshScreen() }
    )

    Box(
        modifier = Modifier
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.TopCenter
//        horizontalAlignment = Alignment.CenterHorizontally, // if Column instead
    ) {
        if (recentMatches.isEmpty()) {
            EmptyContent(
                verticalArrangement = Arrangement.Center,
                textColor = MaterialTheme.colorScheme.onBackground.copy(0.7f),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .height(125.dp)
//                    .padding(top = dimens.paddingXS)
                    .padding(horizontal = dimens.paddingXS)
            ) {
                items(
                    items = recentMatches,
                    key = { it.id } // to make only the interested MatchModel recompose, if recentMatches changes
                ) {
                    RecentMatchItem(
                        recentMatch = it,
//                        onClick = {
//                            navController.navigate("matchResults/${it.id}")
//                        }
                    )
//                    HorizontalDivider(
//                        modifier = Modifier
//                            .padding(vertical = dimens.paddingS),
//                        thickness = dimens.dividerS,
//                        color = MaterialTheme.colorScheme.secondary
//                    )
                    Spacer(modifier = Modifier.padding(vertical = dimens.spacerXXS))
                }
            }
        }
        // top-layer
        MyPullRefreshIndicator(
            isRefreshing = isRefreshing,
            pullRefreshState = pullRefreshState,
        )
    }
}

@Composable
fun RecentMatchItem(
    recentMatch: RecentMatchModel,
//    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val user = recentMatch.user
    val userPlayer = recentMatch.userPlayer
    val userAttempts = userPlayer.attempts
    val userDuration = userPlayer.duration

    val friend = recentMatch.friend
    val friendPlayer = recentMatch.friendPlayer
    val friendAttempts = friendPlayer.attempts
    val friendDuration = friendPlayer.duration

    val defaultColor = Color.LightGray
    val resultsColor = when (recentMatch.winner) {
        user.uid -> Color.Green // user won
        "-" -> Color.Yellow     // tied
        friend.uid -> Color.Red // user lost
        else -> defaultColor    // still no winner
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
//            .clickable { onClick() }
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfilePhoto(
            photoUrl = friend.photoUrl,
            photoSize = dimens.photoS,
            modifier = modifier
                .padding(start = dimens.paddingM)
        )
        ProfileFlag(
            flag = friend.flag,
            flagDim = dimens.flagS,
            modifier = modifier
        )

        Spacer(modifier = Modifier.width(dimens.spacerM))

        // username
        Text(
            text = friend.username,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f) // to let it take the remaining space
        )

        // match results
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = dimens.paddingXXS)
                .padding(end = dimens.paddingM)
                .weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = resultsColor)) {
                            append("$userDuration")
                        }
                        append("s / ")
                        withStyle(style = SpanStyle(color = resultsColor)) {
                            append("$userAttempts")
                        }
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = defaultColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(dimens.spacerXXS))

                Text(
                    text = recentMatch.word.uppercase(),
                    color = Color.Cyan,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(dimens.spacerXXS))

                // check if friend has played, otherwise show "waiting..."
                if (friendPlayer.hasPlayed) {
                    Text(
                        text = "${friendDuration}s / $friendAttempts",
                        style = MaterialTheme.typography.labelMedium,
                        color = defaultColor,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.match_waiting),
                        style = MaterialTheme.typography.labelMedium,
                        color = defaultColor,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}