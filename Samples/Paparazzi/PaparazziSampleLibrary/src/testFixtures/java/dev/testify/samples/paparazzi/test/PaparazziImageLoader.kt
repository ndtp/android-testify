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
package dev.testify.samples.paparazzi.test

import android.content.Context
import coil.Coil
import coil.ImageLoader
import kotlinx.coroutines.Dispatchers

/**
 * Install a Coil [ImageLoader] which resolves images synchronously, on the calling thread.
 *
 * Paparazzi composes and draws a single frame inline; it never idles waiting for background work.
 * With Coil's default dispatchers, a request issued by `AsyncImage` is still being fetched and
 * decoded on a worker thread when the snapshot is taken, and the image renders as empty space.
 * [Dispatchers.Unconfined] runs Coil's interceptor, fetch, decode and transform stages inline on
 * the composing thread, so the bitmap is available before Paparazzi draws.
 *
 * This is the Paparazzi counterpart of the instrumented test helper of the same name, which instead
 * routes Coil through an Espresso `IdlingThreadPoolExecutor` so that Testify blocks while images
 * load. Both exist for the same reason — a screenshot must not be captured mid-load — but Paparazzi
 * has no message loop to idle on, so it resolves the work inline instead.
 *
 * [context] should be `Paparazzi.context`, which reads assets from the module's merged asset
 * directories (`src/main/assets`, `src/debug/assets`, plus any library modules). That lets a
 * Paparazzi test reference an image fixture checked into the repository with exactly the same
 * `file:///android_asset/...` URI used by the Testify instrumented tests.
 *
 * [PaparazziTestRule] calls this for you; call it directly only when constructing a bare
 * [app.cash.paparazzi.Paparazzi] rule.
 */
fun setSynchronousImageLoader(context: Context) {
    val imageLoader = ImageLoader.Builder(context).dispatcher(Dispatchers.Unconfined).build()
    Coil.setImageLoader(imageLoader)
}
