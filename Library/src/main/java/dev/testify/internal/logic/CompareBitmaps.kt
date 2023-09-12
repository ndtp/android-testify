package dev.testify.internal.logic

import android.graphics.Bitmap
import dev.testify.CompareMethod

/**
 * Compare [baselineBitmap] to [currentBitmap] using the provided [bitmapCompare] bitmap comparison method.
 *
 * The definition of "same" depends on the comparison method. The default implementation requires the bitmaps
 * to be identical at a binary level to be considered "the same".
 *
 * @return true if the bitmaps are considered the same.
 */
fun compareBitmaps(
    baselineBitmap: Bitmap,
    currentBitmap: Bitmap,
    bitmapCompare: CompareMethod
): Boolean =
    bitmapCompare(baselineBitmap, currentBitmap)
