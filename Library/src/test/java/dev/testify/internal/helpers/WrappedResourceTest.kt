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
package dev.testify.internal.helpers

import android.app.Activity
import android.os.Build
import io.mockk.every
import io.mockk.mockkStatic

/**
 * Simulate the lifecycle applied to WrappedResource on API >= N
 */
fun WrappedResource<*>.test(mockActivity: Activity) {
    mockkStatic(::buildVersionSdkInt)
    every { buildVersionSdkInt() } returns Build.VERSION_CODES.N

    beforeActivityLaunched()
    updateContext(mockActivity)
    afterTestFinished(mockActivity)
}

/**
 * Simulate the lifecycle applied to WrappedResource on API <= N
 */
fun WrappedResource<*>.testMaxSdkVersionM(mockActivity: Activity) {
    mockkStatic(::buildVersionSdkInt)
    every { buildVersionSdkInt() } returns Build.VERSION_CODES.M

    beforeActivityLaunched()
    afterActivityLaunched(mockActivity)
    afterTestFinished(mockActivity)
}
