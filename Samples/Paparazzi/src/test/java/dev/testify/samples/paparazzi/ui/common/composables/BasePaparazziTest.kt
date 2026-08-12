package dev.testify.samples.paparazzi.ui.common.composables

import dev.testify.samples.paparazzi.test.PaparazziTestRule
import org.junit.Rule

abstract class BasePaparazziTest {

    @get:Rule
    val rule = PaparazziTestRule()
}
