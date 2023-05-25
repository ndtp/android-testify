/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2023 ndtp
 * Original work copyright (c) 2023 Andrew Carmichael
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package dev.testify.samples.flix.application.foundation.secret

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
