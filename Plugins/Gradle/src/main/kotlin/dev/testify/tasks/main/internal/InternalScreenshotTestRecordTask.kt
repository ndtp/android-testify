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
package dev.testify.tasks.main.internal

import dev.testify.internal.AdbParam
import dev.testify.tasks.internal.TaskNameProvider
import dev.testify.tasks.main.ScreenshotTestTask
import org.gradle.api.tasks.Internal

open class InternalScreenshotTestRecordTask : ScreenshotTestTask() {

    override val isHidden = true

    override fun getDescription() = ""

    @Internal
    override fun getRuntimeParams(): List<AdbParam> {
        return super.getRuntimeParams() + AdbParam("isRecordMode", "true")
    }

    override fun finalizeTaskAction(log: String) {
        // Do nothing
    }

    companion object : TaskNameProvider {
        override fun taskName() = "screenshotTestRecord"
    }
}
