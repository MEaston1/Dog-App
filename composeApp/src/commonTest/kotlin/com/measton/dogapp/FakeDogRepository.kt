package com.measton.dogapp

import com.measton.dogapp.domain.Breed
import com.measton.dogapp.domain.DogImage

// using this instead of directly mocking DogRepository so it can be used in CommonMain later
class FakeDogRepository : DogRepository {

    var searchImagesResult: ApiResult<List<DogImage>> = notStubbed("searchImages")
    var randomDogResult: ApiResult<DogImage> = notStubbed("getRandomDogImage")
    var breedDetailsResult: ApiResult<Breed> = notStubbed("getBreedDetails")

    /** Arguments the ViewModel passed to [searchImages], for tests that care. */
    var searchImagesArgs: Pair<Int, String>? = null
        private set

    override suspend fun searchImages(limit: Int, breedId: String): ApiResult<List<DogImage>> {
        searchImagesArgs = limit to breedId
        return searchImagesResult
    }

    override suspend fun getRandomDogImage(): ApiResult<DogImage> = randomDogResult

    override suspend fun getBreedDetails(breedId: String): ApiResult<Breed> = breedDetailsResult
}

private fun <T> notStubbed(name: String): ApiResult<T> =
    ApiResult.Error(IllegalStateException("FakeDogRepository.$name was called but not stubbed"))
