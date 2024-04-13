package com.honeyapps.wordlebattles.data.models

import androidx.compose.runtime.Immutable

@Immutable
data class UserModel(
    val uid: String = "",
    val username: String = "",
    val photoUrl: String = "",
    val flag: String = "",
)