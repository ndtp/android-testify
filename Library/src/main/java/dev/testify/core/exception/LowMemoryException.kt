/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 ndtp
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

import android.content.Context
import dev.testify.core.DEFAULT_FOLDER_FORMAT
import dev.testify.core.DeviceStringFormatter
import dev.testify.core.formatDeviceString

/**
 * An exception thrown when the current device does not have sufficient free memory to perform ParallelPixelProcessing.
 *
 * Parallel pixel processing requires the allocation of two byte buffers. For a typical device resolution of
 * 1080 x 1920, two buffers of approximately 2 MB each (4 MB total) are allocated. Most AVDs are configured with a heap
 * size of 25% their available RAM. For example, 512 MB for a device with 2 GB of RAM.
 * See: https://android.googlesource.com/platform/external/qemu/+/emu-master-dev/android/android-emu/android/main-common.c#1292
 *
 * This is normally more than sufficient for parallel pixel processing. However, in some instances it has been observed
 * where the heap size for androidTest instrumentation runners is set to only 16 MB, which is not sufficient for
 * Testify.
 *
 * If you encounter this exception, please verify that your emulator is correctly configured with at least 2 GB of RAM.
 *
 * @param targetContext - device context
 * @param requestedAllocation - number of bytes that failed to be allocated
 * @param memoryInfo - a string log of the device's current memory state.
 * @param cause - The OutOfMemoryError exception originally thrown
 */
class LowMemoryException(
    targetContext: Context,
    requestedAllocation: Int,
    memoryInfo: String,
    cause: OutOfMemoryError
) : TestifyException(
    tag = "LOW_MEMORY",
    message = """

    Unable to allocate $requestedAllocation bytes for parallel pixel processing.
    The current device ${formatDeviceString(DeviceStringFormatter(targetContext, null), DEFAULT_FOLDER_FORMAT)} does not have sufficient RAM to perform complex diff calculations.

    Please ensure that the emulator is configured with a minimum of 2 GB of RAM.

    Alternatively, disable the use of custom or fuzzy comparison methods:
     - Use a TestifyConfiguration.exactness value of null or 0.0
     - Set TestifyConfiguration.compareMethod to null
     - Do not set any exclusionRects on the TestifyConfiguration
     - Do not enable HighContrastDiff

    Device Memory Info:
    $memoryInfo

    Caused by ${cause::class.simpleName}:
    ${cause.message}
    ${cause.printStackTrace()}
    """.trimIndent()
)
