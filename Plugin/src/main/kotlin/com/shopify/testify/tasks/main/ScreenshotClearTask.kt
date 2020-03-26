/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Shopify Inc.
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
package com.shopify.testify.tasks.main

import com.shopify.testify.internal.AnsiFormat
import com.shopify.testify.internal.deleteOnDevice
import com.shopify.testify.internal.listFailedScreenshotsWithPath
import com.shopify.testify.internal.print
import com.shopify.testify.internal.println
import com.shopify.testify.tasks.internal.TaskNameProvider
import com.shopify.testify.tasks.internal.TestifyDefaultTask
import com.shopify.testify.testifySettings
import java.io.File

open class ScreenshotClearTask : TestifyDefaultTask() {

    override fun getDescription() = "Remove any existing screenshot test images from the device"

    override fun taskAction() {
        val failedScreenshots = project.listFailedScreenshotsWithPath()

        if (failedScreenshots.isEmpty()) {
            println(AnsiFormat.Green, "  No failed screenshots found")
            return
        }

        println("  ${failedScreenshots.size} images to be deleted:")
        failedScreenshots.forEach {
            val file = File(it)
            print(AnsiFormat.Red, "    x ")
            println(AnsiFormat.Red, file.nameWithoutExtension)
            file.deleteOnDevice(project.testifySettings.targetPackageId)
        }
    }

    companion object : TaskNameProvider {
        override fun taskName() = "screenshotClear"
    }
}
