package com.example.dog

import com.example.dog.network.BreedResponse
import com.example.dog.network.DogImageResponse
import com.example.dog.network.TheDogApi
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val api: TheDogApi
) {
    suspend fun searchImages(limit: Int, breedId: String): ApiResult<List<DogImageResponse>> {
        return try {
            val response = api.searchImages(limit = limit, breedId = breedId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return ApiResult.Success(body)
            }
            ApiResult.Error(Exception("response was not successful - ${response.code()}"))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    suspend fun getRandomDogImage(): ApiResult<List<DogImageResponse>> {
        return try {
            val body: List<DogImageResponse>?
            val response = api.getRandomImage()
            body = response.body()

            if (response.isSuccessful && !body.isNullOrEmpty() && !body.first().breeds.isNullOrEmpty()) {
                return ApiResult.Success(body)
            }
            ApiResult.Error(Exception("response was not successful - ${response.code()}"))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun getBreedDetails(breedId: String): ApiResult<BreedResponse> {
        return try {
            val response = api.getBreed(breedId)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.Error(Exception("response was not successful - ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
