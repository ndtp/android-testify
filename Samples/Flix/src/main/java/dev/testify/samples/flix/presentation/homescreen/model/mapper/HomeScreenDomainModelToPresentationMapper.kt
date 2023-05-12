package com.andrewcarmichael.flix.presentation.homescreen.model.mapper

import com.andrewcarmichael.flix.domain.model.HomeScreenDomainModel
import com.andrewcarmichael.flix.presentation.common.model.mapper.MovieDomainModelToPresentationMapper
import com.andrewcarmichael.flix.presentation.homescreen.model.HomeScreenPresentationModel

interface HomeScreenDomainModelToPresentationMapper {
    fun convert(domainModel: HomeScreenDomainModel): HomeScreenPresentationModel
}

class HomeScreenDomainModelToPresentationMapperImpl(
    private val movieMapper: MovieDomainModelToPresentationMapper
) : HomeScreenDomainModelToPresentationMapper {
    override fun convert(domainModel: HomeScreenDomainModel): HomeScreenPresentationModel {
        return HomeScreenPresentationModel(
            headliningMovie = domainModel.headlineTrendingMovie?.let { movieMapper.convert(it) },
            moviesNowPlaying = domainModel.moviesNowPlaying.map { movieMapper.convert(it) },
            upcomingMovies = domainModel.upcomingMovies.map { movieMapper.convert(it) }
        )
    }
}
