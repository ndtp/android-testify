package dev.testify.internal

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import dev.testify.internal.helpers.OrientationHelper
import dev.testify.internal.modification.FocusModification
import dev.testify.internal.modification.HideCursorViewModification
import dev.testify.internal.modification.HidePasswordViewModification
import dev.testify.internal.modification.HideScrollbarsViewModification
import dev.testify.internal.modification.HideTextSuggestionsViewModification
import dev.testify.internal.modification.SoftwareRenderViewModification
import dev.testify.internal.output.instrumentationPrintln
import org.junit.Assert
import java.util.Locale

typealias ExclusionRectProvider = (rootView: ViewGroup, exclusionRects: MutableSet<Rect>) -> Unit

interface TestifyConfigurationInterface {
    var hideSoftKeyboard: Boolean
    var exactness: Float?
    var fontScale: Float?
    var isLayoutInspectionModeEnabled: Boolean
    var locale: Locale?
    var exclusionRectProvider: ExclusionRectProvider?
    val exclusionRects: Set<Rect>
    val focusModification: FocusModification

    fun setHideScrollbars(hideScrollbars: Boolean)
    fun setHidePasswords(hidePasswords: Boolean)
    fun setHideCursor(hideCursor: Boolean)
    fun setHideTextSuggestions(hideTextSuggestions: Boolean)
    fun setUseSoftwareRenderer(useSoftwareRenderer: Boolean)
    fun setFocusTarget(enabled: Boolean = true, @IdRes focusTargetId: Int = android.R.id.content)
    fun hideSoftKeyboard()

    fun applyViewModifications(parentView: ViewGroup)

    /**
     * Allow the test to define a set of rectangles to exclude from the comparison.
     * Any pixels contained within the bounds of any of the provided Rects are ignored.
     * The provided callback is invoked after the layout is fully rendered and immediately before
     * the screenshot is captured.
     *
     * Note: This comparison method is significantly slower than the default.
     *
     * @param provider A callback of type ExclusionRectProvider
     */
    fun defineExclusionRects(provider: ExclusionRectProvider)
    fun hasExclusionRect(): Boolean = exclusionRects.isNotEmpty()
    fun resetExclusionRects()

    fun hasExactness() = exactness != null

    /**
     * Install an activity monitor and set the requested orientation.
     * Blocks and waits for the orientation change to complete before returning.
     *
     * @param requestedOrientation SCREEN_ORIENTATION_LANDSCAPE or SCREEN_ORIENTATION_PORTRAIT
     */
    fun setOrientation(requestedOrientation: Int)
}

data class TestifyConfiguration(
    override var hideSoftKeyboard: Boolean = true,
    override var isLayoutInspectionModeEnabled: Boolean = false,
    override var locale: Locale? = null,
    override var exclusionRectProvider: ExclusionRectProvider? = null,
    override val exclusionRects: MutableSet<Rect> = HashSet(),
    override val focusModification: FocusModification = FocusModification()
) : TestifyConfigurationInterface {

    private val hideCursorViewModification = HideCursorViewModification()
    private val hidePasswordViewModification = HidePasswordViewModification()
    private val hideScrollbarsViewModification = HideScrollbarsViewModification()
    private val hideTextSuggestionsViewModification = HideTextSuggestionsViewModification()
    private val softwareRenderViewModification = SoftwareRenderViewModification()

    internal var orientationHelper = OrientationHelper()
    val deviceOrientation: Int
        get() = orientationHelper.deviceOrientation
    internal var orientationToIgnore: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    override var exactness: Float? = null
        set(value) {
            require(value == null || value in 0.0..1.0)
            field = value
        }

    override fun setHideScrollbars(hideScrollbars: Boolean) {
        this.hideScrollbarsViewModification.isEnabled = hideScrollbars
    }

    override fun setHidePasswords(hidePasswords: Boolean) {
        this.hidePasswordViewModification.isEnabled = hidePasswords
    }

    override fun setHideCursor(hideCursor: Boolean) {
        this.hideCursorViewModification.isEnabled = hideCursor
    }

    override fun setHideTextSuggestions(hideTextSuggestions: Boolean) {
        this.hideTextSuggestionsViewModification.isEnabled = hideTextSuggestions
    }

    override fun setUseSoftwareRenderer(useSoftwareRenderer: Boolean) {
        this.softwareRenderViewModification.isEnabled = useSoftwareRenderer
    }

    override fun hideSoftKeyboard() {
        if (hideSoftKeyboard) {
            Espresso.closeSoftKeyboard()
        }
    }

    /**
     * Allows Testify to deliberately set the keyboard focus to the specified view
     *
     * @param enabled when true, removes focus from all views in the activity
     * @param focusTargetId the View ID to set focus on
     */
    override fun setFocusTarget(enabled: Boolean, @IdRes focusTargetId: Int) {
        focusModification.isEnabled = enabled
        focusModification.focusTargetId = focusTargetId
    }

    override var fontScale: Float? = null

    @CallSuper
    override fun applyViewModifications(parentView: ViewGroup) {
        hideScrollbarsViewModification.modify(parentView)
        hideTextSuggestionsViewModification.modify(parentView)
        hidePasswordViewModification.modify(parentView)
        softwareRenderViewModification.modify(parentView)
        hideCursorViewModification.modify(parentView)
    }

    override fun defineExclusionRects(provider: ExclusionRectProvider) {
        this.exclusionRectProvider = provider
    }

    fun applyExclusionRects(rootView: ViewGroup) {
        exclusionRectProvider?.let { provider ->
            provider(rootView, exclusionRects)
        }
    }

    override fun hasExclusionRect() = exclusionRects.isNotEmpty()
    override fun resetExclusionRects() = exclusionRects.clear()

    override fun setOrientation(requestedOrientation: Int) {
        require(requestedOrientation in ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE..ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        this.orientationHelper.requestedOrientation = requestedOrientation
    }

    fun assertOrientation(activity: Activity, testName: String, outputFileExists: Boolean): Boolean {
        if (orientationHelper.shouldIgnoreOrientation(activity, orientationToIgnore)) {
            val orientationName =
                if (orientationToIgnore == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) "Portrait" else "Landscape"
            instrumentationPrintln(
                "\n\t✓ " + 27.toChar() + "[33mIgnoring baseline for " + testName +
                    " due to $orientationName orientation" + 27.toChar() + "[0m"
            )
            Assert.assertFalse(
                "Output file should not exist for $orientationName orientation",
                outputFileExists
            )
            return false
        }
        return true
    }

    fun afterActivityLaunched(activity: Activity) {
        orientationHelper.afterActivityLaunched(activity)
    }
}
