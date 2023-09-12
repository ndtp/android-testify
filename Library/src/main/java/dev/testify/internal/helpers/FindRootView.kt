package dev.testify.internal.helpers

import android.app.Activity
import android.view.ViewGroup
import androidx.annotation.IdRes
import dev.testify.internal.exception.RootViewNotFoundException

fun Activity.findRootView(@IdRes rootViewId: Int): ViewGroup =
    this.findViewById(rootViewId) ?: throw RootViewNotFoundException(this, rootViewId)
