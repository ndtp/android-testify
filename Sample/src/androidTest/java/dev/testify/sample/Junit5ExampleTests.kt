package dev.testify.sample

import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.sample.test.ScreenshotExtension
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Given the MainActivity is loaded")
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

    @ScreenshotInstrumentation
    @ParameterizedTest
    @ValueSource(strings = ["A", "B", "C"])
    @DisplayName("Then the letter is rendered")
    fun parameterized(values: String) {
        rule
            .setViewModifications {
                rule.activity.title = values
            }
            .assertSame()
    }

    @DisplayName("With a nested test")
    @Nested
    inner class NestedTest {
        @ScreenshotInstrumentation
        @Test
        @DisplayName("Then the default state is rendered")
        fun default() {
            rule.assertSame()
        }
    }
}
