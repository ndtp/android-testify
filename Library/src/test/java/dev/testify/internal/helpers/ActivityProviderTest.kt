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
import android.app.Instrumentation
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class ActivityProviderTest {

    @Test
    fun `WHEN provider is registered THEN return provider`() {
        val provider1 = mockk<ActivityProvider<Activity>>(relaxed = true)
        val provider2 = mockk<ActivityProvider<Activity>>(relaxed = true)
        val instrumentation = mockk<Instrumentation>(relaxed = true)

        instrumentation.registerActivityProvider(provider1)
        assertThat(instrumentation.getActivityProvider<Activity>()).isEqualTo(provider1)
        instrumentation.registerActivityProvider(provider2)
        assertThat(instrumentation.getActivityProvider<Activity>()).isEqualTo(provider2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `WHEN provider is requested from different context THEN throw exception`() {
        val mockActivity1 = mockk<Activity>(relaxed = true)
        val mockActivity2 = mockk<Activity>(relaxed = true)

        val instrumentation = mockk<Instrumentation>(relaxed = true) {
            every { targetContext } returns mockActivity1 andThen mockActivity2
        }
        val provider = mockk<ActivityProvider<Activity>>(relaxed = true)

        instrumentation.registerActivityProvider(provider)
        instrumentation.getActivityProvider<Activity>()
    }
}
