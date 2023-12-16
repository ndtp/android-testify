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
import com.google.common.truth.Truth.assertThat
import dev.testify.core.exception.ActivityMustImplementResourceOverrideException
import dev.testify.core.exception.TestMustWrapContextException
import dev.testify.resources.TestifyResourcesOverride
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.Locale

class ResourceWrapperTest {

    private lateinit var subject: ResourceWrapper
    private val mockActivity = mockk<Activity>(relaxed = true)
    private val mockWrappedResource: WrappedResource<Locale> = mockk(relaxed = true)

    @Before
    fun before() {
        mockkObject(ResourceWrapper)
        mockkStatic(::buildVersionSdkInt)
        every { buildVersionSdkInt() } returns Build.VERSION_CODES.M

        subject = spyk(ResourceWrapper)
        subject.reset()
    }

    @Test
    fun `WHEN object created THEN not wrapped`() {
        assertThat(subject.isWrapped).isFalse()
        assertThat(subject.wrappedResources).isEmpty()
    }

    @Test
    fun `WHEN wrap THEN update context`() {
        subject.addOverride(mockWrappedResource)
        subject.wrap(mockActivity)
        assertThat(subject.isWrapped).isTrue()
        verify { mockWrappedResource.updateContext(mockActivity) }
    }

    @Test
    fun `WHEN reset THEN remove all wrapped resources`() {
        subject.addOverride(mockWrappedResource)
        subject.reset()
        assertThat(subject.wrappedResources).isEmpty()
        assertThat(subject.isWrapped).isFalse()
    }

    @Test
    fun `WHEN activity lifecycle THEN call all wrapped resources`() {
        val mockWrappedResource2: WrappedResource<Float> = mockk(relaxed = true)

        subject.addOverride(mockWrappedResource)
        subject.addOverride(mockWrappedResource2)
        assertThat(subject.wrappedResources).hasSize(2)

        subject.beforeActivityLaunched()
        subject.afterActivityLaunched(mockActivity)

        verify { mockWrappedResource.beforeActivityLaunched() }
        verify { mockWrappedResource2.beforeActivityLaunched() }
        verify { mockWrappedResource.afterActivityLaunched(mockActivity) }
        verify { mockWrappedResource2.afterActivityLaunched(mockActivity) }
    }

    @Test
    fun `WHEN afterTestFinished THEN reset`() {
        subject.addOverride(mockWrappedResource)
        subject.afterTestFinished(mockActivity)
        verify { mockWrappedResource.afterTestFinished(mockActivity) }
        verify { subject.reset() }
        assertThat(subject.isWrapped).isFalse()
    }

    @Test(expected = ActivityMustImplementResourceOverrideException::class)
    fun `WHEN activity does not implement TestifyResourcesOverride THEN throws exception`() {
        every { buildVersionSdkInt() } returns Build.VERSION_CODES.N
        subject.addOverride(mockWrappedResource)
        subject.afterActivityLaunched(mockActivity)
    }

    @Test(expected = TestMustWrapContextException::class)
    fun `WHEN activity is not wrapped THEN throws TestMustWrapContextException`() {
        every { buildVersionSdkInt() } returns Build.VERSION_CODES.N

        val mockTestifyResourcesOverride = mockk<Activity>(
            relaxed = true,
            moreInterfaces = arrayOf(TestifyResourcesOverride::class)
        )

        subject.addOverride(mockWrappedResource)
        subject.afterActivityLaunched(mockTestifyResourcesOverride)
    }

    @Test
    fun `WHEN overrideResourceConfiguration AND no arguments THEN nothing is changed`() {
        overrideResourceConfiguration<Activity>()
        verify(exactly = 0) { ResourceWrapper.addOverride(any()) }
    }

    @Test
    fun `WHEN overrideResourceConfiguration AND locale is set THEN locale is changed`() {
        lateinit var wrappedResource: WrappedResource<*>
        every { ResourceWrapper.addOverride(any()) } answers {
            val value = firstArg<WrappedResource<*>>()
            wrappedResource = spyk(value)
            ResourceWrapper.wrappedResources.add(wrappedResource)
        }

        overrideResourceConfiguration<Activity>(
            locale = Locale("fr_CA")
        )
        verify(exactly = 1) { ResourceWrapper.addOverride(any()) }
        verify { wrappedResource.beforeActivityLaunched() }
    }

    @Test
    fun `WHEN overrideResourceConfiguration AND font scale is set THEN font scale is changed`() {
        lateinit var wrappedResource: WrappedResource<*>
        every { ResourceWrapper.addOverride(any()) } answers {
            val value = firstArg<WrappedResource<*>>()
            wrappedResource = spyk(value)
            ResourceWrapper.wrappedResources.add(wrappedResource)
        }

        overrideResourceConfiguration<Activity>(
            fontScale = 1.5f
        )
        verify(exactly = 1) { ResourceWrapper.addOverride(any()) }
        verify { wrappedResource.beforeActivityLaunched() }
    }

    @Test
    fun `WHEN overrideResourceConfiguration AND locale is set AND font scale is set THEN both are changed`() {
        val wrappedResources = mutableSetOf<WrappedResource<*>>()
        every { ResourceWrapper.addOverride(any()) } answers {
            val value: WrappedResource<*> = spyk(firstArg<WrappedResource<*>>())
            wrappedResources.add(value)
            ResourceWrapper.wrappedResources.add(value)
        }

        overrideResourceConfiguration<Activity>(
            locale = Locale("fr_CA"),
            fontScale = 1.5f
        )
        verify(exactly = 2) { ResourceWrapper.addOverride(any()) }
        verify { wrappedResources.first().beforeActivityLaunched() }
        verify { wrappedResources.last().beforeActivityLaunched() }
    }
}
