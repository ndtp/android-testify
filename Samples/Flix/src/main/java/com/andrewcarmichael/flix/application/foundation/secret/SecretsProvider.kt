package com.andrewcarmichael.flix.application.foundation.secret

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import org.koin.dsl.module

interface SecretsProvider {
    fun getTheMovieDatabaseApiToken(): String
}

internal val secretModule = module {
    single<SecretsProvider> {

        object : SecretsProvider {

            private val apiToken by lazy {
                val applicationInfo = getApplicationInfo()
                val token = applicationInfo.metaData.getString("com.flix.tmdb.api.key") ?: "fiddlesticks"
                token
            }

            override fun getTheMovieDatabaseApiToken(): String {
                return apiToken
            }

            private fun getApplicationInfo(): ApplicationInfo {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    getApplicationInfoTiramisu()
                else
                    getApplicationInfoPreTiramisu()
            }

            @Suppress("DEPRECATION")
            private fun getApplicationInfoPreTiramisu(): ApplicationInfo {
                val context: Context by inject()
                return context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            }

            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            private fun getApplicationInfoTiramisu(): ApplicationInfo {
                val context: Context by inject()
                return context.packageManager.getApplicationInfo(
                    context.packageName,
                    PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong()))
            }
        }
    }
}