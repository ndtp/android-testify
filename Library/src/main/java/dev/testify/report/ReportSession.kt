/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2020 Shopify Inc.
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
package dev.testify.report

import android.app.Instrumentation
import androidx.annotation.VisibleForTesting
import dev.testify.internal.annotation.ExcludeFromJacocoGeneratedReport
import java.io.BufferedReader
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * A representation of a test run session.
 *
 * @see [Reporter]
 */
internal open class ReportSession {

    /**
     * Unique identifier for a test run session.
     */
    @VisibleForTesting
    internal open lateinit var sessionId: String

    /**
     * Number of tests run.
     */
    @VisibleForTesting
    internal open var testCount = 0

    /**
     * Number of tests that passed.
     */
    @VisibleForTesting
    internal open var passCount = 0

    /**
     * Number of tests that were skipped.
     */
    @VisibleForTesting
    internal open var skipCount = 0

    /**
     * Number of tests that failed.
     */
    @VisibleForTesting
    internal open var failCount = 0

    /**
     * Adds a test to the session.
     */
    fun addTest() {
        testCount++
    }

    /**
     * Increments the number of tests that passed.
     */
    fun pass() {
        passCount++
    }

    /**
     * Increments the number of tests that were skipped.
     */
    fun skip() {
        skipCount++
    }

    /**
     * Increments the number of tests that failed.
     */
    fun fail() {
        failCount++
    }

    /**
     * Initialize the session from the given file.
     * This is used to initialize the session from a previous run.
     *
     * @param file The file to initialize from.
     */
    @ExcludeFromJacocoGeneratedReport
    open fun initFromFile(file: File) {
        initFromLines(file.readLines())
    }

    /**
     * Initialize the session from the given lines.
     * This is used to initialize the session from a previous run.
     *
     * @param lines The lines to initialize from.
     */
    @VisibleForTesting
    internal fun initFromLines(lines: List<String>) {
        failCount += lines[3].substringAfterLast(": ").toInt()
        passCount += lines[4].substringAfterLast(": ").toInt()
        skipCount += lines[5].substringAfterLast(": ").toInt()
        testCount += lines[6].substringAfterLast(": ").toInt()
    }

    /**
     * The number of lines that the session information takes up.
     * This is used to determine where to insert the session information in the report.
     */
    val sessionLineCount: Int
        get() = listOf("session", "date", "failed", "passed", "skipped", "total").size

    /**
     * Inserts the session information into the given builder.
     *
     * @param builder The builder to insert the session information into.
     */
    fun insertSessionInfo(builder: StringBuilder): StringBuilder {
        return builder.insert(
            0,
            StringBuilder().apply {
                appendLine("- session: $sessionId")
                val timestamp = getTimestamp(Calendar.getInstance().time)
                appendLine("- date: $timestamp")
                appendLine("- failed: $failCount")
                appendLine("- passed: $passCount")
                appendLine("- skipped: $skipCount")
                appendLine("- total: $testCount")
            }
        )
    }

    /**
     * Identifies the session.
     * Generates a unique identifier for the session.
     *
     * @param instrumentation The instrumentation to use to identify the session.
     */
    fun identifySession(instrumentation: Instrumentation) {
        sessionId = getSessionId(instrumentation, Thread.currentThread())
    }

    /**
     * Checks if the given file is for the same session.
     *
     * @param file The file to check.
     */
    @ExcludeFromJacocoGeneratedReport
    open fun isEqual(file: File): Boolean {
        return (getSessionIdFromFile(file.bufferedReader())?.endsWith(sessionId) == true)
    }

    /**
     * Gets the timestamp for the given date.
     *
     * @param date The date to get the timestamp for.
     * @param timeZone The timezone to use for the timestamp.
     */
    @VisibleForTesting
    internal open fun getTimestamp(date: Date, timeZone: TimeZone? = null): String {
        return SimpleDateFormat("yyyy-MM-dd@HH:mm:ss", Locale.getDefault()).apply {
            if (timeZone != null) {
                this.timeZone = timeZone
            }
        }.format(date)
    }

    companion object {

        /**
         * Injects the session id into the given session, for testing purposes.
         */
        @VisibleForTesting
        internal fun injectSessionId(session: ReportSession, id: String) {
            session.sessionId = id
        }

        /**
         * Gets the session id from the given file.
         */
        @VisibleForTesting
        internal fun getSessionIdFromFile(bufferedReader: BufferedReader): String? {
            val secondLine = bufferedReader.use {
                it.readLine()
                it.readLine()
            }
            return secondLine?.substringAfterLast(": ")
        }

        /**
         * Gets the session id for the given instrumentation and thread.
         */
        @VisibleForTesting
        internal fun getSessionId(instrumentation: Instrumentation, thread: Thread): String {
            return "${instrumentation.context.hashCode().toString(16).padStart(8, '0')}-${thread.id}"
        }
    }
}
