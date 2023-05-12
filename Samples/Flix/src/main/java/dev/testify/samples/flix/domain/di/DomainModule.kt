package dev.testify.samples.flix.domain.di

import dev.testify.samples.flix.domain.model.mapper.DataLayerEntityToDomainModelMapper
import dev.testify.samples.flix.domain.model.mapper.DataLayerEntityToDomainModelMapperImpl
import dev.testify.samples.flix.domain.repository.MovieRepository
import dev.testify.samples.flix.domain.repository.MovieRepositoryImpl
import dev.testify.samples.flix.domain.usecase.LoadHomeScreenUseCase
import dev.testify.samples.flix.domain.usecase.LoadHomeScreenUseCaseImpl
import dev.testify.samples.flix.domain.usecase.LoadMovieDetailsUseCase
import dev.testify.samples.flix.domain.usecase.LoadMovieDetailsUseCaseImpl
import org.koin.dsl.module

val domainModule = module {
    factory<DataLayerEntityToDomainModelMapper> { DataLayerEntityToDomainModelMapperImpl() }
    factory<MovieRepository> { MovieRepositoryImpl(get(), get(), get()) }
    factory<LoadHomeScreenUseCase> { LoadHomeScreenUseCaseImpl(get()) }
    factory<LoadMovieDetailsUseCase> { LoadMovieDetailsUseCaseImpl(get()) }
}
