package dev.testify

import android.graphics.Bitmap
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class Screenshot() {
    var bitmap: Bitmap? = null
    fun capture() {
    }
}

class IsSameAs(
    private val baseline: Screenshot
) : BaseMatcher<Screenshot>() {

    override fun describeTo(p0: Description?) {
    }

    override fun matches(p0: Any?): Boolean {
        return false
    }
}
