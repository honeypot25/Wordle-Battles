package com.honeyapps.wordlebattles.data.models

import androidx.compose.runtime.Immutable
import com.google.firebase.firestore.DocumentReference

@Immutable
data class PlayerModel(
    val user: DocumentReference? = null,
    val hasPlayed: Boolean = false,
    val attempts: Int = 0,
    val duration: Int = 0,
)