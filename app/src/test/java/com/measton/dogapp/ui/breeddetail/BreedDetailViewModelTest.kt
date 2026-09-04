package com.measton.dogapp.ui.breeddetail

import com.measton.dogapp.ApiResult
import com.measton.dogapp.CoroutineTestExtension
import com.measton.dogapp.FakeDogRepository
import com.measton.dogapp.domain.Breed
import com.measton.dogapp.domain.DogImage
import com.measton.dogapp.domain.Measurement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@ExtendWith(CoroutineTestExtension::class)
class BreedDetailViewModelTest {

    private lateinit var viewModel: BreedDetailViewModel
    private lateinit var dogRepository: FakeDogRepository

    @BeforeEach
    fun setUp() {
        dogRepository = FakeDogRepository()
        viewModel = BreedDetailViewModel(dogRepository)
    }

    @Test
    fun `a successful fetch surfaces the breed and its images`() = runTest {
        dogRepository.breedDetailsResult = ApiResult.Success(AFFENPINSCHER)
        dogRepository.searchImagesResult = ApiResult.Success(listOf(IMAGE))

        viewModel.fetchBreedDetails("1")

        val state = assertIs<BreedDetailUiState.Success>(viewModel.uiState.value)
        assertEquals("Affenpinscher", state.breed.name)
        assertEquals(listOf(IMAGE), state.images)
        assertEquals(4 to "1", dogRepository.searchImagesArgs)
    }

    // Images are supplementary. Losing them must not blank a screen whose breed data loaded
    // fine - this is the one piece of real branching in the ViewModel.
    @Test
    fun `a failed image search still yields Success with no images`() = runTest {
        dogRepository.breedDetailsResult = ApiResult.Success(AFFENPINSCHER)
        dogRepository.searchImagesResult = ApiResult.Error(Exception("images unavailable"))

        viewModel.fetchBreedDetails("1")

        val state = assertIs<BreedDetailUiState.Success>(viewModel.uiState.value)
        assertEquals("Affenpinscher", state.breed.name)
        assertTrue(state.images.isEmpty())
    }

    @Test
    fun `a failed breed fetch surfaces Error`() = runTest {
        dogRepository.breedDetailsResult = ApiResult.Error(Exception("breed unavailable"))

        viewModel.fetchBreedDetails("1")

        assertEquals(BreedDetailUiState.Error, viewModel.uiState.value)
    }
}

private val AFFENPINSCHER = Breed(
    id = "1",
    name = "Affenpinscher",
    breedGroup = "Toy",
    bredFor = "Small rodent hunting, lapdog",
    lifeSpan = "12-15",
    temperament = "Stubborn, Curious, Playful",
    wikipediaUrl = "",
    referenceImageId = "0LJiOVlxp",
    weight = Measurement("7-10", "3.2-4.5"),
    height = Measurement("9-11.5", "23-29"),
)

private val IMAGE = DogImage(
    breeds = emptyList(),
    id = "0XYvRd7oD",
    url = "https://cdn2.thedogapi.com/images/0XYvRd7oD.jpg",
)
