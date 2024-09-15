package dev.testify.test

import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach

abstract class BaseTest {

    @BeforeEach
    open fun setUp() {
        MockKAnnotations.init(this)
    }

    companion object {

        @AfterAll
        @JvmStatic
        fun tearDown() {
            clearAllMocks()
            unmockkAll()
        }
    }
}
