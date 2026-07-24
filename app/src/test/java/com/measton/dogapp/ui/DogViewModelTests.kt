package com.measton.dogapp.ui

import com.measton.dogapp.ApiResult
import com.measton.dogapp.CoroutineTestExtension
import com.measton.dogapp.HomeRepository
import com.measton.dogapp.InstantExecutorExtension
import com.measton.dogapp.domain.DogImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@ExtendWith(CoroutineTestExtension::class, InstantExecutorExtension::class)
class DogViewModelTests {

    private lateinit var viewModel: DogViewModel
    private lateinit var  dogRepository: HomeRepository

    @BeforeEach
    fun setUp() {
        dogRepository = mock()
        viewModel = DogViewModel(dogRepository)
    }
    // A successful fetch should surface a Success state holding the returned dog.
    @Test
    fun testRandomDog() = runTest {
        val expectedDog = DogImage(emptyList(), "0XYvRd7oD", "https://cdn2.thedogapi.com/images/0XYvRd7oD.jpg")
        whenever(dogRepository.getRandomDogImage()).thenReturn(ApiResult.Success(listOf(expectedDog)))

        viewModel.fetchRandomDogImage()

        val state = viewModel.uiState.value
        assertTrue(state is DogImageUiState.Success)
        assertEquals(expectedDog.url, (state as DogImageUiState.Success).dog.url)
    }
    // A failed fetch should surface the Error state rather than faking a dog.
    @Test
    fun testIncorrectDogError() = runTest {
        whenever(dogRepository.getRandomDogImage()).thenReturn(ApiResult.Error(Exception("Error Fetching Dog")))

        viewModel.fetchRandomDogImage()

        assertEquals(DogImageUiState.Error, viewModel.uiState.value)
    }
}