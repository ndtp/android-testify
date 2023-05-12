package com.andrewcarmichael.flix.presentation.homescreen.di

import com.andrewcarmichael.flix.presentation.homescreen.model.mapper.HomeScreenDomainModelToPresentationMapper
import com.andrewcarmichael.flix.presentation.homescreen.model.mapper.HomeScreenDomainModelToPresentationMapperImpl
import com.andrewcarmichael.flix.presentation.homescreen.viewmodel.HomeScreenViewModel
import com.andrewcarmichael.flix.presentation.moviedetails.model.mapper.MovieDetailsDomainModelToPresentationMapper
import com.andrewcarmichael.flix.presentation.moviedetails.model.mapper.MovieDetailsDomainModelToPresentationMapperImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeScreenModule = module {
    factory<HomeScreenDomainModelToPresentationMapper> { HomeScreenDomainModelToPresentationMapperImpl(get()) }
    factory<MovieDetailsDomainModelToPresentationMapper> { MovieDetailsDomainModelToPresentationMapperImpl(get()) }
    viewModel { HomeScreenViewModel(get(), get()) }
}
