# Accessibility Checks Overview

To help people with disabilities access Android apps, developers of those apps need to consider how their apps will be presented to accessibility services. Some good practices can be checked by automated tools, such as if a View has a contentDescription. Other rules require human judgment, such as whether or not a contentDescription makes sense to all users. Testify Accessibility can be used to verify common errors that lead to a poorly accessible application.

You can combine visual regression testing with accessibility checks to further improve the quality and expand the reach of your application.

Enabling `assertAccessibility` on `ScreenshotRule` will run the latest set of checks as defined by the [Accessibility Test Framework for Android](https://github.com/google/Accessibility-Test-Framework-for-Android). This library collects various accessibility-related checks on `View` objects as well as `AccessibilityNodeInfo` objects (which the Android framework derives from Views and sends to AccessibilityServices).

For more information about accessibility, see the [Accessibility guides](https://developer.android.com/guide/topics/ui/accessibility).
For more information about _Mobile Accessibility_, see: http://www.w3.org/WAI/mobile/
For more information about _Accessibility Checking_, please see https://developer.android.com/training/testing/espresso/accessibility-checking

:::info

Testify Accessibility is based on [Accessibility Test Framework for Android](https://github.com/google/Accessibility-Test-Framework-for-Android) which currently does not support Compose-based UI.

:::