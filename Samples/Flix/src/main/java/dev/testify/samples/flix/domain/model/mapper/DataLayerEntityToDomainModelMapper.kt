/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2023 ndtp
 * Original work copyright (c) 2023 Andrew Carmichael
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package dev.testify.samples.flix.domain.model.mapper

import dev.testify.samples.flix.data.remote.tmdb.TheMovieDbUrlResolver
import dev.testify.samples.flix.data.remote.tmdb.entity.*
import dev.testify.samples.flix.domain.model.*
import kotlinx.datetime.Instant

interface DataLayerEntityToDomainModelMapper {
    fun map(movie: Movie, urlResolver: TheMovieDbUrlResolver): MovieDomainModel?
    fun map(movieDetails: MovieDetail, urlResolver: TheMovieDbUrlResolver): MovieDomainModel?
    fun map(movieReleaseDates: MovieReleaseDates): MovieReleaseDatesDomainModel?
    fun map(movieCredits: MovieCredits, urlResolver: TheMovieDbUrlResolver): MovieCreditsDomainModel
}

class DataLayerEntityToDomainModelMapperImpl : DataLayerEntityToDomainModelMapper {

    override fun map(movie: Movie, urlResolver: TheMovieDbUrlResolver): MovieDomainModel? {
        return runCatching {
            requireNotNull(movie.id)
            requireNotNull(movie.title)
            requireNotNull(movie.overview)
            requireNotNull(movie.releaseDate)
            MovieDomainModel(
                id = movie.id,
                title = movie.title,
                overview = movie.overview,
                releaseDateYear = movie.releaseDateYear(),
                genres = emptyList(),
                posterUrl = movie.resolvePosterUrl(urlResolver),
                backdropUrl = movie.resolveBackdropUrl(urlResolver)
            )
        }.getOrNull()
    }

    override fun map(
        movieDetails: MovieDetail,
        urlResolver: TheMovieDbUrlResolver
    ): MovieDomainModel? {
        return runCatching {
            with(movieDetails) {
                requireNotNull(id)
                requireNotNull(title)
                requireNotNull(overview)
                requireNotNull(releaseDate)
                MovieDomainModel(
                    id = id,
                    title = title,
                    overview = overview,
                    tagline = tagline,
                    runtimeMinutes = runtime,
                    releaseDateYear = releaseDateYear(),
                    genres = genres?.mapNotNull { it.name } ?: emptyList(),
                    posterUrl = resolvePosterUrl(urlResolver),
                    backdropUrl = null
                )
            }
        }.getOrNull()
    }

    private fun Movie.releaseDateYear(): String? {
        return releaseDate?.releaseDateYear()
    }

    private fun MovieDetail.releaseDateYear(): String? {
        return releaseDate?.releaseDateYear()
    }

    private fun String.releaseDateYear(): String? {
        return split("-").firstOrNull()
    }

    override fun map(movieReleaseDates: MovieReleaseDates): MovieReleaseDatesDomainModel? {
        return runCatching {
            movieReleaseDates.releaseDateByCountries
                .filteredValid()
                .toDomainModels()
        }.getOrNull()
        ?.let {
            MovieReleaseDatesDomainModel(it)
        }
    }

    private fun List<ReleaseDateByCountry>.toDomainModels(): Map<String, List<MovieReleaseDateDomainModel>> {
        val countryCodeToReleaseDatesMap = mutableMapOf<String, List<MovieReleaseDateDomainModel>>()
        forEach { releaseDateByCountry ->
            if (releaseDateByCountry.isValid()) {
                requireNotNull(releaseDateByCountry.iso3166_1)
                requireNotNull(releaseDateByCountry.releaseDates)
                countryCodeToReleaseDatesMap[releaseDateByCountry.iso3166_1] = releaseDateByCountry.releaseDates.mapNotNull { releaseDate ->
                    releaseDate.toDomainModel()
                }
            }
        }
        return countryCodeToReleaseDatesMap
    }

    private fun ReleaseDate.toDomainModel(): MovieReleaseDateDomainModel? {
        return runCatching {
            requireNotNull(releaseDate)
            MovieReleaseDateDomainModel(
                iso639_1LanguageCode = iso639_1,
                releaseDate = Instant.parse(releaseDate),
                certification = certification,
                type = type.toMovieReleaseType()
            )
        }.getOrNull()
    }

    private fun List<ReleaseDateByCountry>?.filteredValid(): List<ReleaseDateByCountry> {
        return this?.let { releaseDatesByCountry ->
            releaseDatesByCountry.filter { !it.iso3166_1.isNullOrBlank() || it.releaseDates.isNullOrEmpty() }
        }   ?: emptyList()
    }

    private fun Int?.toMovieReleaseType(): ReleaseType = when(this) {
        1 -> ReleaseType.Premiere
        2 -> ReleaseType.TheatricalLimited
        3 -> ReleaseType.Theatrical
        4 -> ReleaseType.Digital
        5 -> ReleaseType.Physical
        6 -> ReleaseType.TV
        else -> ReleaseType.Unknown
    }

    private fun Movie.resolvePosterUrl(urlResolver: TheMovieDbUrlResolver): String? {
        return posterPath.resolveImageUrl(urlResolver)
    }

    private fun Movie.resolveBackdropUrl(urlResolver: TheMovieDbUrlResolver): String? {
        return backDropPath.resolveImageUrl(urlResolver)
    }

    private fun MovieDetail.resolvePosterUrl(urlResolver: TheMovieDbUrlResolver): String? {
        return posterPath.resolveImageUrl(urlResolver)
    }

    private fun String?.resolveImageUrl(urlResolver: TheMovieDbUrlResolver): String? {
        return this?.takeIf { it.isNotBlank() }?.let { urlResolver.resolveImageUrl(it) }
    }

    override fun map(movieCredits: MovieCredits, urlResolver: TheMovieDbUrlResolver): MovieCreditsDomainModel {
        return MovieCreditsDomainModel(
            cast = movieCredits.cast?.mapNotNull { it.toDomainModel(urlResolver) } ?: emptyList()
        )
    }

    private fun CastMember.toDomainModel(urlResolver: TheMovieDbUrlResolver): PersonDomainModel? {
        return runCatching {
            requireNotNull(id)
            requireNotNull(name)
            requireNotNull(character)
            PersonDomainModel(
                id = id,
                name = name,
                originalName = originalName ?: "",
                characterName = character,
                popularity = popularity,
                imageUrl = this.profilePath.resolveImageUrl(urlResolver)
            )
        }.getOrNull()
    }
}
