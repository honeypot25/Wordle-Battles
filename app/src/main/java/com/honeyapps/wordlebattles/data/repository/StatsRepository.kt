package com.honeyapps.wordlebattles.data.repository

import android.util.Log
import com.honeyapps.wordlebattles.data.models.StatsModel
import com.honeyapps.wordlebattles.network.StatsApi.statsApiService


interface StatsRepository {
    suspend fun createApiStats(uid: String): Boolean
    suspend fun getApiStats(uid: String): StatsModel?
    suspend fun updateApiStats(uid: String, matchId: String, providedMatch: Map<String, Int>): Boolean
    suspend fun deleteApiStats(uid: String): Boolean
}

class StatsRepositoryImpl : StatsRepository {

    override suspend fun createApiStats(uid: String): Boolean =
        try {
            statsApiService.createStats(uid = uid).isSuccessful
        } catch (e: Exception) {
            Log.e("createApiStats","Failed to create stats: $e")
            false
        }

    override suspend fun getApiStats(uid: String): StatsModel? =
        try {
            statsApiService.getStats(uid = uid).body()!!
        } catch (e: Exception) {
            Log.e("getApiStats", "Failed to get stats: $e")
            null
        }

    override suspend fun updateApiStats(
        uid: String,
        matchId: String,
        providedMatch: Map<String, Int>
    ): Boolean =
        try {
            statsApiService.updateStats(
                uid = uid,
                matchId = matchId,
                providedMatch = providedMatch
            ).isSuccessful
        } catch (e: Exception) {
            Log.e("updateApiStats", "Failed to update stats: $e")
            false
        }

    override suspend fun deleteApiStats(uid: String): Boolean =
        try {
            statsApiService.deleteStats(
                uid = uid
            ).isSuccessful
        } catch (e: Exception) {
            Log.e("deleteApiStats", "Failed to delete stats: $e")
            false
        }
}