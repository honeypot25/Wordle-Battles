package com.honeyapps.wordlebattles.sign_in

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignInViewModel : ViewModel() {

    private val _signInState = MutableStateFlow(SignInResult())
    val signInState = _signInState.asStateFlow()

    fun onSignIn(signInResult: SignInResult) {
        _signInState.value = SignInResult(
            isSignInSuccessful = signInResult.isSignInSuccessful,
            signInErr = signInResult.signInErr
        )
    }

    fun resetSignInState() {
        _signInState.value = SignInResult()
    }
}