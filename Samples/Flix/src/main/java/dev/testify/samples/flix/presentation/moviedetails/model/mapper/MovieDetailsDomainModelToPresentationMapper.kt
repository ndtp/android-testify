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

package dev.testify.samples.flix.presentation.moviedetails.model.mapper

import android.content.Context
import dev.testify.samples.flix.R
import dev.testify.samples.flix.domain.model.MovieCreditsDomainModel
import dev.testify.samples.flix.domain.model.MovieDetailsDomainModel
import dev.testify.samples.flix.domain.model.MovieDomainModel
import dev.testify.samples.flix.presentation.moviedetails.model.CreditPresentationModel
import dev.testify.samples.flix.presentation.moviedetails.model.MovieDetailsPresentationModel
import dev.testify.samples.flix.ui.common.util.ImagePromise
import dev.testify.samples.flix.ui.common.util.imagePromise
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface MovieDetailsDomainModelToPresentationMapper {
    fun map(domainModel: MovieDetailsDomainModel): MovieDetailsPresentationModel
}

class MovieDetailsDomainModelToPresentationMapperImpl(
    private val context: Context
) :
    MovieDetailsDomainModelToPresentationMapper {
    override fun map(domainModel: MovieDetailsDomainModel): MovieDetailsPresentationModel {
        return with(domainModel.movie) {
            MovieDetailsPresentationModel(
                id = id,
                title = title(),
                overview = overview,
                tagline = tagline,
                runtime = runtimeMinutes.toRuntimeString(),
                releaseDateYear = releaseDateYear,
                releaseDate = releaseDate?.toLocalDateTime(TimeZone.currentSystemDefault())?.date.toString(),
                posterPath = posterUrl,
                genres = genres,
                certification = certification,
                credits = domainModel.credits.toPresentation()
            )
        }
    }

    private fun MovieDomainModel.title(): String {
        return releaseDateYear?.let {
            context.getString(R.string.movie_details_title_format_template, title, it)
        } ?: title
    }

    private fun Int?.toRuntimeString(): String? {
        return this?.let {
            val hours = it / 60
            val minutes = it % 60
            context.getString(R.string.movie_details_runtime_format_template, hours, minutes)
        }
    }

    private fun MovieCreditsDomainModel?.toPresentation(): List<CreditPresentationModel> {
        return this?.let {
            it.cast.map { personDomainModel ->
                CreditPresentationModel(
                    name = personDomainModel.name,
                    characterName = personDomainModel.characterName,
                    image = personDomainModel.imageUrl.toImagePromise()
                )
            }
        } ?: emptyList()
    }

    private fun String?.toImagePromise(): ImagePromise? {
        return this?.let { imagePromise(it) }
    }
}
