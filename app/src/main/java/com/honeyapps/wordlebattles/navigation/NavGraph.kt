package com.honeyapps.wordlebattles.navigation

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.sign_in.GAuthUiClient
import com.honeyapps.wordlebattles.sign_in.SignInResult
import com.honeyapps.wordlebattles.sign_in.SignInViewModel
import com.honeyapps.wordlebattles.ui.screens.AboutScreen
import com.honeyapps.wordlebattles.ui.screens.FeedbackAndSupportScreen
import com.honeyapps.wordlebattles.ui.screens.FriendsScreen
import com.honeyapps.wordlebattles.ui.screens.HomeScreen
import com.honeyapps.wordlebattles.ui.screens.HowToPlayScreen
import com.honeyapps.wordlebattles.ui.screens.MatchScreen
import com.honeyapps.wordlebattles.ui.screens.MenuScreen
import com.honeyapps.wordlebattles.ui.screens.ProfileScreen
import com.honeyapps.wordlebattles.ui.screens.Screens
import com.honeyapps.wordlebattles.ui.screens.SettingsScreen
import com.honeyapps.wordlebattles.ui.screens.ShopScreen
import com.honeyapps.wordlebattles.ui.screens.SignInScreen
import com.honeyapps.wordlebattles.ui.screens.UserProfileScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun NavGraphSetup(
    gAuthUiClient: GAuthUiClient,
    signInViewModel: SignInViewModel = koinViewModel(),
) {
    val navController: NavHostController = rememberNavController()

    // build the NavGraph starting from Sign In screen
    NavHost(navController = navController, startDestination = Screens.SignIn.name) {

        // Sign In screen
        composable(route = Screens.SignIn.name) {
            val ctx = LocalContext.current
            val signInState: SignInResult by signInViewModel.signInState.collectAsStateWithLifecycle()

            // visual skip to Home screen if user is already signed in
            LaunchedEffect(key1 = Unit) {
                if (gAuthUiClient.isUserSignedIn()) {
                    navController.navigate(Screens.Home.name)
                }
            }

            // Toast for successful Sign In (proceeding to Home)
            LaunchedEffect(key1 = signInState.isSignInSuccessful) {
                if (signInState.isSignInSuccessful) {
                    Toast.makeText(
                        ctx,
                        R.string.sign_in_success,
                        Toast.LENGTH_SHORT
                    ).show()

                    navController.navigate(Screens.Home.name) {
                        popUpTo(navController.graph.startDestinationRoute!!)
                    }
                    signInViewModel.resetSignInState()
                }
            }

            // Toast for failed Sign In
            LaunchedEffect(key1 = signInState.signInErr != null) {
                if (signInState.signInErr != null) {
                    Toast.makeText(
                        ctx,
                        R.string.sign_in_fail,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // out from onSignInBtnClick since the rememberLauncherForActivityResult composable
            val ioScope = rememberCoroutineScope { Dispatchers.IO }
            val activityResultLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult()
            ) { activityResult ->
                if (activityResult.resultCode == RESULT_OK) {
                    ioScope.launch {
                        val signInResult = gAuthUiClient.signInWithIntent(
                            intent = activityResult.data ?: return@launch
                        )
                        // update signInState
                        signInViewModel.onSignIn(signInResult = signInResult)
                    }
                }
            }
            SignInScreen(
                onSignInBtnClick = {
                    ioScope.launch {
                        val signInIntentSender = gAuthUiClient.getSignInIntentSender()
                        activityResultLauncher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                },
            )
        }

        // Menu screen
        composable(route = Screens.Menu.name) {
            MenuScreen(
                gAuthUiClient = gAuthUiClient,
                navController = navController,
            )
        }

        // Home screen
        composable(route = Screens.Home.name) {
            HomeScreen(
                navController = navController,
            )
        }

        // Profile screen
        composable(route = Screens.Profile.name) {
            ProfileScreen(
                navController = navController,
            )
        }

        // Settings screen
        composable(route = Screens.Settings.name) {
            SettingsScreen(
                navController = navController,
            )
        }

        // Friends screen
        composable(route = Screens.Friends.name) {
            FriendsScreen(
                navController = navController,
            )
        }

        // Shop screen
        composable(route = Screens.Shop.name) {
            ShopScreen(
                navController = navController,
            )
        }

        // Feedback & Support screen
        composable(route = Screens.FeedbackAndSupport.name) {
            FeedbackAndSupportScreen(
                navController = navController,
            )
        }

        // HowToPlay screen
        composable(route = Screens.HowToPlay.name) {
            HowToPlayScreen(
                navController = navController,
            )
        }

        // About screen
        composable(route = Screens.About.name) {
            AboutScreen(
                navController = navController,
            )
        }

        // Screen of the match with a friend
        composable(
            route = "matches/{matchId}?isMatchCreatedByUser={isMatchCreatedByUser}&friendId={friendId}",
            arguments = listOf(
                navArgument("matchId") { type = NavType.StringType },
                navArgument("isMatchCreatedByUser") { type = NavType.BoolType },
                navArgument("friendId") { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            val args = navBackStackEntry.arguments!!
            val matchId = args.getString("matchId")!!
            val isMatchCreatedByUser = args.getBoolean("isMatchCreatedByUser")
            val friendId = args.getString("friendId")!!
            MatchScreen(
                matchId = matchId,
                isMatchCreatedByUser = isMatchCreatedByUser,
                friendId = friendId,
                navController = navController
            )
        }

        // Screen of the selected user
        composable(
            route = "users/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val userId = navBackStackEntry.arguments?.getString("userId")
            UserProfileScreen(
                userId = userId!!,
                navController = navController
            )
        }
    }
}
