package com.example.dog.ui

import com.example.dog.ApiResult
import com.example.dog.CoroutineTestExtension
import com.example.dog.HomeRepository
import com.example.dog.InstantExecutorExtension
import com.example.dog.network.DogImageResponse
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
    // Test to ensure that the fetchRandomDogImage function returns a random dog image as expected and correctly updates the dogImage state
    // with a valid response.
    @Test
    fun testRandomDog() = runTest {
        val expectedDogImage = DogImageResponse(emptyList(), "0XYvRd7oD", "https://cdn2.thedogapi.com/images/0XYvRd7oD.jpg")
        val apiResult = ApiResult.Success(listOf(expectedDogImage))
        whenever(dogRepository.getRandomDogImage()).thenReturn(apiResult)

        viewModel.fetchRandomDogImage()

        val actualDogImage = viewModel.dogImage.value

        assertNotNull(actualDogImage)
        assertNotNull(actualDogImage?.id)
        assertEquals(expectedDogImage.url, actualDogImage!!.url)
        assertNotNull(actualDogImage.breeds)
    }
    // Test to ensure that the "error dog" placeholder image takes over in the event of an error fetching a new dog
    @Test
    fun testIncorrectDogError() = runTest {
        val error = Exception("Error Fetching Dog")
        whenever(dogRepository.getRandomDogImage()).thenReturn(ApiResult.Error(error))

        viewModel.fetchRandomDogImage()

        val actualDogImage = viewModel.dogImage.value
        val errorDogImage = DogImageResponse(emptyList(), "error", "android.resource://com.example.dog/drawable/errordog")
        assertNotNull(actualDogImage)
        assertEquals(errorDogImage.url, actualDogImage!!.url)
    }
}