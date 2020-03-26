package com.shopify.testify.tasks.utility

import com.shopify.testify.internal.Device
import com.shopify.testify.tasks.internal.TaskNameProvider
import com.shopify.testify.tasks.internal.TestifyUtilityTask

open class SetLocaleTask : TestifyUtilityTask() {

    override fun getDescription() = "Sets the locale on the device. Specify the locale as a [language[_territory]]. e.g. setDeviceLocale -Plocale=en_CA for English (Canada)"

    override fun taskAction() {
        val requestedLocale = project.properties["locale"]?.toString() ?: "en_US"
        println("  Requested Locale     = $requestedLocale")
        Device.locale = requestedLocale
    }

    companion object : TaskNameProvider {
        override fun taskName() = "setDeviceLocale"
    }
}
