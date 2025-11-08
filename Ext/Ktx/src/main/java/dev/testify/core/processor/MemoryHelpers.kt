/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 ndtp
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
package dev.testify.core.processor

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.os.Debug
import androidx.test.platform.app.InstrumentationRegistry

/**
 * Formats a Long size to be in the form of bytes, kilobytes, megabytes, etc.
 */
private fun Long.format() = this.toDouble().format()

@SuppressLint("DefaultLocale")
private fun Double.format() =
    when {
        this < 1024.0 -> "${this.toLong()} B"
        (this >= 1024.0 && this < 1048576.0) -> "${String.format("%.1f", this / 1024.0)} KB"
        (this >= 1048576.0 && this < 1073741824.0) -> "${String.format("%.1f", this / 1048576.0)} MB"
        else -> "${String.format("%.1f", this / 1073741824.0)} GB"
    }

fun formatMemoryState(): String {
    val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
    val instrumentationContext = InstrumentationRegistry.getInstrumentation().context

    return StringBuilder()
        .appendLine("Target Context:")
        .append("- ")
        .appendLine(formatMemoryState(targetContext))
        .appendLine("Instrumentation Context:")
        .append("- ")
        .appendLine(formatMemoryState(instrumentationContext))
        .toString()
}

/**
 * Returns a print-friendly string representation of the current system memory state
 */
private fun formatMemoryState(context: Context): String {
    val result = mutableListOf<String>()

    val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    with(activityManager) {
        // The approximate per-application memory class of the current device.
        result.add("memoryClass: $memoryClass MB")
        // The approximate per-application memory class of the current device when an application is running with a large heap
        result.add("largeMemoryClass: $largeMemoryClass MB")
    }

    val memoryInfo = MemoryInfo().apply {
        activityManager.getMemoryInfo(this)
    }
    with(memoryInfo) {
        // The available memory on the system
        result.add("avail: ${availMem.format()}")
        // The total memory accessible by the kernel. This is basically the RAM size of the device
        result.add("total: ${totalMem.format()}")
        // The threshold of availMem at which we consider memory to be low
        result.add("threshold: ${threshold.format()}")
        // Set to true if the system considers itself to currently be in a low memory situation.
        result.add("isLow: $lowMemory")
    }

    with(Runtime.getRuntime()) {
        // The maximum amount of memory that the virtual machine will attempt to use, measured in bytes
        result.add("heapSize: ${maxMemory().format()}")
        // The total amount of memory currently available for current and future objects, measured in bytes.
        result.add("runtime total: ${totalMemory().format()}")
        // An approximation to the total amount of memory currently available for future allocated objects, measured in bytes
        result.add("free: ${freeMemory().format()}")
        // Used memory = total memory available - free memory
        result.add("used: ${(totalMemory() - freeMemory()).format()}")
    }

    // The size of the native heap in bytes.
    result.add("nativeHeapSize: ${Debug.getNativeHeapSize().format()}")
    // Returns the amount of free memory in the native heap.
    result.add("nativeFree: ${Debug.getNativeHeapFreeSize().format()}")
    // Returns the amount of allocated memory in the native heap.
    result.add("nativeUsed: ${Debug.getNativeHeapAllocatedSize().format()}")

    return result.joinToString(", ")
}
