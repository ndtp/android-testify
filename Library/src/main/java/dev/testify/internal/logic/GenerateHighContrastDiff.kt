package dev.testify.internal.logic

import android.content.Context
import android.graphics.Bitmap
import dev.testify.internal.TestifyConfiguration
import dev.testify.internal.helpers.outputFileName
import dev.testify.internal.processor.diff.HighContrastDiff

/**
 * Given [baselineBitmap] and [currentBitmap], use [HighContrastDiff] to write a companion .diff image for the
 * current test.
 *
 * This diff image is a high-contrast image where each difference, regardless of how minor, is indicated in red
 * against a black background.
 */
fun generateHighContrastDiff(
    testContext: Context,
    configuration: TestifyConfiguration,
    baselineBitmap: Bitmap,
    currentBitmap: Bitmap
) {
    HighContrastDiff(configuration.exclusionRects)
        .name(testContext.outputFileName())
        .baseline(baselineBitmap)
        .current(currentBitmap)
        .exactness(configuration.exactness)
        .generate(context = testContext) // TODO: This was originally 'context = activity'
}
