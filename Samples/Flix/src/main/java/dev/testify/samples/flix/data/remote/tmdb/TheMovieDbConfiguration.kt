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

package dev.testify.samples.flix.data.remote.tmdb

import dev.testify.samples.flix.data.remote.tmdb.httpclient.TmdbHostnameProvider

interface TheMovieDbApiConfiguration {
    val baseUrl: String

    val thumbnailImageSizeKey: String
    val headlineImageSizeKey: String

    fun isDefaultConfiguration(): Boolean = false
}

private const val ImageSize185 = "w185"
private const val ImageSize342 = "w342"
private const val ImageSize500 = "w500"
private const val ImageSize780 = "w780"
private const val ImageSizeOriginal = "original"

internal class TheMovieDbApiConfigurationImpl(
    override val baseUrl: String,
    posterSizes: List<String>
) : TheMovieDbApiConfiguration {

    override val thumbnailImageSizeKey = with (posterSizes) {
        find { it == ImageSize342 } ?: find { it == ImageSize185 } ?: find { it == ImageSize500 } ?: throw IllegalStateException()
    }

    override val headlineImageSizeKey = with (posterSizes) {
        find { it == ImageSize780 } ?: find { it == ImageSize500 } ?: find { it == ImageSize342 } ?: throw IllegalStateException()
    }
}

internal fun defaultApiConfiguration(hostnameProvider: TmdbHostnameProvider): TheMovieDbApiConfiguration {
    return object : TheMovieDbApiConfiguration {
        override val baseUrl = hostnameProvider.provideHostname()
        override val thumbnailImageSizeKey = ImageSize342
        override val headlineImageSizeKey = ImageSize780
        override fun isDefaultConfiguration() = true
    }
}
