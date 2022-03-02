package com.shopify.testify.sample

import com.shopify.testify.annotation.ScreenshotInstrumentation
import com.shopify.testify.sample.test.ScreenshotExtension
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@DisplayName("Give the MainActivity is loaded")
class Junit5ExampleTests {

    @JvmField
    @RegisterExtension
    var rule = ScreenshotExtension(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    @DisplayName("Then the default state is rendered")
    fun default() {
        rule.assertSame()
    }
}
