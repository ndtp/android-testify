package dev.testify.samples.flix.presentation.homescreen.di

import dev.testify.samples.flix.presentation.homescreen.model.mapper.HomeScreenDomainModelToPresentationMapper
import dev.testify.samples.flix.presentation.homescreen.model.mapper.HomeScreenDomainModelToPresentationMapperImpl
import dev.testify.samples.flix.presentation.homescreen.viewmodel.HomeScreenViewModel
import dev.testify.samples.flix.presentation.moviedetails.model.mapper.MovieDetailsDomainModelToPresentationMapper
import dev.testify.samples.flix.presentation.moviedetails.model.mapper.MovieDetailsDomainModelToPresentationMapperImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeScreenModule = module {
    factory<HomeScreenDomainModelToPresentationMapper> { HomeScreenDomainModelToPresentationMapperImpl(get()) }
    factory<MovieDetailsDomainModelToPresentationMapper> { MovieDetailsDomainModelToPresentationMapperImpl(get()) }
    viewModel { HomeScreenViewModel(get(), get()) }
}
