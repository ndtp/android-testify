/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2026 ndtp
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
package dev.testify.tasks.main

import dev.testify.internal.Adb
import dev.testify.internal.AdbParam
import dev.testify.internal.StreamData.ConsoleStream
import dev.testify.internal.Style.Failure
import dev.testify.internal.TestOptionsBuilder
import dev.testify.internal.fromEnv
import dev.testify.internal.println
import dev.testify.GradleExtensionException
import dev.testify.tasks.internal.TaskDependencyProvider
import dev.testify.tasks.internal.TaskNameProvider
import dev.testify.tasks.internal.TestifyDefaultTask
import dev.testify.tasks.utility.DisableSoftKeyboardTask
import dev.testify.tasks.utility.DisableStylusTask
import dev.testify.tasks.utility.HidePasswordsTasks
import dev.testify.tasks.utility.LocaleTask
import dev.testify.tasks.utility.TimeZoneTask
import dev.testify.testifySettings
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

open class ScreenshotTestTask : TestifyDefaultTask() {

    @Optional
    @get:Input
    var moduleName: String? = null

    @Optional
    @get:Input
    var outputFormat: String? = null

    @Optional
    @get:Input
    var shardCount: Int? = null

    @Optional
    @get:Input
    var shardIndex: Int? = null

    @Optional
    @get:Input
    var testClass: String? = null

    @Optional
    @get:Input
    var testName: String? = null

    @get:Input
    lateinit var screenshotAnnotation: String

    @get:Input
    lateinit var testPackageId: String

    @get:Input
    lateinit var testRunner: String

    @get:Input
    var useSdCard: Boolean = false

    override fun getDescription() = "Run the Testify screenshot tests"

