package dev.testify.samples.flix.application.navigation.di

import dev.testify.samples.flix.application.navigation.NavigationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val navigationModule = module {
    viewModel { NavigationViewModel() }
}
