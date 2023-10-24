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

package dev.testify.core

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import dev.testify.internal.extensions.languageTag
import dev.testify.internal.helpers.buildVersionSdkInt
import java.util.Locale

typealias TestName = Pair<String, String>

const val DEFAULT_FOLDER_FORMAT = "a-wxh@d-l"
const val DEFAULT_NAME_FORMAT = "c_n"

@Suppress("DEPRECATION")
fun getDeviceDimensions(context: Context): Pair<Int, Int> {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val metrics = DisplayMetrics()
    val display = windowManager.defaultDisplay

    display.getRealMetrics(metrics)
    val realWidth = metrics.widthPixels
    val realHeight = metrics.heightPixels

    return realWidth to realHeight
}

fun getDeviceDescription(context: Context): String {
    return formatDeviceString(DeviceStringFormatter(context, null), DEFAULT_FOLDER_FORMAT)
}

fun formatDeviceString(formatter: DeviceStringFormatter, format: String): String {
    /*
    a: API level (ex. 21)
    w: Device width (ex.720)
    h: Device height (ex. 1440)
    d: Device density (ex. 320dp)
    l: Locale (ex. en_US)
    c: Test class (ex. OrderDetailTest)
    n: Test name (ex. testDefault)
     */
    val a = formatter.androidVersion
    val w = formatter.deviceWidth
    val h = formatter.deviceHeight
    val d = formatter.deviceDensity
    val l = formatter.locale
    val c = formatter.testClass
    val n = formatter.getTestName()

    val stringBuilder = StringBuilder("")
    for (element in format) {
        when (element) {
            'a' -> stringBuilder.append(a)
            'w' -> stringBuilder.append(w)
            'h' -> stringBuilder.append(h)
            'd' -> stringBuilder.append(d)
            'l' -> stringBuilder.append(l)
            'c' -> stringBuilder.append(c)
            'n' -> stringBuilder.append(n)
            else -> stringBuilder.append(element)
        }
    }

    return stringBuilder.toString()
}

open class DeviceStringFormatter(private val context: Context, private val testName: TestName?) {

    internal open val dimensions: Pair<Int, Int>
        get() = getDeviceDimensions(context)

    internal open val androidVersion: String
        get() = buildVersionSdkInt().toString()

    internal open val deviceWidth: String
        get() = dimensions.first.toString()

    internal open val deviceHeight: String
        get() = dimensions.second.toString()

    internal open val deviceDensity: String
        get() = context.resources.displayMetrics.densityDpi.toString() + "dp"

    internal open val locale: String
        get() = Locale.getDefault().languageTag

    internal open val testClass: String
        get() = testName?.first.orEmpty()

    internal open fun getTestName(): String {
        return testName?.second.orEmpty()
    }
}
