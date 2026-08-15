package com.measton.dogapp

import com.measton.dogapp.domain.Breed
import com.measton.dogapp.domain.DogImage
import com.measton.dogapp.network.toDomain


class HomeRepository (
    private val api: TheDogApi
) {
    suspend fun searchImages(limit: Int, breedId: String): ApiResult<List<DogImage>> {
        return try {
            val response = api.searchImages(limit = limit, breedId = breedId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return ApiResult.Success(body.map { it.toDomain() })
            }
            ApiResult.Error(Exception("response was not successful - ${response.code()}"))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    suspend fun getRandomDogImage(): ApiResult<List<DogImage>> {
        return try {
            val response = api.getRandomImage()
            val body = response.body()

            if (response.isSuccessful && !body.isNullOrEmpty() && !body.first().breeds.isNullOrEmpty()) {
                return ApiResult.Success(body.map {it.toDomain()})
            }
            ApiResult.Error(Exception("response was not successful - ${response.code()}"))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun getBreedDetails(breedId: String): ApiResult<Breed> {
        return try {
            val response = api.getBreed(breedId)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                ApiResult.Success(body.toDomain())
            } else {
                ApiResult.Error(Exception("response was not successful - ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
