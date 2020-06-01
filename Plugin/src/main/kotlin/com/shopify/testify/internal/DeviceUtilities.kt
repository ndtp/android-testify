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

package com.shopify.testify.internal

import com.shopify.testify.testifySettings
import java.io.File
import org.gradle.api.Project

internal val Project.deviceImageDirectory: String
    get() {
        return if (this.testifySettings.useSdCard)
            "/sdcard/testify_images/"
        else
            "/data/data/${this.testifySettings.targetPackageId}/app_images/"
    }

internal val Project.screenshotDirectory: String
    get() = "${this.deviceImageDirectory}$SCREENSHOT_DIR"

private fun Adb.listFiles(path: String): List<String> {
    val log = this
        .shell()
        .argument("ls $path")
        .argument("2>/dev/null")
        .execute()

    return log.lines().filter { it.isNotEmpty() }.map { "$path/$it" }
}

internal fun Project.listFailedScreenshotsWithPath(): List<String> {
    val src = screenshotDirectory

    val rootDir = Adb().listFiles(src)
    val files = rootDir.flatMap { Adb().listFiles(it) }

    if (this.isVerbose) {
        files.forEach { println(AnsiFormat.Purple, "\t$it") }
    }
    return files
}

internal fun Project.listFailedScreenshots(): List<String> {
    val src = screenshotDirectory
    val dst = destinationImageDirectory

    val files = this.listFailedScreenshotsWithPath()
    return files.map { it.replace(src, dst) }
}

internal fun File.deleteOnDevice() {
    Adb()
        .shell()
        .argument("rm")
        .argument(this.path)
        .stream(true)
        .execute()
}

const val SCREENSHOT_DIR = "screenshots"
