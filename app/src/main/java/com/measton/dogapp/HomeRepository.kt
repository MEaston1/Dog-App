package com.measton.dogapp

import com.measton.dogapp.domain.Breed
import com.measton.dogapp.domain.DogImage
import com.measton.dogapp.network.DogApiClient
import com.measton.dogapp.network.toDomain
import kotlinx.coroutines.CancellationException


/**
 * The seam the ViewModels depend on. Keeping it an interface means their tests need a
 * hand-written fake rather than a JVM-only mocking framework, so they stay portable.
 */
interface DogRepository {
    suspend fun searchImages(limit: Int, breedId: String): ApiResult<List<DogImage>>
    suspend fun getRandomDogImage(): ApiResult<DogImage>
    suspend fun getBreedDetails(breedId: String): ApiResult<Breed>
}

class HomeRepository (
    private val api: DogApiClient
) : DogRepository {
    override suspend fun searchImages(limit: Int, breedId: String): ApiResult<List<DogImage>> =
        apiCall {
            api.fetchImages(limit = limit, breedId = breedId).map { it.toDomain() }
        }

    override suspend fun getRandomDogImage(): ApiResult<DogImage> =
        apiCall { api.getRandomImage().first().toDomain() }

    override suspend fun getBreedDetails(breedId: String): ApiResult<Breed> =
        apiCall { api.getBreed(breedId = breedId).toDomain() }
}

private suspend fun <T> apiCall(block: suspend () -> T): ApiResult<T> =
    try {
        ApiResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ApiResult.Error(e)
    }
