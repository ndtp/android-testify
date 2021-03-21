/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Shopify Inc.
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
package com.shopify.testify

import android.app.Instrumentation
import android.content.Context
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import com.shopify.testify.report.ReportSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.BufferedReader
import java.util.Date
import java.util.TimeZone

class ReportSessionTest {

    @Test
    fun `addTest() increments the test count`() {
        val session = ReportSession()
        session.addTest()
        assertEquals(1, session.testCount)
        session.addTest()
        assertEquals(2, session.testCount)

        assertEquals(0, session.passCount)
        assertEquals(0, session.failCount)
    }

    @Test
    fun `pass() increments passCount`() {
        val session = ReportSession()
        session.pass()
        assertEquals(1, session.passCount)
        session.pass()
        assertEquals(2, session.passCount)

        assertEquals(0, session.testCount)
        assertEquals(0, session.failCount)
    }

    @Test
    fun `fail() increments failCount`() {
        val session = ReportSession()
        session.fail()
        assertEquals(1, session.failCount)
        session.fail()
        assertEquals(2, session.failCount)

        assertEquals(0, session.testCount)
        assertEquals(0, session.passCount)
    }

    @Test
    fun `timestamp is correctly formatted`() {
        val date = Date(1591234567890)
        assertEquals(
            "2020-06-03@21:36:07",
            ReportSession().getTimestamp(date, TimeZone.getTimeZone("America/New_York"))
        )
    }

    @Test
    fun `session id is correctly formatted`() {
        val instrumentation: Instrumentation = mock()
        val thread: Thread = mock()
        val context: Context = mock()

        doReturn(context).whenever(instrumentation).context
        doReturn(123L).whenever(thread).id

        val id = ReportSession.getSessionId(instrumentation, thread)
        assertTrue("^[0-9a-fA-F]{8}-123".toRegex().containsMatchIn(id))
    }

    @Test
    fun `can read session id from file`() {
        val bufferedReader: BufferedReader = mock()

        var count = 0
        whenever(bufferedReader.readLine()).doAnswer {
            when (count++) {
                0 -> "---"
                1 -> "- session: 623815995-477"
                2 -> "- date: 2020-06-26@14:49:45"
                else -> ""
            }
        }

        val id = ReportSession.getSessionIdFromFile(bufferedReader)
        assertEquals("623815995-477", id)
    }

    @Test
    fun `can init session from file contents`() {
        val lines = listOf(
            "---",
            "- session: 623815995-477",
            "- date: 2020-06-26@14:49:45",
            "- failed: 1",
            "- passed: 3",
            "- total: 4",
            "- tests:",
            "  - test:",
            "    name: default",
            "    class: ClientDetailsViewScreenshotTest",
            "    package: com.shopify.testify.sample.clients.details",
            "    baseline_image: assets/screenshots/22-480x800@240dp-en_US/default.png",
            "    test_image: /data/data/com.shopify.testify.sample/app_images/screenshots/22-480x800@240dp-en_US/" +
                "ClientDetailsViewScreenshotTest_default.png",
            "    status: PASS"
        )

        val session = ReportSession()
        session.initFromLines(lines)

        assertEquals(1, session.failCount)
        assertEquals(3, session.passCount)
        assertEquals(4, session.testCount)
    }

    @Test
    fun `insertSessionInfo() produces expected yaml`() {
        val session = spy(ReportSession())
        repeat(1) { session.fail() }
        repeat(2) { session.pass() }
        repeat(3) { session.addTest() }
        ReportSession.injectSessionId(session, "12345678-123")

        doReturn("2020-06-26@17:34:57").whenever(session).getTimestamp(any(), anyOrNull())

        val builder = StringBuilder()
        val info = session.insertSessionInfo(builder).toString()

        assertEquals(
            "- session: 12345678-123\n" +
                "- date: 2020-06-26@17:34:57\n" +
                "- failed: 1\n" +
                "- passed: 2\n" +
                "- total: 3\n", info
        )
    }
}
