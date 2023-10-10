/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2019 Shopify Inc.
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
package dev.testify

import dev.testify.core.DEFAULT_FOLDER_FORMAT
import dev.testify.core.DEFAULT_NAME_FORMAT
import dev.testify.core.DeviceStringFormatter
import dev.testify.core.formatDeviceString
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class DeviceIdentifierTest {

    private val formatter = mockFormatter()

    private fun mockFormatter(): DeviceStringFormatter {
        val formatter = mockk<DeviceStringFormatter>()

        every { formatter.androidVersion } returns "21"
        every { formatter.deviceDensity } returns "380dp"
        every { formatter.deviceHeight } returns "1024"
        every { formatter.deviceWidth } returns "800"
        every { formatter.locale } returns "en_US"
        every { formatter.testClass } returns "Class"
        every { formatter.getTestName() } returns "method"

        return formatter
    }

    @Test
    fun testFolderNameFormat() {
        assertEquals(
            "21-800x1024@380dp-en_US",
            formatDeviceString(formatter, DEFAULT_FOLDER_FORMAT)
        )
    }

    @Test
    fun testFileNameFormat() {
        assertEquals(
            "Class_method",
            formatDeviceString(formatter, DEFAULT_NAME_FORMAT)
        )
    }

    @Test
    fun testCIFormat() {
        assertEquals(
            "21-800x1024@380dp-en_US#Class_method",
            formatDeviceString(formatter, "a-wxh@d-l#c_n")
        )
    }

    @Test
    fun testCustomFormat() {
        assertEquals(
            "21Class380dp1024en_USmethod800",
            formatDeviceString(formatter, "acdhlnw")
        )
    }
}
