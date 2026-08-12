/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 ndtp
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
package dev.testify.samples.paparazzi.data.translator

import dev.testify.samples.paparazzi.data.model.PaparazziPerson
import dev.testify.samples.paparazzi.data.remote.tmdb.TheMovieDbUrlResolver
import dev.testify.samples.paparazzi.data.remote.tmdb.entity.Person
import dev.testify.samples.paparazzi.ui.common.util.toImagePromise

fun Person.toPaparazziPerson(urlResolver: TheMovieDbUrlResolver) = PaparazziPerson(
    id = this.id ?: 0,
    name = this.name,
    placeOfBirth = this.placeOfBirth,
    popularity = this.popularity ?: 0f,
    image = this.profilePath?.let { urlResolver.resolveImageUrl(this.profilePath).toImagePromise() },
    knownFor = this.knownForDepartment,
    biography = this.biography,
    birthday = this.birthday,
    deathday = this.deathday,
)
