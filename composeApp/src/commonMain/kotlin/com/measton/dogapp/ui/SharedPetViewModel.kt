package com.measton.dogapp.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedPetViewModel : ViewModel() {
    private val _currentBreedId = MutableStateFlow<String?>(null)
    var currentBreedId: StateFlow<String?> = _currentBreedId.asStateFlow()

    fun updateCurrentBreedId(breedId: String?) {
        _currentBreedId.value = breedId
    }
}