package dev.testify.samples.flix.data.model

import dev.testify.samples.flix.ui.common.util.ImagePromise

data class FlixPerson(
    val id: Int,
    val name: String?,
    val popularity: Float,
    val knownFor: String?,
    val biography: String?,
    val placeOfBirth: String?,
    val birthday: String?,
    val deathday: String?,
    val image: ImagePromise?,
)
