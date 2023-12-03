package dev.testify.samples.flix.data.translator

import dev.testify.samples.flix.data.model.FlixPerson
import dev.testify.samples.flix.data.remote.tmdb.TheMovieDbUrlResolver
import dev.testify.samples.flix.data.remote.tmdb.entity.Person
import dev.testify.samples.flix.ui.common.util.toImagePromise

fun Person.toFlixPerson(urlResolver: TheMovieDbUrlResolver) = FlixPerson(
    id = this.id ?: 0,
    name = this.name,
    placeOfBirth = this.placeOfBirth,
    popularity = this.popularity ?: 0f,
    image = this.profilePath?.let { urlResolver.resolveImageUrl(this.profilePath).toImagePromise() },
    knownFor = this.knownForDepartment,
    biography = this.biography,
    birthday = this.birthday,
    deathday = this.deathday,
)
