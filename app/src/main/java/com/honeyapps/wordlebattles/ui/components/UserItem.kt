package com.honeyapps.wordlebattles.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.data.models.UserModel
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.BottomSheetViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.ChallengeViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.FriendsViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
fun UserItem(
    user: UserModel,
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    isChallengeRequestItem: Boolean = false,
    challengeId: String? = null,
    isSheetItem: Boolean = false,
    isMyFriendsItem: Boolean = false,
    isFriendRequestItem: Boolean = false,
    isSearchItem: Boolean = false,
    bottomSheetViewModel: BottomSheetViewModel = koinViewModel(),
    challengeViewModel: ChallengeViewModel = koinViewModel(),
    friendsViewModel: FriendsViewModel = koinViewModel(),
) {
    val ctx = LocalContext.current
    var iconButtonsList: List<Map<Any, Any>> = listOf()

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfilePhoto(
            photoUrl = user.photoUrl,
            photoSize = dimens.photoS,
            modifier = modifier
                .padding(start = dimens.paddingM)
//                .padding(vertical = dimens.paddingXS)
        )
//        ProfileFlag(
//            flag = user.flag,
//            flagDim = dimens.flagS,
//            modifier = modifier
//                .padding(vertical = dimens.paddingXS)
//        )

        Spacer(modifier = Modifier.width(dimens.spacerM))

        // username
        Text(
            text = user.username,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f) // to let it take the remaining space
        )

        // icons
        if (isChallengeRequestItem) {
            iconButtonsList = listOf(
                // Accept icon button to accept the challenge request
                mapOf(
                    "source" to Icons.Default.PlayArrow,
                    "onClick" to {
                        challengeViewModel.handleChallengeRequest(
                            challengeId = challengeId!!,
                            onSuccess = {
                                // feedback
                                Toast.makeText(
                                    ctx,
                                    R.string.match_joining,
                                    Toast.LENGTH_SHORT
                                ).show()
                                // navigate to the existing match, created by the friend
                                val isMatchCreatedByUser = false
                                navController!!.navigate("matches/${challengeId}?isMatchCreatedByUser=${isMatchCreatedByUser}&friendId=${user.uid}")
                            },
                            onFail = {
                                Toast.makeText(
                                    ctx,
                                    R.string.challenge_request_accept_fail,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        )
                    },
                    "tint" to Color.Green
                ),
                mapOf(
                    // Reject icon button to reject the challenge request
                    "source" to Icons.Default.Clear,
                    "onClick" to {
                        challengeViewModel.handleChallengeRequest(
                            challengeId = challengeId!!,
                            onSuccess = {
                                Toast.makeText(
                                    ctx,
                                    R.string.challenge_request_reject_success,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFail = {
                                Toast.makeText(
                                    ctx,
                                    R.string.challenge_request_reject_fail,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    "tint" to Color.Red
                )
            )
        }

        else if (isSheetItem) {
            // create the id of the new match
            val newChallengeId = UUID.randomUUID().toString()
            val joiningMsg = "${stringResource(id = R.string.friend_challenge_success)}\n${stringResource(id = R.string.match_joining)}"
            iconButtonsList = listOf(
                mapOf(
                    // Swords icon button to challenge the friend
                    "source" to R.drawable.ic_challenge_friend,
                    "onClick" to {
                        challengeViewModel.sendChallengeRequest(
                            challengeId = newChallengeId,
                            friendId = user.uid,
                            onSuccess = {
                                Toast.makeText(
                                    ctx,
                                    joiningMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                                bottomSheetViewModel.toggleSheetVisibility()
                                // navigate to the new match, created by the current user
                                val isMatchCreatedByUser = true
                                navController!!.navigate("matches/${newChallengeId}?isMatchCreatedByUser=${isMatchCreatedByUser}&friendId=${user.uid}")
                            },
                            onFail = {
                                Toast.makeText(
                                    ctx,
                                    R.string.friend_challenge_fail,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        )


                    },
                    "tint" to MaterialTheme.colorScheme.primary,
                )
            )
        }

        else if (isMyFriendsItem) {
            iconButtonsList = listOf(
                mapOf(
                    // Remove icon button to remove the friend
                    "source" to Icons.Default.DeleteForever,
                    "onClick" to {
                        friendsViewModel.removeFriend(
                            friend = user,
                            onSuccess = {
                                Toast.makeText(
                                    ctx,
                                    R.string.friend_remove_success,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFail = {
                                Toast.makeText(
                                    ctx,
                                    R.string.friend_remove_fail,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    "tint" to Color.Red,
                ),
                mapOf(
                    // Navigation icon to open its profile
                    "source" to Icons.AutoMirrored.Filled.NavigateNext,
                    "onClick" to {
                        navController!!.navigate("users/${user.uid}")
                    },
                    "tint" to MaterialTheme.colorScheme.onSurface,
                )
            )
        }

        else if (isFriendRequestItem) {
            iconButtonsList = listOf(
                mapOf(
                    // Accept icon button to accept the friend request
                    "source" to Icons.Default.Done,
                    "onClick" to {
                        friendsViewModel.acceptFriendReq(
                            friend = user,
                            onSuccess = {
                                Toast.makeText(
                                    ctx,
                                    R.string.request_accept_success,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFail = {
                                Toast.makeText(
                                    ctx,
                                    R.string.request_accept_fail,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        )
                    },
                    "tint" to Color.Green,
                ),
                mapOf(
                    // Reject icon button to reject the friend request
                    "source" to Icons.Default.Clear,
                    "onClick" to {
                        friendsViewModel.rejectFriendReq(
                            friend = user,
                            onSuccess = {
                                Toast.makeText(
                                    ctx,
                                    R.string.request_reject_success,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFail = {
                                Toast.makeText(
                                    ctx,
                                    R.string.request_reject_fail,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        )
                    },
                    "tint" to Color.Red,
                ),
                mapOf(
                    // Navigation icon to open its profile
                    "source" to Icons.AutoMirrored.Filled.NavigateNext,
                    "onClick" to {
                        navController!!.navigate("users/${user.uid}")
                    },
                    "tint" to MaterialTheme.colorScheme.onSurface,
                )
            )
        }

        else if (isSearchItem) {
            iconButtonsList = listOf(
                mapOf(
                    // Add icon button to send a friend request
                    "source" to Icons.Default.PersonAdd,
                    "onClick" to {
                        friendsViewModel.sendFriendReq(
                            friendId = user.uid,
                            onSuccess = {
                                Toast.makeText(
                                    ctx,
                                    R.string.request_send_success,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFail = {
                                Toast.makeText(
                                    ctx,
                                    R.string.request_send_fail,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        )
                    },
                    "tint" to Color.Green,
                ),
                mapOf(
                    // Navigation icon to open its profile
                    "source" to Icons.AutoMirrored.Filled.NavigateNext,
                    "onClick" to {
                        navController!!.navigate("users/${user.uid}")
                    },
                    "tint" to MaterialTheme.colorScheme.onSurface,
                )
            )
        }

        // icon buttons
        if (iconButtonsList.isNotEmpty()) {
            UserIconButtons(
                userId = user.uid,
                iconButtonsList = iconButtonsList
            )
        }
    }
}

@Composable
fun UserIconButtons(
    userId: String,
    modifier: Modifier = Modifier,
    iconButtonsList: List<Map<Any, Any>>,
) {
    // end-arranged row of icon(s)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End
    ) {
        for (map in iconButtonsList) {
            val source = map["source"]
            val onClick = map["onClick"] as () -> Unit
            val tint = map["tint"] as Color

            IconButton(
                onClick = { onClick() },
            ) {
                // true only for a bottom sheet friend item, which needs a painter (and so an Int resource id)
                if (source == R.drawable.ic_challenge_friend) {
                    Icon(
                        painter = painterResource(id = source as Int),
                        contentDescription = null,
                        tint = tint
                    )
                    // otherwise an imageVector
                } else {
                    Icon(
                        imageVector = source as ImageVector,
                        contentDescription = null,
                        tint = tint
                    )
                }
            }
        }
    }
}
