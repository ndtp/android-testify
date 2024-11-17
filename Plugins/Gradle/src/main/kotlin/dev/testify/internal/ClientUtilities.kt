/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022-2024 ndtp
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

import dev.testify.TestifyPlugin
import dev.testify.internal.StreamData.BufferedStream
import dev.testify.testifySettings
import org.gradle.api.Project
import org.gradle.internal.logging.text.StyledTextOutput
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

fun runProcess(command: String, streamData: StreamData = BufferedStream()): String {
    val process = Runtime.getRuntime().exec(command)
    val result = streamData.handleInputStream(process.inputStream)
    process.waitFor()
    return result
}

enum class Style {
    Description,
    Identifier,
    Info,
    Error,
    Normal,
    ProgressStatus,
    Success,
    SuccessHeader,
    Failure,
    FailureHeader,
    Header,
}

fun println(style: Style = Style.Normal, message: String?) {
    with(TestifyPlugin.styledTextOutput) {
        val textStyle = when (style) {
            Style.Description -> StyledTextOutput.Style.Description
            Style.Failure -> StyledTextOutput.Style.Failure
            Style.FailureHeader -> StyledTextOutput.Style.FailureHeader
            Style.Header -> StyledTextOutput.Style.Header
            Style.Info -> StyledTextOutput.Style.Info
            Style.ProgressStatus -> StyledTextOutput.Style.ProgressStatus
            Style.Success -> StyledTextOutput.Style.Success
            Style.Identifier -> StyledTextOutput.Style.Identifier
            Style.Error -> StyledTextOutput.Style.Error
            Style.Normal -> StyledTextOutput.Style.Normal
            Style.SuccessHeader -> StyledTextOutput.Style.SuccessHeader
        }

        this?.style(textStyle)?.println(message) ?: println(message)
    }
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
    listFiles()?.filter {
        it.nameWithoutExtension.contains(substring)
    }?.forEach {
        println("    Deleting ${it.name}")
        it.delete()
    }
}

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
