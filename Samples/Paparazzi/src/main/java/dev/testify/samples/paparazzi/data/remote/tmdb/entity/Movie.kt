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
data class Movie(
    @SerialName("poster_path")
    val posterPath: String? = null,

    @SerialName("adult")
    val adult: Boolean? = null,

    @SerialName("overview")
    val overview: String? = null,

    @SerialName("release_date")
    val releaseDate: String? = null,

    @SerialName("genre_ids")
    val genreIdList: List<Int>? = null,

    @SerialName("id")
    val id: Int? = null,

    @SerialName("original_title")
    val originalTitle: String? = null,

    @SerialName("original_language")
    val originalLanguage: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("backdrop_path")
    val backDropPath: String? = null,

    @SerialName("popularity")
    val popularity: Double? = null,

    @SerialName("vote_count")
    val voteCount: Int? = null,

    @SerialName("video")
    val video: Boolean? = null,

    @SerialName("vote_average")
    val voteAverage: Double? = null
)
