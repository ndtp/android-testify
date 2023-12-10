package dev.testify.actions.base

import com.intellij.openapi.actionSystem.AnAction

abstract class BaseTestifyAction : AnAction() {

    abstract val classMenuText: String
    abstract val methodMenuText: String

    open var isEnabled: Boolean = true

    abstract val isDeviceRequired: Boolean
}
