/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2019 Shopify Inc.
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

package dev.testify.tasks.utility

import dev.testify.internal.Device
import dev.testify.tasks.internal.TaskNameProvider
import dev.testify.tasks.internal.TestifyUtilityTask

open class DevicesTask : TestifyUtilityTask() {

    override val isDeviceRequired = false

    override fun getDescription() = "Displays Testify devices"

    override fun taskAction() {
        val devices = Device.targets
        println("  Connected devices    = ${devices.size}")
        println(divider)
        if (devices.isEmpty()) {
            println("  No devices connected")
        } else {
            devices.forEach { (index, deviceName) ->
                println("  -Pdevice=$index           = $deviceName")
            }
            println()
            println("  Add -Pdevice=N to any command to target a specific device")
        }
    }

    companion object : TaskNameProvider {
        override fun taskName() = "testifyDevices"
    }
}
