package dev.testify.sample.clients

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.testify.ScreenshotRule
import dev.testify.sample.clients.index.ClientListActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClientListActivityScreenshotTest {

    @get:Rule
    var rule = ScreenshotRule(ClientListActivity::class.java, enableReporter = true)

    /**
     * Demonstrates normal usage of Gradle Managed Devices with Testify
     *
     */
    @Test
    fun default() {
        rule.assertSame()
    }

    /**
     * Demonstrates how works screenshot verification with Gradle managed device
     */
    @Test
    fun testRecorded() {
        rule
            .setRecordModeEnabled(false)
            .assertSame()
    }

    /**
     * Demonstrates how to record new baseline with Gradle managed device
     */
    @Test
    fun testMissingBaseline() {
        rule
            .setRecordModeEnabled(true)
            .assertSame()
    }
}
