/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2021 Shopify Inc.
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
package dev.testify

import android.app.Instrumentation
import android.content.Context
import android.os.Bundle
import dev.testify.internal.output.OutputFileUtility
import dev.testify.report.ReportSession
import dev.testify.report.Reporter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import java.io.File

internal open class ReporterTest {

    private val mockContext: Context = mock()
    private val mockSession: ReportSession = mock()
    private val mockOutputFileUtility: OutputFileUtility = mock()
    private val mockInstrumentation: Instrumentation = mock()
    private val mockRule: ScreenshotRule<*> = mock()
    private val mockTestClass: Class<*> = ReporterTest::class.java
    private var mockDescription = TestDescription("startTest", mockTestClass)
    private val mockFile: File = mock()
    private val reporter = spy(Reporter(mockContext, mockSession, mockOutputFileUtility))

    @Before
    fun prepareMocks() {
        with(mockSession) {
            doReturn("SESSION-ID").whenever(this).sessionId
            doReturn("Today").whenever(this).getTimestamp(any(), anyOrNull())
            doReturn(1).whenever(this).failCount
            doReturn(2).whenever(this).passCount
            doReturn(3).whenever(this).testCount
            doReturn(true).whenever(this).isEqual(eq(mockFile))
            val lines = HEADER_LINES + BODY_LINES
            doAnswer {
                this.initFromLines(lines)
            }.whenever(this).initFromFile(eq(mockFile))
        }

        with(mockContext) {
            doReturn(File("foo")).whenever(this).getExternalFilesDir(any())
            doReturn(File("/data/data/com.app.example/app_testify")).whenever(this).getDir(eq("testify"), any())
            doReturn(File("/sdcard")).whenever(this).getExternalFilesDir(anyOrNull())
        }

        with(reporter) {
            doReturn("foo").whenever(this).getBaselinePath(any())
            doReturn("bar").whenever(this).getOutputPath(any())
            doReturn(mockFile).whenever(this).getReportFile()
            doNothing().whenever(this).writeToFile(any(), any())
            doNothing().whenever(this).clearFile(eq(mockFile))
            doReturn(BODY_LINES).whenever(this).readBodyLines(mockFile)
        }

        doReturn(false).whenever(mockOutputFileUtility).useSdCard(any())
        doReturn(mockContext).whenever(mockInstrumentation).context
        doReturn(true).whenever(mockFile).exists()
    }

    @Test
    fun `writeHeader() produces the expected yaml`() {
        reporter.insertHeader()
        assertEquals(FILE_HEADER, reporter.yaml)
    }

    @Test
    fun `startTest() produces the expected yaml`() {
        reporter.startTest(mockDescription)

        assertEquals(
            "    - test:\n" +
                "        name: startTest\n" +
                "        class: ReporterTest\n" +
                "        package: dev.testify\n",
            reporter.yaml
        )
    }

    @Test
    fun `captureOutput() produces the expected yaml`() {
        reporter.captureOutput(mockRule)
        assertEquals(
            "        baseline_image: assets/foo\n" +
                "        test_image: bar\n",
            reporter.yaml
        )
    }

    @Test
    fun `pass() produces the expected yaml`() {
        reporter.pass()
        assertEquals("        status: PASS\n", reporter.yaml)
    }

    @Test
    fun `fail() produces the expected yaml`() {
        reporter.fail(Exception("Custom description"))
        assertEquals(
            "        status: FAIL\n" +
                "        cause: UNKNOWN\n" +
                "        description: \"Custom description\"\n",
            reporter.yaml
        )
    }

    @Test
    fun `endTest() produces the expected yaml for a new session`() {
        doReturn(false).whenever(mockFile).exists()

        reporter.endTest()

        assertEquals(FILE_HEADER, reporter.yaml)
    }

    @Test
    fun `endTest() produces the expected yaml for an existing session`() {

        reporter.endTest()

        assertEquals(
            FILE_HEADER +
                "  - test:\n" +
                "    name: default\n" +
                "    class: ClientDetailsViewScreenshotTest\n" +
                "    package: dev.testify.sample.clients.details\n" +
                "    baseline_image: assets/screenshots/22-480x800@240dp-en_US/default.png\n" +
                "    test_image: /data/data/dev.testify.sample/app_images/screenshots/22-480x800@240dp-en_US/" +
                "ClientDetailsViewScreenshotTest_default.png\n" +
                "    status: PASS\n", reporter.yaml
        )
    }

    @Test
    fun `endTest() produces the expected yaml for when overwriting a different session`() {
        doReturn(false).whenever(mockSession).isEqual(eq(mockFile))

        reporter.endTest()

        assertEquals(FILE_HEADER, reporter.yaml)
    }

    @Test
    fun `output file path when not using sdcard`() {
        val reporter = spy(Reporter(mockContext, mockSession, mockOutputFileUtility))
        doReturn(mock<Bundle>()).whenever(reporter).getEnvironmentArguments()
        val file = reporter.getReportFile()
        assertEquals("/data/data/com.app.example/app_testify/report.yml", file.path)
    }

    @Test
    fun `output file path when using sdcard`() {
        val reporter = spy(Reporter(mockContext, mockSession, mockOutputFileUtility))
        doReturn(mock<Bundle>()).whenever(reporter).getEnvironmentArguments()
        doReturn(true).whenever(mockOutputFileUtility).useSdCard(any())
        val file = reporter.getReportFile()
        assertEquals("/sdcard/testify/report.yml", file.path)
    }

