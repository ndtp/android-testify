package dev.testify.samples.flix.application.di

import dev.testify.samples.flix.application.foundation.secret.secretModule
import dev.testify.samples.flix.application.navigation.di.navigationModule
import dev.testify.samples.flix.data.di.dataModule
import dev.testify.samples.flix.domain.di.domainModule
import dev.testify.samples.flix.presentation.di.presentationModule
import org.koin.dsl.module

val applicationModule = module {
    includes(secretModule)
    includes(dataModule)
    includes(domainModule)
    includes(presentationModule)
    includes(navigationModule)
}
