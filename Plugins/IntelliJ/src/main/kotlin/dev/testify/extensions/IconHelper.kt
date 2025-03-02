package dev.testify.extensions

import com.intellij.openapi.util.IconLoader

object IconHelper {
    val ICON_CAMERA by lazy { IconLoader.getIcon("/icons/camera.svg", this@IconHelper::class.java) }
}
