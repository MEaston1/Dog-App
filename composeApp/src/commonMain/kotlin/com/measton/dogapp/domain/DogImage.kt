package com.measton.dogapp.domain

data class DogImage(
    val breeds: List<Breed>,
    val id : String,
    val url: String
)
