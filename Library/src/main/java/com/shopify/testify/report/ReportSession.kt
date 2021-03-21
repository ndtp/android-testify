/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Shopify Inc.
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
package com.shopify.testify.report

import android.app.Instrumentation
import androidx.annotation.VisibleForTesting
import java.io.BufferedReader
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal open class ReportSession {
    @VisibleForTesting internal open lateinit var sessionId: String
    @VisibleForTesting internal open var testCount = 0
    @VisibleForTesting internal open var passCount = 0
    @VisibleForTesting internal open var failCount = 0

    fun addTest() {
        testCount++
    }

    fun pass() {
        passCount++
    }

    fun fail() {
        failCount++
    }

    open fun initFromFile(file: File) {
        initFromLines(file.readLines())
    }

    @VisibleForTesting internal fun initFromLines(lines: List<String>) {
        failCount += lines[3].substringAfterLast(": ").toInt()
        passCount += lines[4].substringAfterLast(": ").toInt()
        testCount += lines[5].substringAfterLast(": ").toInt()
    }

    fun insertSessionInfo(builder: StringBuilder): StringBuilder {
        return builder.insert(0, StringBuilder().apply {
            appendLine("- session: $sessionId")
            val timestamp = getTimestamp(Calendar.getInstance().time)
            appendLine("- date: $timestamp")
            appendLine("- failed: $failCount")
            appendLine("- passed: $passCount")
            appendLine("- total: $testCount")
        })
    }

    fun identifySession(instrumentation: Instrumentation) {
        sessionId = getSessionId(instrumentation, Thread.currentThread())
    }

    open fun isEqual(file: File): Boolean {
        return (getSessionIdFromFile(file.bufferedReader())?.endsWith(sessionId) == true)
    }

    @VisibleForTesting internal open fun getTimestamp(date: Date, timeZone: TimeZone? = null): String {
        return SimpleDateFormat("yyyy-MM-dd@HH:mm:ss", Locale.getDefault()).apply {
            if (timeZone != null) {
                this.timeZone = timeZone
            }
        }.format(date)
    }

    companion object {

        @VisibleForTesting internal fun injectSessionId(session: ReportSession, id: String) {
            session.sessionId = id
        }

        @VisibleForTesting internal fun getSessionIdFromFile(bufferedReader: BufferedReader): String? {
            val secondLine = bufferedReader.use {
                it.readLine()
                it.readLine()
            }
            return secondLine?.substringAfterLast(": ")
        }

        @VisibleForTesting internal fun getSessionId(instrumentation: Instrumentation, thread: Thread): String {
            return "${instrumentation.context.hashCode().toString(16).padStart(8, '0')}-${thread.id}"
        }
    }
}
