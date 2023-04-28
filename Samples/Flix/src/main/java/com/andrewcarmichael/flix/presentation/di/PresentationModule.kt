package com.andrewcarmichael.flix.presentation.di

import com.andrewcarmichael.flix.presentation.common.model.mapper.MovieDomainModelToPresentationMapper
import com.andrewcarmichael.flix.presentation.common.model.mapper.MovieDomainModelToPresentationMapperImpl
import com.andrewcarmichael.flix.presentation.homescreen.di.homeScreenModule
import com.andrewcarmichael.flix.presentation.moviedetails.di.movieDetailsModule
import org.koin.dsl.module

val presentationModule = module {
    includes(homeScreenModule)
    includes(movieDetailsModule)
    factory<MovieDomainModelToPresentationMapper> { MovieDomainModelToPresentationMapperImpl() }
}