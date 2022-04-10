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

object Device {

    private val version: Int
        get() {
            return Adb().getprop("ro.build.version.sdk").toInt()
        }

    val locale: String
        get() {
            return when {
                version in 21..22 -> {
                    var language = Adb().getprop("persist.sys.language")
                    if (language.isBlank()) {
                        language = "en"
                    }
                    var region = Adb().getprop("persist.sys.country")
                    if (region.isBlank()) {
                        region = "US"
                    }
                    "${language}_$region"
                }
                version >= 23 -> {
                    var result = Adb().getprop("persist.sys.locale").trim().replace("-", "_")
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
            return Adb().getprop("persist.sys.timezone")
        }

    private val displayDensity: Int
        get() {
            val densityLine = Adb()
                .shell()
                .arguments(
                    "wm",
                    "density"
                )
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
                    "size"
                )
                .execute().trim()
            return sizeLine.substring("Physical size: ".length).trim()
        }

    internal fun deviceKey(): String {
        return "$version-$displaySize@${displayDensity}dp-$locale"
    }

    private fun Adb.getprop(prop: String): String {
        shell()
        argument("getprop")
        argument(prop)
        return execute().trim()
    }
}
