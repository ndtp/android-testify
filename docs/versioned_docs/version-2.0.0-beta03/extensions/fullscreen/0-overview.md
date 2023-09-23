# Fullscreen Capture Method Overview

### Test your app as your user sees it

Taking a full-screen screenshot can provide a more comprehensive view of the user experience, including system UI elements such as notifications, status bar, and navigation buttons. A full-screen screenshot can help identify bugs that may be related to system UI elements or interactions with the operating system. A full-screen screenshot can help identify compatibility issues with different Android versions, screen sizes, and device manufacturers, including how the app interacts with system UI elements.

A full-screen screenshot can provide valuable insights into the app's behavior and help developers improve the app's performance, compatibility, and user experience.


### Capture the entire device screen, including system UI, dialogs and menus


The Testify Fullscreen Capture method can be used to capture UI elements presented outside of your root view. This includes elements rendered in a different [Window](https://developer.android.com/reference/android/view/Window) such as dialogs, alerts, notifications, or overlays.


### How it works

Testify Fullscreen Capture Method uses [UiAutomator's](https://developer.android.com/training/testing/other-components/ui-automator) built-in [screenshotting](https://developer.android.com/reference/androidx/test/uiautomator/UiDevice#takescreenshot) capability to capture a [Bitmap](https://developer.android.com/reference/android/graphics/Bitmap) of the entire device.

The bitmap will be generated from a PNG at 1:1 scale and 100% quality. The bitmap's size will match the full device resolution and include all system UI such as the status bar and navigation bar.

As the system UI content is highly variable, you can use [ScreenshotRule.excludeStatusBar](https://github.com/ndtp/android-testify/tree/main/Ext/Fullscreen/src/main/java/dev/testify/capture/fullscreen/provider/StatusBarExclusionRectProvider.kt) and/or [ScreenshotRule.excludeNavigationBar](https://github.com/ndtp/android-testify/tree/main/Ext/Fullscreen/src/main/java/dev/testify/capture/fullscreen/provider/NavigationBarExclusionRectProvider.kt) to ignore the status bar and navigation bar, respectively.

Though the PNG is intended to be lossless, some compression artifacts or GPU-related variance can occur. As such, it is recommended to use a small tolerance when capturing fullscreen images.

You can set a comparison tolerance using [TestifyConfiguration.exactness](https://github.com/ndtp/android-testify/blob/main/Library/src/main/java/dev/testify/internal/TestifyConfiguration.kt).
