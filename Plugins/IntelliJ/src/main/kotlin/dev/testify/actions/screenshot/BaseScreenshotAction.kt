/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2020 Shopify Inc.
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
package dev.testify.actions.screenshot

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.tools.idea.run.AndroidRunConfiguration
import com.intellij.execution.RunManager
import com.intellij.ide.actions.runAnything.RunAnythingAction
import com.intellij.ide.actions.runAnything.RunAnythingContext
import com.intellij.ide.actions.runAnything.activity.RunAnythingProvider
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import dev.testify.methodName
import dev.testify.moduleName
import dev.testify.testifyClassInvocationPath
import dev.testify.testifyMethodInvocationPath
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.plugins.gradle.action.GradleExecuteTaskAction
import org.jetbrains.plugins.gradle.settings.GradleSettings
import org.jetbrains.plugins.gradle.util.GradleConstants

import com.android.tools.idea.run.editor.AndroidRunConfigurationEditor
import com.intellij.execution.ExecutionManager
import com.intellij.execution.Executor
import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.ActionUpdateThread
import dev.testify.actions.base.BaseTestifyAction
import dev.testify.adb.BridgeImpl
import dev.testify.adb.DeviceResultFetcher
import dev.testify.adb.UseSameDevicesHelper
import dev.testify.preferences.PreferenceAccessorImpl
import dev.testify.preferences.ProjectPreferences

abstract class BaseScreenshotAction(private val anchorElement: PsiElement) : BaseTestifyAction() {

    abstract val classGradleCommand: String

    abstract val methodGradleCommand: String

    abstract val icon: String

    override val isDeviceRequired: Boolean = true

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    protected val methodName: String
        get() {
            var name = anchorElement.methodName
            if (name.length > 18) {
                name = "${name.take(18)}..."
            }
            return name
        }

    protected val className: String?
        get() {
            return if (isClass()) (anchorElement as? KtClass)?.name else null
        }

    private val AnActionEvent.module: Module?
        get() = LangDataKeys.MODULE.getData(this.dataContext)


    /*
    TODO: don't run when sync is in progress

        if (AdbUtil.isGradleSyncInProgress(project)) {
            NotificationHelper.error("Gradle sync is in progress")
            return
        }
     */

    private fun getSelectedDevice(devices: Array<out com.android.ddmlib.IDevice>): com.android.ddmlib.IDevice? {
        // Logic to determine the selected device, for example, based on the device state
        // You might need to adjust this logic based on your requirements

        for (device in devices) {
            if (device.isOnline) {
                // Assuming the selected device is the first online device
                return device
            }
        }

        return null
    }

    /**
     *
     * TODO
     *
     * 1. single select; remove multi-select; remove list; update prompt text
     * 2. do I need the module?
     *   2.a. if yes, can I get it from the test?
     *   2.b. if no, delete this code
     * 3. Does the preferences work? -- how do you reset?
     *   3.a add a menu to view preferences?
     * 4. hook up option. Do I need to offer a direct method? Can I assume the same order?
     *
     */

    private fun selectEmulator(module: Module) {

        val project = module.project

        val projectPreferenceAccessor = PreferenceAccessorImpl(PropertiesComponent.getInstance(project))
        val projectPreferences = ProjectPreferences(projectPreferenceAccessor)
        val bridge = BridgeImpl(project)
        val helper = UseSameDevicesHelper(projectPreferences, bridge)

        val result = DeviceResultFetcher(
            project,
            projectPreferences,
            helper,
            bridge
        ).fetch()

        println("$result")


        val androidFacet = AndroidFacet.getInstance(module)
        println("$androidFacet")



        AndroidDebugBridge.initIfNeeded(false)
        val devices = AndroidDebugBridge.createBridge().devices
        val selectedDevice = getSelectedDevice(devices)

        println("$selectedDevice")

        if (selectedDevice != null) {
            val deviceName = selectedDevice.name
            val deviceSerial = selectedDevice.serialNumber
            println("Selected device: $deviceName ($deviceSerial)")
        } else {
            println("No device selected")
        }

//        (RunManager.getInstance(project).selectedConfiguration as? AndroidRunConfiguration)?.let { androidRunConfiguration ->
//            println("$androidRunConfiguration")
//        }
//        project?.let { project ->
//            val androidFacet = AndroidFacet.getInstance(module)
//            (RunManager.getInstance(project).selectedConfiguration as? AndroidRunConfiguration)?.let { androidRunConfiguration->
////                val emulatorId = androidRunConfiguration.getEmulatorId()
//                println("$androidRunConfiguration $androidFacet")
//            }
//        }
    }


