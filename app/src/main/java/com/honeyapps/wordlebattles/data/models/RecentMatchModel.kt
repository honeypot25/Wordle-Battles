package com.honeyapps.wordlebattles.data.models

import com.google.firebase.Timestamp
import javax.annotation.concurrent.Immutable

@Immutable
data class RecentMatchModel(
    val id: String = "",
    val user: UserModel = UserModel(),
    val userPlayer: PlayerModel = PlayerModel(),
    val friend: UserModel = UserModel(),
    val friendPlayer: PlayerModel = PlayerModel(),
    val timestamp: Timestamp? = null,
    val winner: String = "",
    val word: String = "",
)