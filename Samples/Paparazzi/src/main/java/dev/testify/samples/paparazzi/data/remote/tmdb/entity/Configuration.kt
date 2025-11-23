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

package dev.testify.samples.paparazzi.data.remote.tmdb.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    @SerialName("images")
    val imageConfiguration: ImageConfiguration? = null,

    @SerialName("change_keys")
    val changeKeys: List<String>? = null
)

@Serializable
data class ImageConfiguration(
    @SerialName("base_url")
    val baseUrl: String? = null,

    @SerialName("secure_base_url")
    val secureBaseUrl: String? = null,

    @SerialName("backdrop_sizes")
    val backDropSizes: List<String>? = null,

    @SerialName("logo_sizes")
    val logoSizes: List<String>? = null,

    @SerialName("poster_sizes")
    val posterSizes: List<String>? = null,

    @SerialName("profile_sizes")
    val profileSizes: List<String>? = null,

    @SerialName("still_sizes")
    val stillSizes: List<String>? = null
)
