package com.measton.dogapp.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class DogApiClient (private val client: HttpClient) {

    suspend fun fetchImages(
        limit: Int,
        breedId: String,
        hasBreeds: Boolean = true
    ) : List<DogImageResponse> =
        client.get("images/search") {
            parameter("limit", limit)
            parameter("breed_id", breedId)
            parameter("has_breeds", hasBreeds)
        }.body()

    suspend fun getRandomImage(hasBreeds: Boolean = true) : List<DogImageResponse> =
        client.get("images/search") {
            parameter("has_breeds", hasBreeds)
        }.body()

    suspend fun getBreed(breedId: String) : BreedResponse =
        client.get("breeds/$breedId").body()
}