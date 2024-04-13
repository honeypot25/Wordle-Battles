package com.honeyapps.wordlebattles.data.models

import androidx.compose.runtime.Immutable

@Immutable
data class KeyModel(
    val coords: Pair<Int, Int> = 0 to 0,
    val letter: String = "",
    val isLetterInRightPlace: Boolean = false,
    val isLetterInWord: Boolean = false,
    val isAvailable: Boolean = true,
)