    @Test
    fun `reporter output for a single test in a new session`() {
        doReturn(false).whenever(mockFile).exists()

        reporter.startTest(mockDescription)
        reporter.identifySession(mockInstrumentation)
        reporter.captureOutput(mockRule)
        reporter.pass()
        reporter.endTest()

        val yaml = reporter.yaml

        assertEquals(
            "---\n" +
                "- session: SESSION-ID\n" +
                "- date: Today\n" +
                "- failed: 1\n" +
                "- passed: 2\n" +
                "- total: 3\n" +
                "- tests:\n" +
                "    - test:\n" +
                "        name: startTest\n" +
                "        class: ReporterTest\n" +
                "        package: dev.testify\n" +
                "        baseline_image: assets/foo\n" +
                "        test_image: bar\n" +
                "        status: PASS\n", yaml
        )
    }

    @Test
    fun `reporter output for a multiples tests in a new session`() {

        with(setUpForFirstTest(spy(ReportSession()))) {
            this.startTest(mockDescription)
            this.identifySession(mockInstrumentation)
            this.captureOutput(mockRule)
            this.pass()
            this.endTest()
        }

        val reporter = setUpForSecondTest()
        with(reporter) {
            this.startTest(mockDescription)
            this.identifySession(mockInstrumentation)
            this.captureOutput(mockRule)
            this.fail(Exception("This is a failure"))
            this.endTest()
        }

        val lines = reporter.yaml.lines()
        assertEquals("---", lines[0])
        assertTrue("- session: [0-9a-fA-F]{8}-[0-9]{1,3}".toRegex().containsMatchIn(lines[1]))
        assertTrue("- date: [0-9]{4}-[0-9]{2}-[0-9]{2}@[0-9]{2}:[0-9]{2}:[0-9]{2}".toRegex().containsMatchIn(lines[2]))
        assertEquals("- failed: 1", lines[3])
        assertEquals("- passed: 1", lines[4])
        assertEquals("- total: 2", lines[5])
        assertEquals("- tests:", lines[6])
        assertEquals("    - test:", lines[7])
        assertEquals("        name: failingTest", lines[8])
        assertEquals("        class: ReporterTest", lines[9])
        assertEquals("        package: dev.testify", lines[10])
        assertEquals("        baseline_image: assets/foo", lines[11])
        assertEquals("        test_image: bar", lines[12])
        assertEquals("        status: FAIL", lines[13])
        assertEquals("        cause: UNKNOWN", lines[14])
        assertEquals("        description: \"This is a failure\"", lines[15])
        assertEquals("    - test:", lines[16])
        assertEquals("        name: passingTest", lines[17])
        assertEquals("        class: ReporterTest", lines[18])
        assertEquals("        package: dev.testify", lines[19])
        assertEquals("        baseline_image: assets/foo", lines[20])
        assertEquals("        test_image: bar", lines[21])
        assertEquals("        status: PASS", lines[22])
    }

    private val Reporter.yaml: String
        get() {
            return this.builder.toString()
        }

    private fun setUpForFirstTest(session: ReportSession): Reporter {
        val reporter = spy(Reporter(mockContext, session, mockOutputFileUtility))

        with(reporter) {
            doReturn("foo").whenever(this).getBaselinePath(any())
            doReturn("bar").whenever(this).getOutputPath(any())
            doReturn(mockFile).whenever(this).getReportFile()
            doNothing().whenever(this).writeToFile(any(), any())
            doNothing().whenever(this).clearFile(eq(mockFile))
        }

        doReturn(false).whenever(mockFile).exists()
        return reporter
    }

    private fun setUpForSecondTest(): Reporter {
        val session = spy(ReportSession())
        val reporter = setUpForFirstTest(session)
        val bodyLines = listOf(
            "    - test:",
            "        name: passingTest",
            "        class: ReporterTest",
            "        package: dev.testify",
            "        baseline_image: assets/foo",
            "        test_image: bar",
            "        status: PASS"
        )

        doReturn(true).whenever(mockFile).exists()
        doReturn(true).whenever(session).isEqual(any())
        doReturn(bodyLines).whenever(reporter).readBodyLines(mockFile)
        mockDescription = mockDescription.copy(methodName = "failingTest")

        with(session) {
            doReturn(true).whenever(this).isEqual(eq(mockFile))
            val lines = listOf(
                "---",
                "- session: 623815995-1",
                "- date: 2020-06-26@14:49:45",
                "- failed: 0",
                "- passed: 1",
                "- total: 1",
                "- tests:"
            ) + bodyLines

            doAnswer {
                this.initFromLines(lines)
            }.whenever(this).initFromFile(eq(mockFile))
        }
        return reporter
    }

    companion object {
        private const val FILE_HEADER = "---\n" +
            "- session: SESSION-ID\n" +
            "- date: Today\n" +
            "- failed: 1\n" +
            "- passed: 2\n" +
            "- total: 3\n" +
            "- tests:\n"

        private val HEADER_LINES = listOf(
            "---",
            "- session: 623815995-477",
            "- date: 2020-06-26@14:49:45",
            "- failed: 1",
            "- passed: 3",
            "- total: 4",
            "- tests:"
        )

        private val BODY_LINES = listOf(
            "  - test:",
            "    name: default",
            "    class: ClientDetailsViewScreenshotTest",
            "    package: dev.testify.sample.clients.details",
            "    baseline_image: assets/screenshots/22-480x800@240dp-en_US/default.png",
            "    test_image: /data/data/dev.testify.sample/app_images/screenshots/22-480x800@240dp-en_US/" +
                "ClientDetailsViewScreenshotTest_default.png",
            "    status: PASS"
        )
    }
}
