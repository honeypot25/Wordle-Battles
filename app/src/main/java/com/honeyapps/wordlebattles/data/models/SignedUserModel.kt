package com.honeyapps.wordlebattles.data.models

import androidx.compose.runtime.Immutable

@Immutable
data class SignedUserModel(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val flag: String = "",
//    val coins: Int = 0,
)