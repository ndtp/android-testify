/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2024 ndtp
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
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

open class SettingsTask : TestifyUtilityTask() {

    override val isDeviceRequired = false

    @get:Input
    lateinit var baselineSourceDir: String

    @get:Input
    lateinit var moduleName: String

    @get:Input
    lateinit var screenshotDirectory: String

    @get:Input
    lateinit var targetPackageId: String

    @get:Input
    lateinit var testPackageId: String

    @get:Input
    lateinit var testRunner: String

    @get:Input
    var isRecordMode: Boolean = false

    @get:Input
    var pullWaitTime: Long = 0L

    @get:Input
    var useSdCard: Boolean = false

    @get:Input
    var useTestStorage: Boolean = false

    @get:Optional
    @get:Input
    var installAndroidTestTask: String? = null

    @get:Optional
    @get:Input
    var installTask: String? = null

    @get:Optional
    @get:Input
    var outputFileNameFormat: String? = null

    @get:Optional
    @get:Input
    var reportFilePath: String? = null

    @get:Optional
    @get:Input
    var screenshotAnnotation: String? = null

    override fun getDescription() = "Displays the Testify gradle extension settings"

    override fun provideInput(project: Project) {
        super.provideInput(project)
        with(project.testifySettings) {
            this@SettingsTask.baselineSourceDir = this.baselineSourceDir
            this@SettingsTask.installAndroidTestTask = this.installAndroidTestTask
            this@SettingsTask.installTask = this.installTask
            this@SettingsTask.isRecordMode = this.isRecordMode
            this@SettingsTask.moduleName = this.moduleName
            this@SettingsTask.outputFileNameFormat = this.outputFileNameFormat
            this@SettingsTask.pullWaitTime = this.pullWaitTime
            this@SettingsTask.screenshotAnnotation = this.screenshotAnnotation
            this@SettingsTask.targetPackageId = this.targetPackageId
            this@SettingsTask.testPackageId = this.testPackageId
            this@SettingsTask.testRunner = this.testRunner
            this@SettingsTask.useSdCard = this.useSdCard
            this@SettingsTask.useTestStorage = this.useTestStorage
        }
        this@SettingsTask.reportFilePath = project.reportFilePath
        this@SettingsTask.screenshotDirectory = project.screenshotDirectory
    }

    override fun taskAction() {
        val userId = Adb.forcedUser?.toString() ?: Device.user.takeUnless { Device.isEmpty } ?: "Device not found"

        println("  baselineSourceDir      = $baselineSourceDir")
        println("  installAndroidTestTask = $installAndroidTestTask")
        println("  installTask            = $installTask")
        println("  isRecordMode           = $isRecordMode")
        println("  moduleName             = $moduleName")
        println("  outputFileNameFormat   = $outputFileNameFormat")
        println("  pullWaitTime           = $pullWaitTime")
        println("  reportFilePath         = $reportFilePath")
        println("  screenshotAnnotation   = $screenshotAnnotation")
        println("  screenshotDirectory    = $screenshotDirectory")
        println("  targetPackageId        = $targetPackageId")
        println("  testPackageId          = $testPackageId")
        println("  testRunner             = $testRunner")
        println("  useSdCard              = $useSdCard")
        println("  useTestStorage         = $useTestStorage")
        println("  user                   = $userId")
    }

    companion object : TaskNameProvider {
        override fun taskName() = "testifySettings"
    }
}