    fun foo(event: AnActionEvent) {
        val project: Project = event.project ?: return

        // Access the currently active run configuration
        val executionManager = ExecutionManager.getInstance(project)

//            val context = executionManager.selectedContext ?: return
//            val runConfiguration = context.runProfile as? RunConfiguration ?: return

        val runConfiguration = RunManager.getInstance(project).selectedConfiguration?.configuration ?: return

        val module = event.module ?: return

        val androidFacet = AndroidFacet.getInstance(module) ?: return

//            // Check if it's an Android run configuration
//            if (isAndroidRunConfiguration(runConfiguration)) {
//                val androidFacet = getAndroidFacet(runConfiguration)
        val selectedDevice = getSelectedDevice(androidFacet)
//
//                if (selectedDevice != null) {
//                    val deviceName = selectedDevice.name
//                    val deviceSerial = selectedDevice.serialNumber
//                    println("Selected device: $deviceName ($deviceSerial)")
//                } else {
//                    println("No device selected")
//                }
//            }
    }

    private fun isAndroidRunConfiguration(runConfiguration: RunConfiguration): Boolean {
        // Customize this check based on your actual run configuration type
        return runConfiguration.type.id.startsWith("AndroidRunConfiguration")
    }

//        private fun getAndroidFacet(runConfiguration: RunConfiguration, module: Module): AndroidFacet? {
//            val configurationModule = runConfiguration
//
//            // Need a "module"
//
//            // com.intellij.openapi.module
//            AndroidFacet.getInstance(module)
//
//            println("$configurationModule")
////            AndroidFacet.getInstance()
////
////            return AndroidFacet.getInstance(configurationModule.module)
//            return null
//        }

    private fun getSelectedDevice(androidFacet: AndroidFacet): IDevice? {

        val mainModule = androidFacet.mainModule ?: return null

        println("$mainModule")
//            AndroidFacet

//            val application = androidFacet.getApplicationFacet() ?: return null
//            val runningDevices = application.lastState?.deployedDevices ?: emptyList()
//
//            // Logic to determine the selected device based on the running devices
//            // You might need to adjust this logic based on your requirements
//
//            return runningDevices.firstOrNull()
        return null
    }


    private fun String.toFullGradleCommand(event: AnActionEvent): String {

//        foo(event)
//
//        event.module?.let { module ->
//            selectEmulator(module)
//        }


        val arguments = when (anchorElement) {
            is KtNamedFunction -> anchorElement.testifyMethodInvocationPath
            is KtClass -> anchorElement.testifyClassInvocationPath
            else -> null
        }
        val command = ":${event.moduleName}:$this"
        return if (arguments != null) "$command -PtestClass=$arguments" else command
    }

    private fun isClass(): Boolean {
        return anchorElement is KtClass
    }

    final override fun actionPerformed(event: AnActionEvent) {
        val project = event.project as Project
        val dataContext = SimpleDataContext.getProjectContext(project)
        val executionContext =
            dataContext.getData(RunAnythingProvider.EXECUTING_CONTEXT) ?: RunAnythingContext.ProjectContext(project)
        val workingDirectory: String = executionContext.getProjectPath() ?: ""
        val executor = RunAnythingAction.EXECUTOR_KEY.getData(dataContext)

        val gradleCommand = if (isClass()) classGradleCommand else methodGradleCommand
        val fullCommandLine = gradleCommand.toFullGradleCommand(event)
        GradleExecuteTaskAction.runGradle(project, executor, workingDirectory, fullCommandLine)
    }

    final override fun update(anActionEvent: AnActionEvent) {
        anActionEvent.presentation.apply {
            text = if (isClass()) classMenuText else methodMenuText
            isEnabledAndVisible = (anActionEvent.project != null)

            isEnabled = this@BaseScreenshotAction.isEnabled

            val classLoader = BaseScreenshotAction::class.java.classLoader
            icon = IconLoader.getIcon("/icons/${this@BaseScreenshotAction.icon}.svg", classLoader)
        }
    }

    @Suppress("UnstableApiUsage")
    private fun RunAnythingContext.getProjectPath() = when (this) {
        is RunAnythingContext.ProjectContext ->
            GradleSettings.getInstance(project).linkedProjectsSettings.firstOrNull()
                ?.let {
                    ExternalSystemApiUtil.findProjectData(
                        project,
                        GradleConstants.SYSTEM_ID,
                        it.externalProjectPath
                    )
                }
                ?.data?.linkedExternalProjectPath

        is RunAnythingContext.ModuleContext -> ExternalSystemApiUtil.getExternalProjectPath(module)
        is RunAnythingContext.RecentDirectoryContext -> path
        is RunAnythingContext.BrowseRecentDirectoryContext -> null
    }
}
