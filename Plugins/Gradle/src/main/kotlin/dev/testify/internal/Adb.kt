/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2026 ndtp
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

package dev.testify.internal

import dev.testify.internal.StreamData.BufferedStream
import dev.testify.internal.Style.Description
import org.gradle.api.GradleException

class Adb(
    private val adbService: AdbService
) {

    private val arguments = ArrayList<String>()
    private var streamData: StreamData? = null

    private val adbPath: String
        get() = adbService.adbPath

    private val deviceTargetIndex: Int
        get() = adbService.deviceTargetIndex

    private val verbose: Boolean
        get() = adbService.verbose

    private val forcedUser: Int?
        get() = adbService.forcedUser

    fun emulator(): Adb {
        arguments.add("-e")
        return this
    }

    fun shell(): Adb {
        arguments.add("shell")
        return this
    }

    fun execOut(): Adb {
        arguments.add("exec-out")
        return this
    }

    fun runAs(packageId: String): Adb {
        if (arguments.isEmpty() ||
            !arguments.last().contentEquals("shell") &&
            !arguments.last().contentEquals("exec-out")
        ) {
            throw GradleException("You must specify 'shell' or 'execOut' before 'runAs'")
        }

        arguments.add("run-as")
        arguments.add(packageId)

        user()

        return this
    }

    private fun user(): Adb {
        if (forcedUser != null) {
            arguments("--user", "$forcedUser")
        } else {
            val user = Device.user(adbService)
            if (user.isNotEmpty() && (user.toIntOrNull() ?: 0) > 0) {
                arguments("--user", user)
            }
        }
        return this
    }

    fun arguments(vararg arg: String): Adb {
        arguments.addAll(arg)
        return this
    }

    fun argument(arg: String): Adb {
        arguments.add(arg)
        return this
    }

    fun stream(streamData: StreamData): Adb {
        this.streamData = streamData
        return this
    }

    fun execute(targetsDevice: Boolean = true): String {
        if (targetsDevice) {
            val deviceTarget = Device.targets(adbService)[deviceTargetIndex]
            if (deviceTarget != null) {
                arguments.add(0, "-s")
                arguments.add(1, deviceTarget)
            }
        }

        val command = (listOf(adbPath) + arguments).joinToString(" ")

        if (verbose) {
            println(Description, command)
        }

        return runProcess(command, streamData ?: BufferedStream())
    }

    fun testOptions(testOptionsBuilder: TestOptionsBuilder): Adb {
        testOptionsBuilder.resolved.forEach {
            arguments.add(TestOptionsBuilder.TEST_OPTIONS_FLAG)
            arguments.add(it)
        }

        return this
    }
}

typealias AdbParam = Pair<String, String>

class TestOptionsBuilder {
    private val params: ArrayList<AdbParam> = ArrayList()

    fun add(key: String, value: String): TestOptionsBuilder {
        params.add(Pair(key, value))
        return this
    }

    fun add(param: AdbParam?): TestOptionsBuilder {
        if (param != null) {
            params.add(param)
        }
        return this
    }

    fun addAll(collection: Collection<AdbParam>): TestOptionsBuilder {
        params.addAll(collection)
        return this
    }

    val resolved: Collection<String>
        get() {
            val set = HashSet<String>()
            params.forEach {
                set.add("${it.first} ${it.second}")
            }
            return set
        }

    companion object {
        const val TEST_OPTIONS_FLAG = "-e"
    }
}
