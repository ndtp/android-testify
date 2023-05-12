package com.andrewcarmichael.flix.application.di

import com.andrewcarmichael.flix.application.foundation.secret.secretModule
import com.andrewcarmichael.flix.application.navigation.di.navigationModule
import com.andrewcarmichael.flix.data.di.dataModule
import com.andrewcarmichael.flix.domain.di.domainModule
import com.andrewcarmichael.flix.presentation.di.presentationModule
import org.koin.dsl.module

val applicationModule = module {
    includes(secretModule)
    includes(dataModule)
    includes(domainModule)
    includes(presentationModule)
    includes(navigationModule)
}
