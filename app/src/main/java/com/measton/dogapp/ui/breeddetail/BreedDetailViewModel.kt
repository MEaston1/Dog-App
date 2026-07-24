package com.measton.dogapp.ui.breeddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.measton.dogapp.ApiResult
import com.measton.dogapp.HomeRepository
import com.measton.dogapp.domain.Breed
import com.measton.dogapp.domain.DogImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedDetailViewModel @Inject constructor(
    private val dogRepository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BreedDetailUiState>(BreedDetailUiState.Loading)
    val uiState: StateFlow<BreedDetailUiState> = _uiState.asStateFlow()

    fun fetchBreedDetails(breedId: String) {
        viewModelScope.launch {
            _uiState.value = BreedDetailUiState.Loading
            _uiState.value = when (val breedResult = dogRepository.getBreedDetails(breedId)) {
                is ApiResult.Success -> {
                    // Images are supplementary; a failure there shouldn't blank the whole screen.
                    val images = (dogRepository.searchImages(4, breedId) as? ApiResult.Success)?.data.orEmpty()
                    BreedDetailUiState.Success(breedResult.data, images)
                }
                is ApiResult.Error -> BreedDetailUiState.Error
            }
        }
    }
}

sealed interface BreedDetailUiState {
    data object Loading : BreedDetailUiState
    data class Success(val breed: Breed, val images: List<DogImage>) : BreedDetailUiState
    data object Error : BreedDetailUiState
}