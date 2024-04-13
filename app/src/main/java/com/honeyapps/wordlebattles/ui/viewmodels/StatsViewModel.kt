package com.honeyapps.wordlebattles.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honeyapps.wordlebattles.data.models.StatsModel
import com.honeyapps.wordlebattles.data.repository.StatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsViewModel(
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _statsState = MutableStateFlow(StatsModel())
    val statsState = _statsState.asStateFlow()

    fun fetchStats(
        uid: String,
//        onFail: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // get the stats if the user exists
            val stats = statsRepository.getApiStats(uid = uid)
            // if successful request
            stats?.let {
                // if the user stats exist, get them
                if (stats.matches != -1) {
                    _statsState.value = it
                    // else create them
                } else {
                    statsRepository.createApiStats(uid = uid)
                }
                // else failed (but still 0 as default)
            } ?: Log.e("fetchStats", "Failed to fetch stats")
        }
    }

    fun updateStats(
        uid: String,
        matchId: String,
        providedMatch: Map<String, Int>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            statsRepository.updateApiStats(
                uid = uid,
                matchId = matchId,
                providedMatch = providedMatch
            )
        }
    }

    fun deleteStats(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            statsRepository.deleteApiStats(uid = uid)
            _statsState.value = StatsModel()
        }
    }
}