/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2024 ndtp
 * Original work copyright (c) 2021 Shopify Inc.
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
package dev.testify.core.exception

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The module name returned by `getModuleName()` carries no Gradle path separator, so every message
 * that builds a task path adds its own `:`. A name that already ended in one produced
 * `app::screenshotPull` — a command a user cannot run.
 *
 * @see dev.testify.extensions.getModuleName
 */
class GradleCommandMessageTest {

    private val moduleName = "app"

    private fun assertSingleColon(message: String) {
        assertFalse(
            "message contains a double-colon gradle path:\n$message",
            message.contains("::")
        )
        assertTrue(
            "message does not name a gradle task for the module:\n$message",
            message.contains("./gradlew $moduleName:")
        )
    }

    @Test
    fun `ScreenshotIsDifferentException names a runnable gradle task`() {
        val message = ScreenshotIsDifferentException(moduleName, "MyTest#default").message!!

        assertSingleColon(message)
        assertTrue(message.contains("./gradlew app:screenshotPull"))
        assertTrue(message.contains("./gradlew app:screenshotTest -PtestClass=MyTest#default"))
    }

    @Test
    fun `ScreenshotBaselineNotDefinedException names a runnable gradle task`() {
        val message = ScreenshotBaselineNotDefinedException(
            moduleName = moduleName,
            testName = "default",
            testClass = "MyTest",
            deviceKey = "37-1080x2220@440dp-en_US"
        ).message!!

        assertSingleColon(message)
        assertTrue(message.contains("./gradlew app:screenshotRecord -PtestClass=MyTest"))
    }

    /**
     * A nested module keeps its own separators; only the one the message adds is at issue.
     */
    @Test
    fun `a nested module name is not mistaken for a double colon`() {
        val message = ScreenshotIsDifferentException("feature:ui", "MyTest#default").message!!

        assertFalse(message.contains("::"))
        assertTrue(message.contains("./gradlew feature:ui:screenshotPull"))
    }
}
