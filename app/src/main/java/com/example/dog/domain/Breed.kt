package com.example.dog.domain

data class Breed(
    val id: String,
    val name: String,
    val breedGroup: String,
    val bredFor: String,
    val lifeSpan: String,
    val temperament: String,
    val wikipediaUrl: String,
    val referenceImageId: String,
    val weight: Measurement,
    val height: Measurement,
    )

data class Measurement(
    val imperial: String?,
    val metric: String?
)