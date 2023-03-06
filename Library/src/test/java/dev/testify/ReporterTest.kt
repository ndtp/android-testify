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
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

internal open class ReporterTest {

    private lateinit var mockContext: Context
    private lateinit var mockSession: ReportSession
    private val mockOutputFileUtility: OutputFileUtility = mockk()
    private val mockInstrumentation: Instrumentation = mockk()
    private val mockRule: ScreenshotRule<*> = mockk()
    private val mockTestClass: Class<*> = ReporterTest::class.java
    private var mockDescription = TestDescription("startTest", mockTestClass)
    private val mockFile: File = mockk()
    private lateinit var reporter: Reporter

    @Before
    fun prepareMocks() {
        mockSession = spyk(ReportSession()) {
            every { sessionId } returns "SESSION-ID"
            every { getTimestamp(any(), any()) } returns "Today"
            every { failCount } returns 1
            every { passCount } returns 2
            every { testCount } returns 3
            every { isEqual(mockFile) } returns true
            val lines = HEADER_LINES + BODY_LINES

            every { initFromFile(mockFile) } answers { initFromLines(lines) }
        }

        mockContext = mockk(relaxed = true) {
            every { getExternalFilesDir(any()) } returns File("foo")
            every { getDir("testify", any()) } returns File("/data/data/com.app.example/app_testify")
            every { getExternalFilesDir(any()) } returns File("/sdcard")
        }

        reporter = spyk(Reporter(mockContext, mockSession, mockOutputFileUtility))
        reporter.configureMocks(BODY_LINES)

        every { mockOutputFileUtility.useSdCard(any()) } returns false
        every { mockInstrumentation.context } returns mockContext
        every { mockFile.exists() } returns true
    }

    private fun Reporter.configureMocks(body: List<String>? = null) {
        every { getBaselinePath(any()) } returns "foo"
        every { getOutputPath(any()) } returns "bar"
        every { getReportFile() } returns mockFile
        every { writeToFile(any(), any()) } just runs
        every { clearFile(mockFile) } just runs
        body?.let { every { readBodyLines(mockFile) } returns body }
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
        every { mockFile.exists() } returns false

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
                "    status: PASS\n",
            reporter.yaml
        )
    }

    @Test
    fun `endTest() produces the expected yaml for when overwriting a different session`() {
        every { mockSession.isEqual(mockFile) } returns false

        reporter.endTest()

        assertEquals(FILE_HEADER, reporter.yaml)
    }

    @Test
    fun `output file path when not using sdcard`() {
        val reporter = spyk(Reporter(mockContext, mockSession, mockOutputFileUtility))
        every { reporter.getEnvironmentArguments() } returns mockk<Bundle>()
        val file = reporter.getReportFile()
        assertEquals("/data/data/com.app.example/app_testify/report.yml", file.path)
    }

    @Test
    fun `output file path when using sdcard`() {
        val reporter = spyk(Reporter(mockContext, mockSession, mockOutputFileUtility))
        every { reporter.getEnvironmentArguments() } returns mockk<Bundle>()
        every { mockOutputFileUtility.useSdCard(any()) } returns true
        val file = reporter.getReportFile()
        assertEquals("/sdcard/testify/report.yml", file.path)
    }

    @Test
    fun `reporter output for a single test in a new session`() {
        every { mockFile.exists() } returns false

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
                "        status: PASS\n",
            yaml
        )
    }

    @Test
    fun `reporter output for a multiples tests in a new session`() {
        var reporter = setUpForFirstTest(spyk(ReportSession()))
        reporter.startTest(mockDescription)
        reporter.identifySession(mockInstrumentation)
        reporter.captureOutput(mockRule)
        reporter.pass()
        reporter.endTest()

        reporter = setUpForSecondTest()
        reporter.startTest(mockDescription)
        reporter.identifySession(mockInstrumentation)
        reporter.captureOutput(mockRule)
        reporter.fail(Exception("This is a failure"))
        reporter.endTest()

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
        val reporter = spyk(Reporter(mockContext, session, mockOutputFileUtility))
        reporter.configureMocks()
        every { mockFile.exists() } returns false
        return reporter
    }

    private fun setUpForSecondTest(): Reporter {
        val session = spyk(ReportSession())
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

        every { mockFile.exists() } returns true
        every { session.isEqual(any()) } returns true
        every { reporter.readBodyLines(mockFile) } returns bodyLines
        mockDescription = mockDescription.copy(methodName = "failingTest")

        every { session.isEqual(mockFile) } returns true
        val lines = listOf(
            "---",
            "- session: 623815995-1",
            "- date: 2020-06-26@14:49:45",
            "- failed: 0",
            "- passed: 1",
            "- total: 1",
            "- tests:"
        ) + bodyLines

        every { session.initFromFile(mockFile) } answers { session.initFromLines(lines) }
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
