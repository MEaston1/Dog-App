package com.measton.dogapp.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheDogApi {
    @GET("images/search")
    suspend fun searchImages(
        @Query("limit") limit: Int,
        @Query("breed_ids") breedId: String,
        @Query("has_breeds") hasBreeds: Boolean = true
    ): Response<List<DogImageResponse>>

    @GET("images/search")
    suspend fun getRandomImage(
        @Query("has_breeds") hasBreeds: Boolean = true
    ): Response<List<DogImageResponse>>

    @GET("breeds/{breed_id}")
    suspend fun getBreed(@Path("breed_id") breedId: String): Response<BreedResponse>
}
