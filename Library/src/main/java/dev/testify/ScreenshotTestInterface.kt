package dev.testify

import dev.testify.internal.TestifyConfigurationInterface

interface ScreenshotTestInterface {

    var screenshotViewProvider: ViewProvider?

    fun configure(configure: (TestifyConfigurationInterface) -> Unit): ScreenshotTestInterface

    fun setEspressoActions(espressoActions: EspressoActions): ScreenshotTestInterface
    fun setViewModifications(viewModification: ViewModification): ScreenshotTestInterface

    fun assertSame()

    fun isRecordMode(): Boolean
}
