package com.andrewcarmichael.flix.presentation.moviedetails.di

import com.andrewcarmichael.flix.presentation.moviedetails.viewmodel.MovieDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val movieDetailsModule = module {
    viewModel { parameters ->
        MovieDetailsViewModel(
            args = MovieDetailsViewModel.Args(movieId = parameters.get()),
            loadMovieDetailsUseCase = get(),
            movieDetailsPresentationMapper = get()
        )
    }
}
