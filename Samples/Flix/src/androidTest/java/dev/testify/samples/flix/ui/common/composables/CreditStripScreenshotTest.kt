package dev.testify.samples.flix.ui.common.composables

import dev.testify.ComposableScreenshotRule
import dev.testify.accessibility.assertAccessibility
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.samples.flix.ui.common.composeables.CastMemberPresentationModel
import dev.testify.samples.flix.ui.common.composeables.CreditStrip
import org.junit.Rule
import org.junit.Test

class CreditStripScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    private val presentationModels = listOf(
        CastMemberPresentationModel(
            id = 1,
            name = "Ving Rhames",
            characterName = "Marsellus Wallace",
            image = null
        ),
        CastMemberPresentationModel(
            id = 2,
            name = "Marlon Brando",
            characterName = "Don Vito Corleone",
            image = null
        ),
        CastMemberPresentationModel(
            id = 3,
            name = "Tom Hanks",
            characterName = "Paul Edgecomb",
            image = null
        ),
        CastMemberPresentationModel(
            id = 4,
            name = "Clint Eastwood",
            characterName = "Blondie",
            image = null
        ),
        CastMemberPresentationModel(
            id = 5,
            name = "Rumi Hiiragi",
            characterName = "Chihiro Ogino / Sen (voice)",
            image = null
        )
    )

    @Test
    @ScreenshotInstrumentation
    fun default() {
        rule
            .setCompose {
                CreditStrip(credits = presentationModels.take(3)) {}
            }
            .assertAccessibility()
            .assertSame()
    }

    @Test
    @ScreenshotInstrumentation
    fun longCreditStrip() {
        rule
            .setCompose {
                CreditStrip(credits = presentationModels) {}
            }
            .assertAccessibility()
            .assertSame()
    }

    @Test
    @ScreenshotInstrumentation
    fun emptyCreditStrip() {
        rule
            .setCompose {
                CreditStrip(credits = emptyList()) {}
            }
            .assertAccessibility()
            .assertSame()
    }
}
