package com.example.dog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dog.ApiResult
import com.example.dog.HomeRepository
import com.example.dog.domain.DogImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogViewModel @Inject constructor(
    private val dogRepository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DogImageUiState>(DogImageUiState.Loading)
    val uiState: StateFlow<DogImageUiState> = _uiState.asStateFlow()

    fun fetchRandomDogImage() {
        viewModelScope.launch {
            _uiState.value = DogImageUiState.Loading
            _uiState.value = when (val result = dogRepository.getRandomDogImage()) {
                is ApiResult.Success -> DogImageUiState.Success(result.data.first())
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