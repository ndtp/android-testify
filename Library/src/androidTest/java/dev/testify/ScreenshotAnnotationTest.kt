package dev.testify

import android.os.Build
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenshotAnnotationTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setUp() {
            val arguments = Bundle().apply {
                putString("annotation", "com.example.Annotation")
            }
            mockkStatic(InstrumentationRegistry::class)
            every { InstrumentationRegistry.getArguments() } returns arguments
        }

        @JvmStatic
        @AfterClass
        fun tearDown() {
            unmockkAll()
        }
    }

    @get:Rule
    var rule: ScreenshotRule<TestActivity> = ScreenshotRule(TestActivity::class.java)

    @get:Rule
    var thrown: ExpectedException = ExpectedException.none()

    /**
     * An annotation is required when run from the gradle plugin.
     */
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.P)
    @Test
    fun testAnnotationRequired() {
        thrown.expect(RuntimeException::class.java)
        thrown.expectMessage(
            "dev.testify.internal.exception.MissingScreenshotInstrumentationAnnotationException: " +
                "Please add annotation com.example.Annotation to the test 'testAnnotationRequired'"
        )
        rule.assertSame()
    }
}
