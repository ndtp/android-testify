package dev.testify.samples

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.LocalAsyncImagePreviewHandler
import com.android.tools.screenshot.PreviewTest
import dev.testify.samples.flix.data.model.FlixPerson
import dev.testify.samples.flix.ui.cast.CastDetailState.Loaded
import dev.testify.samples.flix.ui.cast.CastDetailView
import dev.testify.samples.flix.ui.common.createPreviewHandler
import dev.testify.samples.flix.ui.common.util.imagePromise

@PreviewTest
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Text("Hello, world!")
}


@PreviewTest
@Preview(apiLevel = 33, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CastDetailLoadedPreview() {
    val context = LocalContext.current // Get the context
    val previewHandler = createPreviewHandler(context)
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        Box(modifier = Modifier.fillMaxSize()) {
            CastDetailView(
                state = Loaded(
                    FlixPerson(
                        id = 0,
                        name = "Isaac Newton",
                        popularity = 92.4f,
                        knownFor = "Newtonian mechanics, universal gravitation, calculus",
                        biography = "Sir Isaac Newton FRS (25 December 1642 – 20 March 1726/27) was an English" +
                            " polymath active as a mathematician, physicist, astronomer, alchemist, theologian, " +
                            "and author who was described in his time as a natural philosopher. He was a key " +
                            "figure in the Scientific Revolution and the Enlightenment that followed. " +
                            "His pioneering book Philosophiæ Naturalis Principia Mathematica (Mathematical " +
                            "Principles of Natural Philosophy), first published in 1687, consolidated many " +
                            "previous results and established classical mechanics. Newton also made seminal " +
                            "contributions to optics, and shares credit with German mathematician " +
                            "Gottfried Wilhelm Leibniz for developing infinitesimal calculus, though he " +
                            "developed calculus years before Leibniz. He is considered one of the greatest and " +
                            "most influential scientists in history." +
                            "• Isaac Newton. In Wikipedia. Retrieved Dec 3, 2023, from https://en.wikipedia.org/wiki/Isaac_Newton",
                        placeOfBirth = "Woolsthorpe-by-Colsterworth, Lincolnshire, England",
                        birthday = "4 January 1643",
                        deathday = "31 March 1727",
                        image = imagePromise("file:///android_asset/images/headshots/Newton.png")
                    )
                )
            )
        }
    }
}
