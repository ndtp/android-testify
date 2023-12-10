package dev.testify.adb

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice

@Suppress("DEPRECATION")
class DeviceList {

    private val devices: List<IDevice>? by lazy {
        init()
    }

    private fun init(): List<IDevice>? {
        AndroidDebugBridge.initIfNeeded(false)
        val devices: Array<IDevice?>? = AndroidDebugBridge.createBridge()?.devices
        println("$devices")
        return devices?.filterNotNull()
    }

    fun hasMultipleDevices(): Boolean {
        return (devices?.size ?: 0) > 1
    }

    fun isEmpty(): Boolean =
        devices?.isEmpty() ?: true

    fun list() : List<IDevice> {
        return devices ?: emptyList()
    }

}
