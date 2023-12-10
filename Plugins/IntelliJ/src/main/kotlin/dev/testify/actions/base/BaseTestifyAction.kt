package dev.testify.actions.base

import com.intellij.openapi.actionSystem.AnAction
import dev.testify.actions.screenshot.BaseScreenshotAction

abstract class BaseTestifyAction : AnAction() {

    abstract val classMenuText: String

    abstract val methodMenuText: String

    open var menuTextOverride: String? = null

    open var isEnabled: Boolean = true
}

val BaseTestifyAction.isDeviceRequired: Boolean
    get() = (this is BaseScreenshotAction)
