package com.honeyapps.wordlebattles.sign_in

import javax.annotation.concurrent.Immutable

@Immutable
data class SignInResult(
    val isSignInSuccessful: Boolean = false,
    val signInErr: String? = null
)