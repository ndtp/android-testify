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

import com.shopify.testify.tasks.internal.TaskNameProvider
import com.shopify.testify.tasks.internal.TestifyUtilityTask
import com.shopify.testify.testifySettings

open class SettingsTask : TestifyUtilityTask() {

    override fun getDescription() = "Displays the Testify gradle extension settings"

    override fun taskAction() {
        with(project.testifySettings) {
            println("  baselineSourceDir      = $baselineSourceDir")
            println("  installAndroidTestTask = $installAndroidTestTask")
            println("  installTask            = $installTask")
            println("  moduleName             = $moduleName")
            println("  outputFileNameFormat   = $outputFileNameFormat")
            println("  pullWaitTime           = $pullWaitTime")
            println("  targetPackageId        = $targetPackageId")
            println("  testPackageId          = $testPackageId")
            println("  testRunner             = $testRunner")
            println("  useSdCard              = $useSdCard")
        }
    }

    companion object : TaskNameProvider {
        override fun taskName() = "testifySettings"
    }
}
