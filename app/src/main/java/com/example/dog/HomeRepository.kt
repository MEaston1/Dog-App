package com.example.dog

import com.example.dog.network.BreedResponse
import com.example.dog.network.DogImageResponse
import com.example.dog.network.TheDogApi
import retrofit2.Response
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
    // I added a do-while loop to ensure that the response is successful and that the body is not null or empty.
    // This is because the majority of the returned responses did not include a full breed response, and only included the image id and url.
    suspend fun getRandomDogImage(): ApiResult<List<DogImageResponse>> {
        return try {
            var response: Response<List<DogImageResponse>>
            var body: List<DogImageResponse>?
            do {
                response = api.getRandomImage()
                body = response.body()
            } while (response.isSuccessful && (body.isNullOrEmpty() || body.first().breeds!!.isEmpty()))

            if (response.isSuccessful && body != null) {
                return ApiResult.Success(body)
            }
            ApiResult.Error(Exception("response was not successful - ${response.code()}"))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun getBreedDetails(breedId: String): ApiResult<BreedResponse> {
        return try {
            val response = api.searchImages(limit = 4, breedId = breedId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return ApiResult.Success(body.first().breeds?.first()!!)
            }
            ApiResult.Error(Exception("response was not successful - ${response.code()}"))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
