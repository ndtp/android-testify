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

package dev.testify.tasks.utility

import dev.testify.internal.Adb
import dev.testify.internal.Device
import dev.testify.internal.reportFilePath
import dev.testify.internal.screenshotDirectory
import dev.testify.tasks.internal.TaskNameProvider
import dev.testify.tasks.internal.TestifyUtilityTask
import dev.testify.testifySettings

open class SettingsTask : TestifyUtilityTask() {

    override fun getDescription() = "Displays the Testify gradle extension settings"

    override fun taskAction() {
        val userId = Adb.forcedUser?.toString() ?: Device.user
        with(project.testifySettings) {
            println("  baselineSourceDir      = $baselineSourceDir")
            println("  installAndroidTestTask = $installAndroidTestTask")
            println("  installTask            = $installTask")
            println("  moduleName             = $moduleName")
            println("  outputFileNameFormat   = $outputFileNameFormat")
            println("  pullWaitTime           = $pullWaitTime")
            println("  reportFilePath         = ${project.reportFilePath}")
            println("  screenshotAnnotation   = $screenshotAnnotation")
            println("  screenshotDirectory    = ${project.screenshotDirectory}")
            println("  targetPackageId        = $targetPackageId")
            println("  testPackageId          = $testPackageId")
            println("  testRunner             = $testRunner")
            println("  useSdCard              = $useSdCard")
            println("  useTestStorage         = $useTestStorage")
            println("  user                   = $userId")
        }
    }

    companion object : TaskNameProvider {
        override fun taskName() = "testifySettings"
    }
}
