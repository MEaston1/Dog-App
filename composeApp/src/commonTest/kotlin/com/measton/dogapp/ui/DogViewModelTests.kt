package com.measton.dogapp.ui

import com.measton.dogapp.ApiResult
import com.measton.dogapp.FakeDogRepository
import com.measton.dogapp.domain.DogImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@ExperimentalCoroutinesApi
class DogViewModelTests {

    private lateinit var viewModel: DogViewModel
    private lateinit var dogRepository: FakeDogRepository

    // viewModelScope dispatches on Dispatchers.Main, which does not exist off-device, so
    // every class exercising a ViewModel needs its own setMain/resetMain pair.
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        dogRepository = FakeDogRepository()
        viewModel = DogViewModel(dogRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // A successful fetch should surface a Success state holding the returned dog.
    @Test
    fun testRandomDog() = runTest {
        val expectedDog = DogImage(emptyList(), "0XYvRd7oD", "https://cdn2.thedogapi.com/images/0XYvRd7oD.jpg")
        dogRepository.randomDogResult = ApiResult.Success(expectedDog)

        viewModel.fetchRandomDogImage()

        val state = assertIs<DogImageUiState.Success>(viewModel.uiState.value)
        assertEquals(expectedDog.url, state.dog.url)
    }

    // A failed fetch should surface the Error state rather than faking a dog.
    @Test
    fun testIncorrectDogError() = runTest {
        dogRepository.randomDogResult = ApiResult.Error(Exception("Error Fetching Dog"))

        viewModel.fetchRandomDogImage()

        assertEquals(DogImageUiState.Error, viewModel.uiState.value)
    }
}
