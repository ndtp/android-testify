package com.shopify.testify

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.shopify.testify.internal.compare.FuzzyCompare
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FuzzyCompareBitmapTest {

    @get:Rule var testActivityRule = ActivityTestRule(TestActivity::class.java)

    private val screenshotUtility = ScreenshotUtility()

    private fun loadBitmap(name: String): Bitmap {
        return screenshotUtility.loadBaselineBitmapForComparison(testActivityRule.activity, name)!!
    }

    @Test
    fun fuzzy() {
        val baselineBitmap = loadBitmap("test")

        val similarBitmap = baselineBitmap.copy(baselineBitmap.config, true)!!
        similarBitmap.setPixel(0, 0, similarBitmap.getPixel(0, 0) + 1)

        assertFalse(FuzzyCompare(1.0f).compareBitmaps(similarBitmap, baselineBitmap))
        assertTrue(FuzzyCompare(0.99f).compareBitmaps(similarBitmap, baselineBitmap))
    }

    @Test
    fun compare237to225() {
        val baselineBitmap = loadBitmap("Fuzzy_237")
        val currentBitmap = loadBitmap("Fuzzy_225")

        /*
         The majority of these images are solid Red237 and Red225. But, there's a small portion
         of the text whose black values are shifted slightly so that it varies from
         solid 0,0,0 black to fully-saturated, nearly black 0,1,0.004
         */
        assertTrue(FuzzyCompare(0.975f).compareBitmaps(baselineBitmap, currentBitmap))
    }
}
