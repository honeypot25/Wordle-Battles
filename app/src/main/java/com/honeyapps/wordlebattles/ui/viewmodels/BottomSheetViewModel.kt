package com.honeyapps.wordlebattles.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class BottomSheetViewModel : ViewModel() {

    private val _isSheetVisible = mutableStateOf(false)
    val isSheetVisible = _isSheetVisible
    // default to true (sheet is still not displayed)
    private val _canTriggerShake = mutableStateOf(true)

    fun toggleSheetVisibility() {
        _isSheetVisible.value = !_isSheetVisible.value
        _canTriggerShake.value = !_canTriggerShake.value
    }

    fun handleShake() {
        if (_canTriggerShake.value) {
            toggleSheetVisibility()
        }
    }
}