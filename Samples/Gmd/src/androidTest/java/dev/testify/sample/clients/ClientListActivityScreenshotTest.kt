package dev.testify.sample.clients

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.testify.ScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.sample.clients.index.ClientListActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClientListActivityScreenshotTest {

    @get:Rule
    var rule = ScreenshotRule(ClientListActivity::class.java)

    /**
     * Demonstrates how works screenshot verification with Gradle managed device
     */
    @ScreenshotInstrumentation
    @Test
    fun testRecorded() {
        rule
            .setRecordModeEnabled(false)
            .assertSame()
    }

    /**
     * Demonstrates how to record new baseline with Gradle managed device
     */
    @ScreenshotInstrumentation
    @Test
    fun testMissingBaseline() {
        rule
            .setRecordModeEnabled(true)
            .assertSame()
    }
}
