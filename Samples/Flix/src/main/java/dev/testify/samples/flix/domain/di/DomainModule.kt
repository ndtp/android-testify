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

package dev.testify.samples.flix.domain.di

import dev.testify.samples.flix.domain.model.mapper.DataLayerEntityToDomainModelMapper
import dev.testify.samples.flix.domain.model.mapper.DataLayerEntityToDomainModelMapperImpl
import dev.testify.samples.flix.domain.repository.MovieRepository
import dev.testify.samples.flix.domain.repository.MovieRepositoryImpl
import dev.testify.samples.flix.domain.usecase.LoadHomeScreenUseCase
import dev.testify.samples.flix.domain.usecase.LoadHomeScreenUseCaseImpl
import dev.testify.samples.flix.domain.usecase.LoadMovieDetailsUseCase
import dev.testify.samples.flix.domain.usecase.LoadMovieDetailsUseCaseImpl
import org.koin.dsl.module

val domainModule = module {
    factory<DataLayerEntityToDomainModelMapper> { DataLayerEntityToDomainModelMapperImpl() }
    factory<MovieRepository> { MovieRepositoryImpl(get(), get(), get()) }
    factory<LoadHomeScreenUseCase> { LoadHomeScreenUseCaseImpl(get()) }
    factory<LoadMovieDetailsUseCase> { LoadMovieDetailsUseCaseImpl(get()) }
}
