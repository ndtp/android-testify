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
package dev.testify.output

import android.app.Instrumentation
import android.content.Context
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.internal.formatDeviceString
import dev.testify.saveBitmapToDestination
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class DestinationTest {

    @RelaxedMockK
    lateinit var mockContext: Context

    @RelaxedMockK
    lateinit var mockArguments: Bundle

    @RelaxedMockK
    lateinit var mockInstrumentation: Instrumentation

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        mockkStatic(InstrumentationRegistry::class)
        mockkStatic(::formatDeviceString)

        every { InstrumentationRegistry.getArguments() } returns mockArguments
        every { InstrumentationRegistry.getInstrumentation() } returns mockInstrumentation
        every { mockInstrumentation.context } returns mockContext

        every { mockContext.getExternalFilesDir(null) } answers {
            File("/storage/emulated/0/Android/data/dev.testify.sample/files")
        }

        val getDirSlot = slot<String>()
        every { mockContext.getDir(capture(getDirSlot), Context.MODE_PRIVATE) } answers {
            File("/data/user/0/dev.testify.sample/app_images/" + getDirSlot.captured)
        }

        every { formatDeviceString(any(), any()) } returns "33-1080x2200@420dp-en_CA"
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `WHEN default THEN data directory`() {
        val destination = getDestination(
            mockContext,
            "fileName",
            extension = ".ext",
            customKey = null,
            root = "root"
        )
        assertEquals(
            "/data/user/0/dev.testify.sample/app_images/root/screenshots/33-1080x2200@420dp-en_CA/fileName.ext",
            destination.description
        )
    }

    @Test
    fun `WHEN custom key THEN data directory with custom key`() {
        val destination = getDestination(
            mockContext,
            "fileName",
            extension = ".ext",
            customKey = "custom_key",
            root = "root"
        )
        assertEquals(
            "/data/user/0/dev.testify.sample/app_images/root/custom_key/fileName.ext",
            destination.description
        )
    }

    @Test
    fun `WHEN sdcard THEN sdcard directory`() {
        every { mockArguments.getString("useSdCard") } returns "true"

        val destination = getDestination(
            mockContext,
            "fileName",
            extension = ".ext",
            customKey = null,
            root = "root"
        )
        assertEquals(
            "/storage/emulated/0/Android/data/dev.testify.sample/files/root/33-1080x2200@420dp-en_CA/fileName.ext",
            destination.description
        )
    }

    @Test(expected = TestStorageNotFoundException::class)
    fun `WHEN test storage is not configured THEN throw TestStorageNotFoundException`() {
        every { mockArguments.getString("useTestStorage") } returns "true"
        val destination = TestStorageDestination(
            mockContext,
            "fileName",
            extension = ".ext",
            key = null,
            root = "root"
        )
        saveBitmapToDestination(mockk(), mockk(), destination)
    }

    @Test
    fun `WHEN testStorage THEN data directory`() {
        every { mockArguments.getString("useTestStorage") } returns "true"

        val destination = getDestination(
            mockContext,
            "fileName",
            extension = ".ext",
            customKey = null,
            root = "root"
        )
        assertTrue("destination is $destination", destination is TestStorageDestination)
        assertEquals(
            "/data/user/0/dev.testify.sample/app_images/root/screenshots/33-1080x2200@420dp-en_CA/fileName.ext",
            destination.description
        )
    }
}
