package com.honeyapps.wordlebattles.data.models

import com.google.firebase.Timestamp
import javax.annotation.concurrent.Immutable

@Immutable
data class ChallengeModel(
    val id: String = "",
    val timestamp: Timestamp? = null,
    val friend: UserModel = UserModel(),
)