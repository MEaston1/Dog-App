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
    val breed_group: String?,
    val bred_for: String?,
    val life_span: String?,
    val temperament: String?,
    @Json(name = "wikipedia_url") val wikipediaUrl: String?,
    @Json(name = "reference_image_id") val imageId: String?,
    val weight: Measurement?,
    val height: Measurement?
)

@JsonClass(generateAdapter = true)
data class Measurement(
    val imperial: String?,
    val metric: String?
)
