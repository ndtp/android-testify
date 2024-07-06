package dev.testify.samples.flix.ui.common.composables
//
//import dev.testify.ComposableScreenshotRule
//import dev.testify.annotation.ScreenshotInstrumentation
//import dev.testify.samples.flix.ui.common.composeables.OverviewText
//import org.junit.Rule
//import org.junit.Test
//
//class OverviewTextScreenshotTest {
//
//    @get:Rule
//    val rule = ComposableScreenshotRule()
//
//    @Test
//    @ScreenshotInstrumentation
//    fun default() {
//        rule.setCompose {
//            OverviewText(text = "In a near-future Britain, young Alexander DeLarge and his pals get their kicks beating and raping anyone they please. When not destroying the lives of others, Alex swoons to the music of Beethoven. The state, eager to crack down on juvenile crime, gives an incarcerated Alex the option to undergo an invasive procedure that'll rob him of all personal agency. In a time when conscience is a commodity, can Alex change his tune?")
//        }.assertSame()
//    }
//
//    @Test
//    @ScreenshotInstrumentation
//    fun longText() {
//        rule.setCompose {
//            OverviewText(text = "Newspaper magnate, Charles Foster Kane is taken from his mother as a boy and made the ward of a rich industrialist. As a result, every well-meaning, tyrannical or self-destructive move he makes for the rest of his life appears in some way to be a reaction to that deeply wounding event. Newspaper magnate, Charles Foster Kane is taken from his mother as a boy and made the ward of a rich industrialist. As a result, every well-meaning, tyrannical or self-destructive move he makes for the rest of his life appears in some way to be a reaction to that deeply wounding event.")
//        }.assertSame()
//    }
//}
