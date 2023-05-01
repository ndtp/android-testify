---
title:  Customizing Screenshot Instrumentation Annotation
description:  Customizing Screenshot Instrumentation Annotation
slug: custom-annotations
authors:
  - name: Daniel Jette
    title: Core Contributor to Android Testify
    url: https://github.com/DanielJette
    image_url: https://github.com/DanielJette.png
tags: [annotations, customization]
hide_table_of_contents: false
---

# Introducing a New Testify Setting: Customizing Screenshot Instrumentation Annotation

Testify is a powerful Android plugin that provides a selection of utility tasks for advanced use cases in screenshot testing. With Testify, developers can easily create, compare, and report screenshots of their app across different devices, locales, and screen sizes.

In the latest release of Testify, we've added a new setting that allows developers to customize the instrumentation annotation used to identify which test is a screenshot test. This means that the @ScreenshotInstrumentation annotation is no longer a required element in your code.

## Why This Change?

The @ScreenshotInstrumentation annotation was previously required for Testify to identify screenshot tests. This annotation added an additional layer of complexity to the testing process and could cause confusion for developers who were new to Testify. By introducing the ability to customize the annotation, we hope to simplify the testing process and make Testify more accessible to a wider range of developers.

## How to Use the New Setting

To use the new Testify setting, developers can now add a `screenshotAnnotation` parameter to their build.gradle file. This parameter allows developers to specify the name of the annotation that they are using for their screenshot tests. Here's an example:

```gradle
testify {
  // Set the instrumentation annotation used for screenshot tests
  screenshotAnnotation = "com.example.MyScreenshotAnnotation"
}
```
In this example, we're setting the screenshotAnnotation parameter to "com.example.MyScreenshotAnnotation". This tells Testify to look for tests annotated with @com.example.MyScreenshotAnnotation to identify screenshot tests.

If you don't specify a screenshotAnnotation, Testify will use the default @ScreenshotInstrumentation annotation.

## Conclusion

By adding the ability to customize the instrumentation annotation used to identify screenshot tests, we've made Testify even more powerful and accessible to developers. We hope that this new setting will simplify the testing process and make it easier for more developers to use Testify in their Android apps.

If you have any questions or feedback on this new feature, please don't hesitate to reach out to us on our [GitHub](https://github.com/ndtp/android-testify/issues) page.
