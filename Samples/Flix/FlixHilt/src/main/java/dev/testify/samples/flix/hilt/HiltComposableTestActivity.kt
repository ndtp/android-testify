/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 ndtp
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

package dev.testify.samples.flix.hilt

import dagger.hilt.android.AndroidEntryPoint
import dev.testify.ComposableTestActivity

/**
 * A Hilt-aware host for composables under test.
 *
 * Hilt injects only into activities annotated with [AndroidEntryPoint]. Testify's
 * [ComposableTestActivity] is not, so a composable that resolves a dependency — through
 * `hiltViewModel()`, for example — fails inside it with:
 *
 * ```
 * java.lang.IllegalStateException: Given component holder class dev.testify.ComposableTestActivity
 * does not implement interface dagger.hilt.internal.GeneratedComponent or interface
 * dagger.hilt.internal.GeneratedComponentManager
 * ```
 *
 * Subclassing it here adds the annotation without changing anything else, so
 * `ComposableScreenshotScenarioRule` can host Hilt-backed composables.
 *
 * This lives in `main` rather than `androidTest` because the activity has to be declared in a
 * merged manifest, which cannot reference test sources. That is safe here because this whole module
 * exists only to host screenshot tests. In an application module, put it in the `debug` source set
 * instead so it is never shipped — see the
 * [Hilt recipe](https://testify.dev/docs/recipes/hilt).
 *
 * @see HiltComposableScreenshotTest
 */
@AndroidEntryPoint
class HiltComposableTestActivity : ComposableTestActivity()
