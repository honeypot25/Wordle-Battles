package com.honeyapps.wordlebattles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.auth.api.identity.Identity
import com.honeyapps.wordlebattles.navigation.NavGraphSetup
import com.honeyapps.wordlebattles.sign_in.GAuthUiClient
import com.honeyapps.wordlebattles.ui.theme.WordleBattlesTheme

class MainActivity : ComponentActivity() {

    private val gAuthUiClient by lazy {
        GAuthUiClient(
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WordleBattlesTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WordleBattlesApp(gAuthUiClient = gAuthUiClient)
                }
            }
        }
    }

//    override fun onStop() {
//        super.onStop()
////        stopKoin()
//        KoinModules.clearModules(getKoin())
//    }
}


@Composable
fun WordleBattlesApp(
    gAuthUiClient: GAuthUiClient,
) {

    // LoadingScreen()

    // Navigation setup
    NavGraphSetup(gAuthUiClient = gAuthUiClient)
}