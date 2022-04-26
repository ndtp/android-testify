package dev.testify.internal.output

import android.app.Instrumentation
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry

fun instrumentationPrintln(str: String) {
    val b = Bundle()
    b.putString(Instrumentation.REPORT_KEY_STREAMRESULT, "\n" + str)
    InstrumentationRegistry.getInstrumentation().sendStatus(0, b)
}
