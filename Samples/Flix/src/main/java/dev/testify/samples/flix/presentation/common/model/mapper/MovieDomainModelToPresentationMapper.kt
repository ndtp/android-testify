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

package dev.testify.samples.flix.presentation.common.model.mapper

import dev.testify.samples.flix.domain.model.MovieDomainModel
import dev.testify.samples.flix.presentation.common.model.MoviePresentationModel

interface MovieDomainModelToPresentationMapper {
    fun convert(domainModel: MovieDomainModel): MoviePresentationModel
}

class MovieDomainModelToPresentationMapperImpl : MovieDomainModelToPresentationMapper {
    override fun convert(domainModel: MovieDomainModel): MoviePresentationModel {
        return MoviePresentationModel(
            id = domainModel.id,
            title = domainModel.title.takeIf { it.isNotBlank() } ?: "",
            overview = domainModel.overview,
            releaseDateYear = domainModel.releaseDateYear,
            genre = null,
            posterUrl = domainModel.posterUrl,
            backdropUrl = domainModel.backdropUrl
        )
    }
}
