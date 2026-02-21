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

object Device {

    fun version(adbService: AdbService): Int {
        return Adb(adbService).getprop(adbService, "ro.build.version.sdk").toInt()
    }

    fun locale(adbService: AdbService): String {
        return when {
            version(adbService) in 21..22 -> {
                var language = Adb(adbService).getprop(adbService, "persist.sys.language")
                if (language.isBlank()) {
                    language = "en"
                }
                var region = Adb(adbService).getprop(adbService, "persist.sys.country")
                if (region.isBlank()) {
                    region = "US"
                }
                "${language}_$region"
            }

            version(adbService) >= 23 -> {
                var result = Adb(adbService).getprop(adbService, "persist.sys.locale").trim().replace("-", "_")
                if (result.isBlank()) {
                    result = "en_US"
                }
                return result
            }

            else -> "unsupported"
        }
    }

    fun timeZone(adbService: AdbService): String {
        return Adb(adbService).getprop(adbService, "persist.sys.timezone")
    }

    fun displayDensity(adbService: AdbService): Int {
        val densityLine = Adb(adbService)
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

    fun displaySize(adbService: AdbService): String {
        val sizeLine = Adb(adbService)
            .shell()
            .arguments(
                "wm",
                "size"
            )
            .execute().trim()
        return sizeLine.substring("Physical size: ".length).trim()
    }

    internal fun user(adbService: AdbService): String {
        val user = Adb(adbService)
            .shell()
            .arguments(
                "am",
                "get-current-user"
            )
            .execute().trim()
        return user.ifEmpty { "0" }
    }

    internal fun deviceKey(adbService: AdbService): String {
        return "${version(adbService)}-${displaySize(adbService)}@${displayDensity(adbService)}dp-${locale(adbService)}"
    }

    private fun Adb.getprop(adbService: AdbService, prop: String): String {
        shell()
        argument("getprop")
        argument(prop)
        return execute().trim()
    }

    fun isEmpty(adbService: AdbService): Boolean {
        return count(adbService) == 0
    }

    fun count(adbService: AdbService): Int {
        val result = Adb(adbService)
            .argument("devices")
            .execute(targetsDevice = false)

        return result.lines().count {
            it.isNotBlank() && !it.contains("List of devices attached")
        }
    }

    fun targets(adbService: AdbService): Map<Int, String> {
        val map = HashMap<Int, String>()
        enumerateDevices(adbService).mapIndexed { index, s ->
            map[index] = s
        }
        return map
    }

    private fun enumerateDevices(adbService: AdbService): List<String> {
        val result = Adb(adbService)
            .argument("devices")
            .execute(targetsDevice = false)

        return result.lines().filter {
            it.isNotBlank() && !it.contains("List of devices attached")
        }.map {
            it.substringBefore("\t")
        }
    }
}
