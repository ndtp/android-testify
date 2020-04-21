package com.shopify.testify

import android.view.View
import com.shopify.testify.annotation.ScreenshotInstrumentation
import com.shopify.testify.internal.exception.ViewModificationException
import org.junit.Rule
import org.junit.Test

class ViewModificationExceptionTest {

    @get:Rule val rule = ScreenshotRule(TestActivity::class.java)

    @ScreenshotInstrumentation
    @Test(expected = ViewModificationException::class)
    fun default() {
        rule
            .setViewModifications {
                it.getChildAt(100).visibility = View.GONE
            }
            .assertSame()
    }
}
