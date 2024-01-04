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

package dev.testify.internal

class Device private constructor(private val adb: Adb) {

    private val version: Int
        get() {
            return adb.getprop("ro.build.version.sdk").toInt()
        }

    val locale: String
        get() {
            return when {
                version in 21..22 -> {
                    var language = adb.getprop("persist.sys.language")
                    if (language.isBlank()) {
                        language = "en"
                    }
                    var region = adb.getprop("persist.sys.country")
                    if (region.isBlank()) {
                        region = "US"
                    }
                    "${language}_$region"
                }
                version >= 23 -> {
                    var result = adb.getprop("persist.sys.locale").trim().replace("-", "_")
                    if (result.isBlank()) {
                        result = "en_US"
                    }
                    return result
                }
                else -> "unsupported"
            }
        }

    val timeZone: String
        get() {
            return adb.getprop("persist.sys.timezone")
        }

    private val displayDensity: Int
        get() {
            val densityLine = adb
                .execute {
                    shell()
                    arguments(
                        "wm",
                        "density"
                    )
                }.trim()
            return if (densityLine.contains("Override density", true)) {
                densityLine.split(":").last().trim().toInt()
            } else {
                densityLine.substring("Physical density: ".length).trim().toInt()
            }
        }

    private val displaySize: String
        get() {
            val sizeLine = adb
                .execute {
                    shell()
                    arguments(
                        "wm",
                        "size"
                    )
                }.trim()
            return sizeLine.substring("Physical size: ".length).trim()
        }

    internal val user: String
        get() {
            val user = adb
                .execute {
                    shell()
                    arguments(
                        "am",
                        "get-current-user"
                    )
                }.trim()
            return user.ifEmpty { "0" }
        }

    internal fun deviceKey(): String {
        return "$version-$displaySize@${displayDensity}dp-$locale"
    }

    private fun Adb.getprop(prop: String): String {
        return execute {
            shell()
            argument("getprop")
            argument(prop)
        }.trim()
    }

    val isEmpty: Boolean
        get() = (count == 0)

    val count: Int
        get() {
            val result = adb
                .execute {
                    global()
                    argument("devices")
                }

            return result.lines().filter {
                it.isNotBlank() && !it.contains("List of devices attached")
            }.count()
        }

    fun target(index: Int): String? {
        return enumerateDevices().getOrNull(index)
    }

    val targets: Map<Int, String>
        get() {
            val map = HashMap<Int, String>()
            enumerateDevices().mapIndexed { index, s ->
                map[index] = s
            }
            return map
        }

    private fun enumerateDevices(): List<String> {
        val result = adb.execute {
            global()
            argument("devices")
        }

        return result.lines().filter {
            it.isNotBlank() && !it.contains("List of devices attached")
        }.map {
            it.substringBefore("\t")
        }
    }

    companion object {
        fun construct(adb: Adb): Device = Device(adb)
    }
}
