package com.honeyapps.wordlebattles.data.models

import javax.annotation.concurrent.Immutable

@Immutable
data class MatchModel(
    val id: String = "",
    val user: PlayerModel = PlayerModel(),
    val friend: PlayerModel = PlayerModel(),
    val winner: String = "",
    val word: String = "",
)