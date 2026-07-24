package com.measton.dogapp.network

import com.measton.dogapp.domain.Breed
import com.measton.dogapp.domain.DogImage
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

fun Measurement?.toDomain() = com.measton.dogapp.domain.Measurement(
    imperial = this?.imperial.orEmpty(),
    metric = this?.metric.orEmpty(),
)

fun BreedResponse.toDomain() = Breed(
    id = id,
    name = name,
    breedGroup = breed_group.orEmpty(),
    bredFor = bred_for.orEmpty(),
    lifeSpan = life_span ?: "Unknown",
    temperament = temperament.orEmpty(),
    wikipediaUrl = wikipediaUrl.orEmpty(),
    referenceImageId = imageId.orEmpty(),
    weight = weight.toDomain(),
    height = height.toDomain(),
)

fun DogImageResponse.toDomain() = DogImage(
    breeds = breeds?.map{ it.toDomain()}.orEmpty(),
    id = id,
    url = url
    )