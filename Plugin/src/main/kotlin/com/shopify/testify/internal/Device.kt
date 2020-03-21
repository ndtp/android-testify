/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Shopify Inc.
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

package com.shopify.testify.internal

object Device {

    private val version: Int
        get() {
            val result = Adb()
                .shell()
                .arguments(
                    "getprop",
                    "ro.build.version.sdk")
                .execute()
            return result.trim().toInt()
        }

    private var language: String
        get() = Adb()
                .shell()
                .argument("getprop")
                .apply {
                    if (version == 22) {
                        argument("ro.product.locale.language")
                    } else {
                        argument("persist.sys.language")
                    }
                }.execute().trim()
        set(value) {
            Adb()
                    .shell()
                    .argument("setprop")
                    .apply {
                        if (version == 22) {
                            argument("ro.product.locale.language")
                        } else {
                            argument("persist.sys.language")
                        }
                    }
                    .argument(value)
                    .execute()
        }

    private var region: String
        get() = Adb()
                .shell()
                .argument("getprop")
                .apply {
                    if (version == 22) {
                        argument("ro.product.locale.region")
                    } else {
                        argument("persist.sys.country")
                    }
                }.execute().trim()
        set(value) {
            Adb()
                    .shell()
                    .argument("setprop")
                    .apply {
                        if (version == 22) {
                            argument("ro.product.locale.region")
                        } else {
                            argument("persist.sys.country")
                        }
                    }
                    .argument(value)
                    .execute()
        }

    var locale: String
        get() {
            return if (version <= 22) {
                "${language}_$region"
            } else {
                Adb().arguments(
                    "shell",
                    "getprop",
                    "ro.product.locale")
                    .execute().replace("-", "_")
            }
        }
        set(value) {
            if (version <= 22) {
                val (language, territory) = value.split("_")
                this.language = language
                this.region = territory
                restart()
            } else {
                Adb()
                    .shell()
                    .arguments(
                        "setprop",
                        "persist.sys.locale",
                        value.replace("_", "-"))
                    .execute()
            }
            restart()
        }

    val timeZone: String
        get() {
            val result = Adb()
                .shell()
                .arguments(
                    "getprop",
                    "persist.sys.timezone")
                .execute()
            return result.trim()
        }

    private val displayDensity: Int
        get() {
            val densityLine = Adb()
                .shell()
                .arguments(
                    "wm",
                    "density")
                .execute().trim()
            return if (densityLine.contains("Override density", true)) {
                densityLine.split(":").last().trim().toInt()
            } else {
                densityLine.substring("Physical density: ".length).trim().toInt()
            }
        }

    private val displaySize: String
        get() {
            val sizeLine = Adb()
                .shell()
                .arguments(
                    "wm",
                    "size")
                .execute().trim()
            return sizeLine.substring("Physical size: ".length).trim()
        }

    internal fun deviceKey(): String {
        return "$version-$displaySize@${displayDensity}dp-$locale"
    }

    val hasRootAccess: Boolean
        get() = Adb()
            .shell()
            .arguments(
                "ls",
                "data/data")
            .execute().trim().isNotEmpty()

    private fun restart() {
        val adb = Adb().shell()
        if (version <= 22) {
            adb.arguments(
                "setprop",
                "ctl.restart",
                "zygote")
        } else {
            adb.arguments(
                "stop;",
                "sleep 2;",
                "start")
        }
        adb.execute()
    }
}
