package com.example.dog.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DogImageResponse(
    val breeds: List<BreedResponse>?,
    val id: String,
    val url: String
)

@JsonClass(generateAdapter = true)
data class BreedResponse(
    val id: String,
    val name: String,
    val temperament: String?,
    val description: String?,
    @Json(name = "wikipedia_url") val wikipediaUrl: String?,
    @Json(name = "reference_image_id") val imageId: String?,
)
