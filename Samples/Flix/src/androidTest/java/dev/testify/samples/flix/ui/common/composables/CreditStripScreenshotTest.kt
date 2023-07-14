package dev.testify.samples.flix.ui.common.composables

import dev.testify.ComposableScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.samples.flix.presentation.moviedetails.model.CreditPresentationModel
import dev.testify.samples.flix.ui.common.composeables.CreditStrip
import org.junit.Rule
import org.junit.Test

class CreditStripScreenshotTest {

    @get:Rule
    val rule = ComposableScreenshotRule()

    private val presentationModels = listOf(
        CreditPresentationModel(
            name = "Ving Rhames",
            characterName = "Marsellus Wallace",
            image = null
        ),
        CreditPresentationModel(
            name = "Marlon Brando",
            characterName = "Don Vito Corleone",
            image = null
        ),
        CreditPresentationModel(
            name = "Tom Hanks",
            characterName = "Paul Edgecomb",
            image = null
        ),
        CreditPresentationModel(
            name = "Clint Eastwood",
            characterName = "Blondie",
            image = null
        ),
        CreditPresentationModel(
            name = "Rumi Hiiragi",
            characterName = "Chihiro Ogino / Sen (voice)",
            image = null
        )
    )

    @Test
    @ScreenshotInstrumentation
    fun default() {
        rule.setCompose {
            CreditStrip(credits = presentationModels.take(3))
        }.assertSame()
    }

    @Test
    @ScreenshotInstrumentation
    fun longCreditStrip() {
        rule.setCompose {
            CreditStrip(credits = presentationModels)
        }.assertSame()
    }

    @Test
    @ScreenshotInstrumentation
    fun emptyCreditStrip() {
        rule.setCompose {
            CreditStrip(credits = emptyList())
        }.assertSame()
    }
}
