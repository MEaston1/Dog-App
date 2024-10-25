package com.example.dog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dog.ApiResult
import com.example.dog.HomeRepository
import com.example.dog.network.BreedResponse
import com.example.dog.network.DogImageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogViewModel @Inject constructor(
    private val dogRepository: HomeRepository
) : ViewModel() {

    private val _dogImage = MutableStateFlow<DogImageResponse?>(null)
    val dogImage: StateFlow<DogImageResponse?> = _dogImage

    private val _breedDetails = MutableStateFlow<BreedResponse?>(null)
    val breedDetails: StateFlow<BreedResponse?> = _breedDetails

    private val _breedImages = MutableStateFlow<List<DogImageResponse>?>(null)
    val breedImages: StateFlow<List<DogImageResponse>?> = _breedImages

    private val error = DogImageResponse(emptyList(), "error", "android.resource://com.example.dog/drawable/errordog")

    fun fetchRandomDogImage() {
        viewModelScope.launch {
            val result = dogRepository.getRandomDogImage()
            if (result is ApiResult.Success) {
                _dogImage.value = result.data.first()
            } else if (result is ApiResult.Error) {
                ApiResult.Error(Exception("response was not successful - ${result.exception}"))
                _dogImage.value = error
            }
        }
    }

    fun fetchBreedDetails(breedId: String) {
        viewModelScope.launch {
            val breedDetailsResult = dogRepository.getBreedDetails(breedId)
            if (breedDetailsResult is ApiResult.Success) {
                _breedDetails.value = breedDetailsResult.data
            } else if (breedDetailsResult is ApiResult.Error) {
                ApiResult.Error(Exception("response was not successful - ${breedDetailsResult.exception}"))
            }

            val breedImagesResult = dogRepository.searchImages(4, breedId)
            if (breedImagesResult is ApiResult.Success) {
                _breedImages.value = breedImagesResult.data
            } else if (breedImagesResult is ApiResult.Error) {
                ApiResult.Error(Exception("response was not successful - ${breedImagesResult.exception}"))
            }
        }
    }

}