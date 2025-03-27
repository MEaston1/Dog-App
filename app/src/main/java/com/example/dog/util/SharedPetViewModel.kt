package com.example.dog.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class SharedPetViewModel @Inject constructor(): ViewModel() {
    private val _currentBreedId = MutableStateFlow<String?>(null)
    var currentBreedId: StateFlow<String?> = _currentBreedId.asStateFlow()

    fun updateCurrentBreedId(breedId: String?) {
        _currentBreedId.value = breedId
    }
}