    private val testTarget: AdbParam?
        get() {
            return if (!testClass.isNullOrEmpty()) {
                AdbParam("class", "$testClass${if (!testName.isNullOrEmpty()) "#$testName" else ""}")
            } else {
                null
            }
        }

    private val shardParams: Set<AdbParam>
        get() {
            val params = HashSet<AdbParam>()
            if (shardCount != null && shardIndex != null) {
                println("  Running test shard $shardIndex of $shardCount...")
                params.add(AdbParam("numShards", "$shardCount"))
                params.add(AdbParam("shardIndex", "$shardIndex"))
            }
            return params
        }

    @Internal
    protected open fun getRuntimeParams(): List<AdbParam> {
        val params = ArrayList<AdbParam>()

        if (useSdCard) {
            params.add(AdbParam("useSdCard", "true"))
        }

        outputFormat.letNotEmpty {
            params.add(AdbParam("outputFileNameFormat", it))
        }

        moduleName.letNotEmpty {
            params.add(AdbParam("moduleName", it))
        }

        return params
    }

    private fun String?.notEmptyOrDefault(default: String): String =
        if (this?.isNotEmpty() == true) this else default

    private fun String?.letNotEmpty(block: (String) -> Unit) {
        if (this?.isNotEmpty() == true) block(this)
    }

    private val annotation: AdbParam
        get() = AdbParam("annotation", screenshotAnnotation)

    override fun provideInput(project: Project) {
        super.provideInput(project)

        moduleName = project.testifySettings.moduleName
        outputFormat = "TESTIFY_OUTPUT_FORMAT".fromEnv(project.testifySettings.outputFileNameFormat)
        screenshotAnnotation = project.testifySettings.screenshotAnnotation.notEmptyOrDefault(
            default = "dev.testify.annotation.ScreenshotInstrumentation"
        )
        shardCount = (project.properties["shardCount"] as? String)?.toInt()
        shardIndex = (project.properties["shardIndex"] as? String)?.toInt()
        testClass = project.properties["testClass"] as String?
        testName = project.properties["testName"] as String?
        testPackageId = project.testifySettings.testPackageId
        testRunner = project.testifySettings.testRunner
        useSdCard = "TESTIFY_USE_SDCARD".fromEnv(project.testifySettings.useSdCard)
    }

    override fun taskAction() {
        val testOptions = TestOptionsBuilder()
        testOptions
            .addAll(shardParams)
            .add(testTarget)
            .addAll(getRuntimeParams())
            .add(annotation)

        val log = Adb(adbServiceProvider.get())
            .shell()
            .argument("am")
            .argument("instrument")
            .testOptions(testOptions)
            .argument("-w")
            .argument("$testPackageId/$testRunner")
            .stream(ConsoleStream)
            .execute()
        finalizeTaskAction(log)
    }

    protected open fun finalizeTaskAction(log: String) {
        if (log.contains("FAILURES!!!") ||
            log.contains("INSTRUMENTATION_CODE: 0") ||
            log.contains("Process crashed while executing")
        ) {
            println(Failure, "SCREENSHOT TESTS HAVE FAILED!!!")
            throw RuntimeException("Screenshot tests have failed")
        }
    }

    companion object : TaskNameProvider, TaskDependencyProvider {
        override fun taskName() = "screenshotTest"

        override fun setDependencies(taskNameProvider: TaskNameProvider, project: Project) {
            val task = project.tasks.getByName(taskNameProvider.taskName())
            task.dependsOn(
                HidePasswordsTasks.taskName(),
                DisableSoftKeyboardTask.taskName(),
                DisableStylusTask.taskName(),
                LocaleTask.taskName(),
                TimeZoneTask.taskName()
            )
            getInstallDebugAndroidTestTask(project)?.let { installDebugAndroidTestTask ->
                task.dependsOn(installDebugAndroidTestTask)
            }
            getInstallDebugTask(project)?.let { installDebugTask ->
                task.dependsOn(installDebugTask)
            }
        }
    }
}

internal fun getInstallDebugAndroidTestTask(project: Project): Task? =
    project.findInstallTask(
        taskName = project.testifySettings.installAndroidTestTask,
        settingName = "installAndroidTestTask"
    )

internal fun getInstallDebugTask(project: Project): Task? =
    project.findInstallTask(
        taskName = project.testifySettings.installTask,
        settingName = "installTask"
    )

/**
 * Resolve an install task by its full Gradle path, built from the configured `moduleName`.
 *
 * Returns `null` when [taskName] is `null`, which is the legitimate case for a module that has no
 * install task at all — an Android library, a `com.android.test` module, or a JVM-only module such
 * as the Paparazzi sample. Those modules are expected to have nothing to depend on.
 *
 * When [taskName] is known but the path does not resolve, the configuration is wrong rather than
 * absent, and the task would otherwise be dropped silently — leaving the tests to run against
 * whatever happened to be installed on the device, or failing much later with Gradle's
 * `A dependency must not be empty`. This is the case reported in
 * [#238](https://github.com/ndtp/android-testify/issues/238): under a composite build the inferred
 * `moduleName` of `project.name` is not the module's Gradle path, so the lookup cannot succeed.
 */
private fun Project.findInstallTask(taskName: String?, settingName: String): Task? {
    if (taskName == null) return null

    val moduleName = this.testifySettings.moduleName
    val path = ":$moduleName:$taskName"

    return this.tasks.findByPath(path) ?: throw GradleExtensionException(
        """
        |Testify could not find the `$taskName` task for this project.
        |
        |  Searched for : $path
        |  moduleName   : $moduleName${if (this.testifySettings.moduleName == this.name) " (inferred from the project name)" else ""}
        |  $settingName : $taskName
        |
        |`moduleName` must be the module's Gradle path, without the leading colon. It is inferred
        |from the project name, which is correct for a top-level module but not for a nested one or
        |for a module inside an included build — there, `${this.name}` is only the last segment of
        |the path.
        |
        |Set it explicitly in this module's build file:
        |
        |    testify {
        |        moduleName = "<the path to this module, without the leading colon>"
        |    }
        |
        |Run `./gradlew $moduleName:testifySettings` to print the resolved configuration, and
        |`./gradlew tasks --all` to find the task's real path.
        |
        |See https://testify.dev/docs/recipes/build-types-and-flavors#nested-modules
        """.trimMargin()
    )
}
