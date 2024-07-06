package dev.testify.samples.flix.ui.common.composables
//
//import dev.testify.ComposableScreenshotRule
//import dev.testify.annotation.ScreenshotInstrumentation
//import dev.testify.samples.flix.ui.common.composeables.PrimaryTitle
//import org.junit.Rule
//import org.junit.Test
//
//class PrimaryTitleScreenshotTest {
//
//    @get:Rule
//    val rule = ComposableScreenshotRule()
//
//    @Test
//    @ScreenshotInstrumentation
//    fun default() {
//        rule.setCompose {
//            PrimaryTitle(title = "Citizen Kane")
//        }.assertSame()
//    }
//
//    @Test
//    @ScreenshotInstrumentation
//    fun longText() {
//        rule.setCompose {
//            PrimaryTitle(title = "'Night of the Day of the Dawn of the Son of the Bride of the Return of the Revenge of the Terror of the Attack of the Evil, Mutant, Hellbound, Flesh-Eating Subhumanoid Zombified Living Dead, Part 2: In Shocking 2-D' (1991)")
//        }.assertSame()
//    }
//}
