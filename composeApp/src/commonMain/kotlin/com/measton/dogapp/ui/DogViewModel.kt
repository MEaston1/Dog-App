package com.measton.dogapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.measton.dogapp.ApiResult
import com.measton.dogapp.DogRepository
import com.measton.dogapp.domain.DogImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DogViewModel (
    private val dogRepository: DogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DogImageUiState>(DogImageUiState.Loading)
    val uiState: StateFlow<DogImageUiState> = _uiState.asStateFlow()

    fun fetchRandomDogImage() {
        viewModelScope.launch {
            _uiState.value = DogImageUiState.Loading
            _uiState.value = when (val result = dogRepository.getRandomDogImage()) {
                is ApiResult.Success -> DogImageUiState.Success(result.data)
                is ApiResult.Error -> DogImageUiState.Error
            }
        }
    }
}

sealed interface DogImageUiState {
    data object Loading : DogImageUiState
    data class Success(val dog: DogImage) : DogImageUiState
    data object Error : DogImageUiState
}