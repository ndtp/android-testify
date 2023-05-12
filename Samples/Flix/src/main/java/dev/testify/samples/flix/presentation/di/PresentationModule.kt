package dev.testify.samples.flix.presentation.di

import dev.testify.samples.flix.presentation.common.model.mapper.MovieDomainModelToPresentationMapper
import dev.testify.samples.flix.presentation.common.model.mapper.MovieDomainModelToPresentationMapperImpl
import dev.testify.samples.flix.presentation.homescreen.di.homeScreenModule
import dev.testify.samples.flix.presentation.moviedetails.di.movieDetailsModule
import org.koin.dsl.module

val presentationModule = module {
    includes(homeScreenModule)
    includes(movieDetailsModule)
    factory<MovieDomainModelToPresentationMapper> { MovieDomainModelToPresentationMapperImpl() }
}
