package dev.testify.samples.flix.domain.model

data class PersonDomainModel(
    val id: Int,
    val name: String,
    val originalName: String,
    val characterName: String,
    val popularity: Double?,
    val imageUrl: String?
)
