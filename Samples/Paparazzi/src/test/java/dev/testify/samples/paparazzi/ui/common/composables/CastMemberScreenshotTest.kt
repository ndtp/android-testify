package dev.testify.samples.paparazzi.ui.common.composables

import dev.testify.samples.paparazzi.test.PaparazziTestRule
import dev.testify.samples.paparazzi.ui.common.util.imagePromise
import org.junit.Rule
import org.junit.Test

/**
 * The Paparazzi equivalent of the Testify test in
 * `src/androidTest/java/dev/testify/samples/paparazzi/ui/common/composables/CastMemberScreenshotTest.kt`.
 *
 * Both render the same composable against the same image fixture, referenced by the same
 * `file:///android_asset/...` URI. The fixture lives in `src/debug/assets/images/headshots/` and is
 * loaded by Coil in both cases; only the way the screenshot is captured differs.
 */
class CastMemberScreenshotTest {

    @get:Rule
    val rule = PaparazziTestRule()

    @Test
    fun default() {
        rule.snapshot {
            CastMember(
                model = CastMemberPresentationModel(
                    id = 0,
                    name = "Benjamin Franklin",
                    characterName = "Himself",
                    image = imagePromise("file:///android_asset/images/headshots/BenjaminFranklin.jpg")
                )
            )
        }
    }
}
