package com.andrewcarmichael.flix.domain.di

import com.andrewcarmichael.flix.domain.model.mapper.DataLayerEntityToDomainModelMapper
import com.andrewcarmichael.flix.domain.model.mapper.DataLayerEntityToDomainModelMapperImpl
import com.andrewcarmichael.flix.domain.repository.MovieRepository
import com.andrewcarmichael.flix.domain.repository.MovieRepositoryImpl
import com.andrewcarmichael.flix.domain.usecase.LoadHomeScreenUseCase
import com.andrewcarmichael.flix.domain.usecase.LoadHomeScreenUseCaseImpl
import com.andrewcarmichael.flix.domain.usecase.LoadMovieDetailsUseCase
import com.andrewcarmichael.flix.domain.usecase.LoadMovieDetailsUseCaseImpl
import org.koin.dsl.module

val domainModule = module {
    factory<DataLayerEntityToDomainModelMapper> { DataLayerEntityToDomainModelMapperImpl() }
    factory<MovieRepository> { MovieRepositoryImpl(get(), get(), get()) }
    factory<LoadHomeScreenUseCase> { LoadHomeScreenUseCaseImpl(get()) }
    factory<LoadMovieDetailsUseCase> { LoadMovieDetailsUseCaseImpl(get()) }
}
