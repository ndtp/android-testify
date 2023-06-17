package dev.testify.internal.helpers

import android.os.Looper

fun isRunningOnUiThread(): Boolean {
    return Looper.getMainLooper().thread == Thread.currentThread()
}
