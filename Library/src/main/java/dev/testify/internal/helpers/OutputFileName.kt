package dev.testify.internal.helpers

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import dev.testify.TestDescription
import dev.testify.internal.DEFAULT_NAME_FORMAT
import dev.testify.internal.DeviceStringFormatter
import dev.testify.internal.formatDeviceString
import dev.testify.testDescription

fun Context.outputFileName(
    description: TestDescription = InstrumentationRegistry.getInstrumentation().testDescription
) = formatDeviceString(
    DeviceStringFormatter(
        this,
        description.nameComponents
    ),
    DEFAULT_NAME_FORMAT
)
