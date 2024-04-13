package com.honeyapps.wordlebattles.data.models

import javax.annotation.concurrent.Immutable

@Immutable
data class CellModel(
    val coords: Pair<Int, Int> = 0 to 0,
    val letter: String = "",
    val isLetterInWord: Boolean = false,
    val isLetterInRightPlace: Boolean = false,
)