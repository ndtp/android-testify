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

import dev.testify.internal.StreamData.BufferedStream
import dev.testify.testifySettings
import org.gradle.api.Project
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

sealed class StreamData {

    abstract fun handleInputStream(inputStream: InputStream): String

    /**
     * Capture input stream from process and pass it to a StringBuilder
     */
    open class BufferedStream : StreamData() {

        override fun handleInputStream(inputStream: InputStream): String {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.useLines { sequence ->
                sequence.forEach { handleOutputLine(it, stringBuilder) }
            }
            return stringBuilder.toString()
        }

        open fun handleOutputLine(s: String, sb: StringBuilder) {
            sb.append(s)
            sb.append(System.lineSeparator())
        }
    }

    /**
     * Use StringBuild to produce a String of the process output and print to console
     */
    object ConsoleStream : BufferedStream() {
        override fun handleOutputLine(s: String, sb: StringBuilder) {
            super.handleOutputLine(s, sb)
            println(s)
        }
    }

    /**
     * Use an OutputStream to stream the process output to a client-side file
     */
    class BinaryStream(private val outputStream: OutputStream) : StreamData() {
        override fun handleInputStream(inputStream: InputStream): String {
            val buffer = ByteArray(size = 512)
            var length: Int
            while (inputStream.read(buffer).also { bytesWritten -> length = bytesWritten } != -1) {
                outputStream.write(buffer, 0, length)
            }
            return "OK"
        }
    }
}

internal val Project.destinationImageDirectory: String
    get() = "${project.testifySettings.baselineSourceDir}${File.separatorChar}"

fun runProcess(command: String, streamData: StreamData = BufferedStream(), dryRun: Boolean = false): String {
//    return if (dryRun) {
//        "Dry run $command"
//    } else {
//        val process = Runtime.getRuntime().exec(command)
//        val result = streamData.handleInputStream(process.inputStream)
//        process.waitFor()
//        result
//    }

    if (dryRun) {
        val result = DryRunCache.get(command)
        if (result != null) {
            println(AnsiFormat.Yellow, "\n\tresult from cache:[$result]")
            return result
        }
    }

    println(AnsiFormat.Yellow, "\n\tcommand:[$command]")

    val process = Runtime.getRuntime().exec(command)
    val result = streamData.handleInputStream(process.inputStream)
    process.waitFor()

    println(AnsiFormat.Yellow, "\tresult:[$result]")

    return result
}

private object DryRunCache {

    private val cache = mapOf(
        "adb -s emulator-5554 shell getprop ro.build.version.sdk" to "29",
        "adb -s emulator-5554 shell wm size" to "Physical size: 1080x2220",
        "adb -s emulator-5554 shell wm density" to "Physical density: 440",
        "adb -s emulator-5554 shell getprop persist.sys.locale" to "",
        "adb -s emulator-5554 shell settings put system show_password 0" to "",
        "adb -s emulator-5554 shell getprop persist.sys.timezone" to "Atlantic/Reykjavik",
        "adb -s emulator-5554 shell settings put secure show_ime_with_hard_keyboard 0" to "Atlantic/Reykjavik",
//        "adb -s emulator-5554 devices" to "\nList of devices attached\nemulator-5554   device\n",
        "adb -s emulator-5554 shell am get-current-user" to "0",
        "adb -s emulator-5554 devices" to "List of devices attached\n" +
            "emulator-5554   device\n" +
            "\n",
    )

    fun get(command: String): String? {
        val key = command.substringAfterLast("/")
        return cache[key]
    }
}

sealed class AnsiFormat(private val code: String) {
    object Reset : AnsiFormat("\u001B[0m")
    object Black : AnsiFormat("\u001B[30m")
    object Red : AnsiFormat("\u001B[31m")
    object Green : AnsiFormat("\u001B[32m")
    object Yellow : AnsiFormat("\u001B[33m")
    object Blue : AnsiFormat("\u001B[34m")
    object Purple : AnsiFormat("\u001B[35m")
    object Cyan : AnsiFormat("\u001B[36m")
    object White : AnsiFormat("\u001B[37m")
    object Bold : AnsiFormat("\u001B[1m")

    override fun toString(): String {
        return code
    }
}

fun println(format: AnsiFormat, message: Any?) {
    print(format, message)
    println()
}

fun print(format: AnsiFormat, message: Any?) {
    print(format)
    print(message)
    print(AnsiFormat.Reset)
}

internal fun File.assurePath() {
    if (!this.exists()) {
        this.mkdirs()
    }
    if (!this.exists()) {
        throw RuntimeException("Destination directory ${this.path} could not be found")
    }
}

internal fun File.deleteFilesWithSubstring(substring: String) {
    listFiles().filter {
        it.nameWithoutExtension.contains(substring)
    }.forEach {
        println("    Deleting ${it.name}")
        it.delete()
    }
}

@Suppress("UNCHECKED_CAST")
internal inline fun <reified T> String.fromEnv(defaultValue: T): T {
    val envVal: String? = System.getenv(this)
    return if (envVal?.isNotEmpty() == true) {
        when (defaultValue) {
            is Boolean -> envVal.toBoolean() as T
            else -> envVal as T
        }
    } else {
        defaultValue
    }
}
