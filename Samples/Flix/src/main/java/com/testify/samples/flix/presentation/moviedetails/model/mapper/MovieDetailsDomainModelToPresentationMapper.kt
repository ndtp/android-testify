package com.andrewcarmichael.flix.presentation.moviedetails.model.mapper

import android.content.Context
import com.andrewcarmichael.flix.R
import com.andrewcarmichael.flix.domain.model.MovieCreditsDomainModel
import com.andrewcarmichael.flix.domain.model.MovieDetailsDomainModel
import com.andrewcarmichael.flix.domain.model.MovieDomainModel
import com.andrewcarmichael.flix.presentation.moviedetails.model.CreditPresentationModel
import com.andrewcarmichael.flix.presentation.moviedetails.model.MovieDetailsPresentationModel
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
                    imagePath = personDomainModel.imageUrl
                )
            }
        } ?: emptyList()
    }
}