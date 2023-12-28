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

import dev.testify.TestifyPlugin.Companion.EVALUATED_SETTINGS
import dev.testify.internal.Adb
import dev.testify.internal.android
import dev.testify.tasks.internal.TaskNameProvider
import dev.testify.tasks.internal.TestifyDefaultTask
import dev.testify.tasks.main.ScreenshotClearTask
import dev.testify.tasks.main.ScreenshotPullTask
import dev.testify.tasks.main.ScreenshotRecordTask
import dev.testify.tasks.main.ScreenshotTestTask
import dev.testify.tasks.report.ReportPullTask
import dev.testify.tasks.report.ReportShowTask
import dev.testify.tasks.utility.DeviceKeyTask
import dev.testify.tasks.utility.DevicesTask
import dev.testify.tasks.utility.DisableSoftKeyboardTask
import dev.testify.tasks.utility.HidePasswordsTasks
import dev.testify.tasks.utility.LocaleTask
import dev.testify.tasks.utility.SettingsTask
import dev.testify.tasks.utility.TimeZoneTask
import dev.testify.tasks.utility.VersionTask
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class TestifyPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            extensions.create(TestifyExtension.NAME, TestifyExtension::class.java)
            createTasks()
            afterEvaluate(AfterEvaluate)
        }
    }

    private object AfterEvaluate : Action<Project> {
        override fun execute(project: Project) {
            val settings = TestifySettings.create(project)
            settings.validate()
            project.extensions.add(EVALUATED_SETTINGS, settings)

            project.addManifestPlaceholders(settings)
            project.addDependencies()

            if (settings.autoImplementLibrary) {
                val version = javaClass.getPackage().implementationVersion
                project.dependencies.add("androidTestImplementation", "dev.testify:testify:$version")
            }

            Adb.init(project)
        }

        private fun Project.addManifestPlaceholders(settings: TestifySettings) {
            val destination = when {
                settings.useSdCard -> "sdcard"
                settings.useTestStorage -> "teststorage"
                else -> "default"
            }
            val module = settings.moduleName
            val isRecordMode = settings.isRecordMode.toString()
            android.defaultConfig {
                it.resValue("string", "testifyDestination", destination)
                it.resValue("string", "testifyModule", module)
                it.resValue("string", "isRecordMode", isRecordMode)
            }
        }

        private fun Project.addDependencies() {
            ScreenshotTestTask.setDependencies(ScreenshotTestTask.Companion, project = this)
            ScreenshotRecordTask.setDependencies(ScreenshotRecordTask.Companion, project = this)
        }
    }

    private fun Project.createTasks() {
        tasks.create(DeviceKeyTask.taskName(), DeviceKeyTask::class.java)
        tasks.create(DevicesTask.taskName(), DevicesTask::class.java)
        tasks.create(DisableSoftKeyboardTask.taskName(), DisableSoftKeyboardTask::class.java)
        tasks.create(HidePasswordsTasks.taskName(), HidePasswordsTasks::class.java)
        tasks.create(LocaleTask.taskName(), LocaleTask::class.java)
        tasks.create(ReportPullTask.taskName(), ReportPullTask::class.java)
        registerTask<ReportShowTask>(ReportShowTask.Companion)
        registerTask<ScreenshotClearTask>(ScreenshotClearTask.Companion)
        registerTask<ScreenshotPullTask>(ScreenshotPullTask.Companion)
        tasks.create(ScreenshotRecordTask.taskName(), ScreenshotRecordTask::class.java)
        tasks.create(ScreenshotTestTask.taskName(), ScreenshotTestTask::class.java)
        registerTask<SettingsTask>(SettingsTask.Companion)
        tasks.create(TimeZoneTask.taskName(), TimeZoneTask::class.java)
        tasks.create(VersionTask.taskName(), VersionTask::class.java)
    }

    private inline fun <reified T : TestifyDefaultTask> Project.registerTask(taskNameProvider: TaskNameProvider) {
        tasks.register(taskNameProvider.taskName(), T::class.java) {
            (it as? TestifyDefaultTask)?.provideInput(this)
        }
    }


    companion object {
        const val EVALUATED_SETTINGS = "testify_evaluated_settings"
    }
}

internal val Project.testifySettings: TestifySettings
    get() {
        return this.extensions.getByName(EVALUATED_SETTINGS) as TestifySettings
    }
