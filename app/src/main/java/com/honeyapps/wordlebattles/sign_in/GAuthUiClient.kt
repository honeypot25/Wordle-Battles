package com.honeyapps.wordlebattles.sign_in

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.honeyapps.wordlebattles.utils.env
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class GAuthUiClient(private val oneTapClient: SignInClient) {
    private val auth = FirebaseAuth.getInstance()

    suspend fun getSignInIntentSender(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        val webClientId = env["WEB_CLIENT_ID"]

        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val gIdToken = credential.googleIdToken
        val gCredentials = GoogleAuthProvider.getCredential(gIdToken, null)

        return try {
            val user = auth.signInWithCredential(gCredentials).await().user
            // subscribe to the user's own FCM topic to receive push notifications
//            user?.let {
//                FirebaseMessaging.getInstance().subscribeToTopic(user.uid)
//            }
            SignInResult(
                isSignInSuccessful = user != null,
                signInErr = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                isSignInSuccessful = false,
                signInErr = e.message
            )
        }
    }

    suspend fun signOut(
        onSuccess: () -> Unit,
    ) {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
            onSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun deleteAccount(
        onSuccess: (String) -> Unit,
        onFail: () -> Unit
    ) {
        try {
            val uid = auth.currentUser!!.uid
            auth.currentUser!!.delete()
                .addOnSuccessListener {
                    onSuccess(uid)
                }
                .addOnFailureListener {
                    onFail()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun isUserSignedIn(): Boolean = auth.currentUser != null
}