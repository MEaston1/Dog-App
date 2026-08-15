package com.measton.dogapp.network

import com.measton.dogapp.domain.Breed
import com.measton.dogapp.domain.DogImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DogImageResponse(
    val breeds: List<BreedResponse>? = null,
    val id: String,
    val url: String
)

@Serializable
data class BreedResponse(
    val id: String,
    val name: String,
    @SerialName("breed_group") val breedGroup: String? = null,
    @SerialName("bred_for") val bredFor: String? = null,
    @SerialName("life_span") val lifeSpan: String? = null,
    val temperament: String? = null,
    @SerialName( "wikipedia_url") val wikipediaUrl: String? = null,
    @SerialName("reference_image_id") val imageId: String? = null,
    val weight: Measurement? = null,
    val height: Measurement? = null
)

@Serializable
data class Measurement(
    val imperial: String? = null,
    val metric: String? = null
)

fun Measurement?.toDomain() = com.measton.dogapp.domain.Measurement(
    imperial = this?.imperial.orEmpty(),
    metric = this?.metric.orEmpty(),
)

fun BreedResponse.toDomain() = Breed(
    id = id,
    name = name,
    breedGroup = breedGroup.orEmpty(),
    bredFor = bredFor.orEmpty(),
    lifeSpan = lifeSpan ?: "Unknown",
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