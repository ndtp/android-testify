package com.andrewcarmichael.flix.presentation.common.model.mapper

import com.andrewcarmichael.flix.domain.model.MovieDomainModel
import com.andrewcarmichael.flix.presentation.common.model.MoviePresentationModel

interface MovieDomainModelToPresentationMapper {
    fun convert(domainModel: MovieDomainModel): MoviePresentationModel
}

class MovieDomainModelToPresentationMapperImpl : MovieDomainModelToPresentationMapper {
    override fun convert(domainModel: MovieDomainModel): MoviePresentationModel {
        return MoviePresentationModel(
            id = domainModel.id,
            title = domainModel.title.takeIf { it.isNotBlank() } ?: "",
            overview = domainModel.overview,
            releaseDateYear = domainModel.releaseDateYear,
            genre = null,
            posterUrl = domainModel.posterUrl,
            backdropUrl = domainModel.backdropUrl
        )
    }
}
