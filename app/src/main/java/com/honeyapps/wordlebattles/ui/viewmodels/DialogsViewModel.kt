package com.honeyapps.wordlebattles.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DialogsViewModel : ViewModel() {

    private val _textFieldDialogVisibility = MutableStateFlow(false)
    val textFieldDialogVisibility = _textFieldDialogVisibility.asStateFlow()

    private val _photoSourceDialogVisibility = MutableStateFlow(false)
    val photoSourceDialogVisibility = _photoSourceDialogVisibility.asStateFlow()

    private val _confirmationDialogVisibility = MutableStateFlow(false)
    val confirmationDialogVisibility = _confirmationDialogVisibility.asStateFlow()

    private val _matchResultDialogVisibility = MutableStateFlow(false)
    val matchResultDialogVisibility = _matchResultDialogVisibility.asStateFlow()

    fun setPhotoSourceDialogVisibility(visible: Boolean) {
        _photoSourceDialogVisibility.value = visible
    }

    fun setTextFieldDialogVisibility(visible: Boolean) {
        _textFieldDialogVisibility.value = visible
    }

    fun setConfirmationDialogVisibility(visible: Boolean) {
        _confirmationDialogVisibility.value = visible
    }
    fun setMatchResultDialogVisibility(visible: Boolean) {
        _matchResultDialogVisibility.value = visible
    }
}