package com.honeyapps.wordlebattles.data.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class StatsModel(
    val matches: Int = 0,
    val wins: Int = 0,
    val ties: Int = 0,
    val losses: Int = 0,
)