---
sidebar_position: 1
---

# Android Screenshot Testing

## Testify

Expand your test coverage by including the View layer. Testify allows you to easily set up a variety of screenshot tests in your application. Capturing a screenshot of your view gives you a new tool for monitoring the quality of your UI experience. It's also an easy way to review changes to your UI. Once you've established a comprehensive set of screenshots for your application, you can use them as a "visual dictionary". In this case, a picture really is worth a thousand words; it's easy to catch unintended changes in your view rendering by watching for differences in your captured images.

Testify screenshot tests are built on top of [Android Instrumentation tests](https://developer.android.com/training/testing/unit-testing/instrumented-unit-tests) and so integrate seamlessly with your existing test suites. You can run tests and capture screenshots from within Android Studio or using the Gradle command-line tools. Testify also works well with most Continuous Integration services. 

You can easily capture screenshots with different resolutions, orientations, API versions and languages by simply configuring different emulators. Testify natively supports grouping screenshot tests by device characteristics. Testify captures a bitmap of your specified View after all layout and draw calls have completed so you know that you're capturing an authentic rendering representative of what your users will see in your final product.