package dev.testify.samples.flix.ui.common.composables
//
//import dev.testify.ComposableScreenshotRule
//import dev.testify.annotation.ScreenshotInstrumentation
//import dev.testify.samples.flix.ui.common.composeables.SecondaryTitle
//import org.junit.Rule
//import org.junit.Test
//
//class SecondaryTitleScreenshotTest {
//
//    @get:Rule
//    val rule = ComposableScreenshotRule()
//
//    @Test
//    @ScreenshotInstrumentation
//    fun default() {
//        rule.setCompose {
//            // Chicken Run
//            SecondaryTitle(text = "Escape or die frying.")
//        }.assertSame()
//    }
//
//    @Test
//    @ScreenshotInstrumentation
//    fun longText() {
//        rule.setCompose {
//            // Taxi Driver
//            SecondaryTitle(text = "On every street in every city, there's a nobody who dreams of being a somebody. On every street in every city, there's a nobody who dreams of being a somebody")
//        }.assertSame()
//    }
//}
