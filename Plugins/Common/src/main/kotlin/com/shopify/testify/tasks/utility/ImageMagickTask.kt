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

package com.shopify.testify.tasks.utility

import com.shopify.testify.internal.ImageMagick
import com.shopify.testify.tasks.internal.TaskNameProvider
import com.shopify.testify.tasks.internal.TestifyUtilityTask

open class ImageMagickTask : TestifyUtilityTask() {

    override fun getDescription() = "Verify that the ImageMagick tools required for diff images are installed"

    override fun taskAction() {
        val isInstalled = ImageMagick.isInstalled
        println("  Compare               = $isInstalled")

        if (!isInstalled) {
            throw Exception("Required ImageMagick tool 'compare' not found")
        }
    }

    companion object : TaskNameProvider {
        override fun taskName() = "verifyImageMagick"
    }
}
