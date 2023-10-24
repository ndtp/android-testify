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
package dev.testify.report

import android.app.Instrumentation
import android.content.Context
import com.google.common.truth.Truth.assertThat
import dev.testify.TestDescription
import dev.testify.core.getDeviceDescription
import dev.testify.output.Destination
import dev.testify.output.getDestination
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

internal open class ReporterTest {

    private lateinit var mockContext: Context
    private lateinit var mockSession: ReportSession
    private val mockInstrumentation: Instrumentation = mockk()
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
            every { skipCount } returns 3
            every { testCount } returns 6
            every { isEqual(mockFile) } returns true
            val lines = HEADER_LINES + BODY_LINES

            every { initFromFile(mockFile) } answers { initFromLines(lines) }
        }

        mockContext = mockk(relaxed = true) {
            every { getExternalFilesDir(any()) } returns File("ext")
            every { getDir("testify", any()) } returns File("/data/data/com.app.example/app_testify")
            every { getExternalFilesDir(any()) } returns File("/sdcard")
        }

        val mockDestination = mockk<Destination>(relaxed = true) {
            every { assureDestination(any()) } returns true
        }

        reporter = spyk(Reporter.create(mockContext, mockSession))
        reporter.configureMocks(BODY_LINES)

        mockkStatic(::getDestination)
        mockkStatic(::getDeviceDescription)

        every { mockInstrumentation.context } returns mockContext
        every { mockFile.exists() } returns true
        every { getDeviceDescription(any()) } returns "device"
        every { getDestination(any(), any(), any(), any(), any()) } returns mockDestination
        every { reporter.finalize() } returns true
    }

    private fun Reporter.configureMocks(body: List<String>? = null) {
        every { getOutputPath() } returns "path"
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
        reporter.finalize()

        assertEquals(
            "    - test:\n" +
                "        name: startTest\n" +
                "        class: ReporterTest\n" +
                "        package: dev.testify.report\n",
            reporter.yaml
        )
    }

    @Test
    fun `captureOutput() produces the expected yaml`() {
        every { reporter.getBaselinePath() } returns "path"
        reporter.captureOutput()
        assertEquals(
            "        baseline_image: assets/path\n" +
                "        test_image: path\n",
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
    fun `output file destination`() {
        val reporter = spyk(Reporter.create(mockContext, mockSession))
        every { reporter.getEnvironmentArguments() } returns mockk()

        reporter.getReportFile()

        verify {
            getDestination(
                context = any(),
                fileName = "report",
                extension = ".yml",
                customKey = "",
                root = "testify"
            )
        }
    }

    @Test
    fun `reporter output for a single test in a new session`() {
        every { mockFile.exists() } returns false

        reporter.startTest(mockDescription)
        reporter.identifySession(mockInstrumentation)
        reporter.captureOutput()
        reporter.pass()
        reporter.endTest()

        val yaml = reporter.yaml

        assertEquals(
            "---\n" +
                "- session: SESSION-ID\n" +
                "- date: Today\n" +
                "- failed: 1\n" +
                "- passed: 2\n" +
                "- skipped: 3\n" +
                "- total: 6\n" +
                "- tests:\n" +
                "    - test:\n" +
                "        name: startTest\n" +
                "        class: ReporterTest\n" +
                "        package: dev.testify.report\n" +
                "        baseline_image: assets/screenshots/device/startTest.png\n" +
                "        test_image: path\n" +
                "        status: PASS\n",
            yaml
        )
    }

    @Test
    fun `reporter output for a multiples tests in a new session`() {
        var reporter = setUpForFirstTest(spyk(ReportSession()))
        reporter.startTest(mockDescription)
        reporter.identifySession(mockInstrumentation)
        reporter.captureOutput()
        reporter.pass()
        reporter.endTest()

        reporter = setUpForSecondTest()
        reporter.startTest(mockDescription)
        reporter.identifySession(mockInstrumentation)
        reporter.captureOutput()
        reporter.fail(Exception("This is a failure"))
        reporter.endTest()

        reporter = setUpForThirdTest()
        reporter.startTest(mockDescription)
        reporter.identifySession(mockInstrumentation)
        reporter.captureOutput()
        reporter.skip()
        reporter.endTest()

        val lines = reporter.yaml.lines()
        assertEquals("---", lines[0])
        assertTrue("- session: [0-9a-fA-F]{8}-[0-9]{1,3}".toRegex().containsMatchIn(lines[1]))
        assertTrue("- date: [0-9]{4}-[0-9]{2}-[0-9]{2}@[0-9]{2}:[0-9]{2}:[0-9]{2}".toRegex().containsMatchIn(lines[2]))
        assertEquals("- failed: 1", lines[3])
        assertEquals("- passed: 1", lines[4])
        assertEquals("- skipped: 1", lines[5])
        assertEquals("- total: 3", lines[6])
        assertEquals("- tests:", lines[7])
        assertEquals("    - test:", lines[8])
        assertEquals("        name: skipTest", lines[9])
        assertEquals("        class: ReporterTest", lines[10])
        assertEquals("        package: dev.testify.report", lines[11])
        assertEquals("        baseline_image: assets/screenshots/device/skipTest.png", lines[12])
        assertEquals("        test_image: path", lines[13])
        assertEquals("        status: SKIP", lines[14])
        assertEquals("    - test:", lines[15])
        assertEquals("        name: failingTest", lines[16])
        assertEquals("        class: ReporterTest", lines[17])
        assertEquals("        package: dev.testify", lines[18])
        assertEquals("        baseline_image: assets/device", lines[19])
        assertEquals("        test_image: path", lines[20])
        assertEquals("        status: FAIL", lines[21])
        assertEquals("        cause: UNKNOWN", lines[22])
        assertEquals("        description: \"This is a failure\"", lines[23])
        assertEquals("    - test:", lines[24])
        assertEquals("        name: passingTest", lines[25])
        assertEquals("        class: ReporterTest", lines[26])
        assertEquals("        package: dev.testify", lines[27])
        assertEquals("        baseline_image: assets/device", lines[28])
        assertEquals("        test_image: path", lines[29])
        assertEquals("        status: PASS", lines[30])
    }

    private val Reporter.yaml: String
        get() {
            return this.builder.toString()
        }

    private fun setUpForFirstTest(session: ReportSession): Reporter {
        val reporter = spyk(Reporter.create(mockContext, session))
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
            "        baseline_image: assets/device",
            "        test_image: path",
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
            "- skipped: 0",
            "- total: 1",
            "- tests:"
        ) + bodyLines

        every { session.initFromFile(mockFile) } answers { session.initFromLines(lines) }
        return reporter
    }

    private fun setUpForThirdTest(): Reporter {
        val session = spyk(ReportSession())
        val reporter = setUpForFirstTest(session)
        val bodyLines = listOf(
            "    - test:",
            "        name: failingTest",
            "        class: ReporterTest",
            "        package: dev.testify",
            "        baseline_image: assets/device",
            "        test_image: path",
            "        status: FAIL",
            "        cause: UNKNOWN",
            "        description: \"This is a failure\"",
            "    - test:",
            "        name: passingTest",
            "        class: ReporterTest",
            "        package: dev.testify",
            "        baseline_image: assets/device",
            "        test_image: path",
            "        status: PASS"
        )

        every { mockFile.exists() } returns true
        every { session.isEqual(any()) } returns true
        every { reporter.readBodyLines(mockFile) } returns bodyLines
        mockDescription = mockDescription.copy(methodName = "skipTest")

        every { session.isEqual(mockFile) } returns true
        val lines = listOf(
            "---",
            "- session: 623815995-1",
            "- date: 2020-06-26@14:49:45",
            "- failed: 1",
            "- passed: 1",
            "- skipped: 0",
            "- total: 2",
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
            "- skipped: 3\n" +
            "- total: 6\n" +
            "- tests:\n"

        private val HEADER_LINES = listOf(
            "---",
            "- session: 623815995-477",
            "- date: 2020-06-26@14:49:45",
            "- failed: 1",
            "- passed: 3",
            "- skipped: 2",
            "- total: 6",
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

    @Test
    fun `verify headerLineCount`() {
        assertThat(reporter.headerLineCount).isEqualTo(8)
    }
}
