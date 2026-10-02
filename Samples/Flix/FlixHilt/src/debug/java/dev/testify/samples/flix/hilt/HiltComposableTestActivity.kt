/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 ndtp
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
 * This lives in the `debug` source set, with its manifest entry in `src/debug/AndroidManifest.xml`
 * and Testify on the debug compile classpath as `debugCompileOnly`. That is the arrangement an
 * application module needs — a test-only activity has no business in a shipping build — and it is
 * what the [Hilt recipe](https://testify.dev/docs/recipes/hilt) describes, so this sample mirrors
 * the recipe step for step.
 *
 * It is not the only arrangement that works. In a library module the test APK *is* the application,
 * so the activity can equally live in `androidTest` with an `androidTest` manifest and no
 * `compileOnly` at all. `debug` is used here to match the recipe.
 *
 * @see HiltComposableScreenshotTest
 */
@AndroidEntryPoint
class HiltComposableTestActivity : ComposableTestActivity()
