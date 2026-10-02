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
 * Resolve an install task that `screenshotTest` and `screenshotRecord` should depend on.
 *
 * The task belongs to this project. [inferredInstallTask] and [inferredAndroidTestInstallTask] pick
 * the name out of `project.tasks.names`, so a simple name is looked up with `findByName` — which
 * resolves identically in a top-level module, a module nested inside another directory and a module
 * inside an included build.
 *
 * This is deliberately independent of `moduleName`. Building `":${'$'}moduleName:${'$'}taskName"` and
 * resolving it with `findByPath` meant the lookup depended on a setting that defaults to
 * `project.name`, which is only the last segment of a nested module's path — so for `:feature:login`
 * it searched `:login:installDebugAndroidTest`, found nothing, and silently dropped the dependency.
 * That is [#238](https://github.com/ndtp/android-testify/issues/238), and resolving the task where it
 * actually lives fixes it rather than reporting it.
 *
 * Returns `null` when [taskName] is `null`, which is the legitimate case for a module that has no
 * such task — an Android library has no `installDebug`, and a `com.android.test` module has no
 * `installDebugAndroidTest` because its own APK carries the tests.
 *
 * The one remaining way to have a name that does not resolve is an explicitly configured
 * [settingName] naming a task that does not exist, which is a misconfiguration worth failing on.
 */
private fun Project.findInstallTask(taskName: String?, settingName: String): Task? {
    if (taskName == null) return null

    // A configured value may be a full task path rather than a name in this project.
    val task = if (taskName.contains(':')) tasks.findByPath(taskName) else tasks.findByName(taskName)

    return task ?: throw GradleExtensionException(
        """
        |Testify could not find the task `$taskName`, configured as `$settingName`.
        |
        |Set it to a task that exists in this project, or remove it and let Testify infer the task.
        |`./gradlew $path:tasks --all` lists the tasks available here.
        """.trimMargin()
    )
}
