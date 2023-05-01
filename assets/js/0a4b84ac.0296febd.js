"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[2774],{6261:e=>{e.exports=JSON.parse('{"blogPosts":[{"id":"custom-annotations","metadata":{"permalink":"/android-testify/blog/custom-annotations","editUrl":"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/blog/2023-04-25-custom-annotations.md","source":"@site/blog/2023-04-25-custom-annotations.md","title":"Customizing Screenshot Instrumentation Annotation","description":"Customizing Screenshot Instrumentation Annotation","date":"2023-04-25T00:00:00.000Z","formattedDate":"April 25, 2023","tags":[{"label":"annotations","permalink":"/android-testify/blog/tags/annotations"},{"label":"customization","permalink":"/android-testify/blog/tags/customization"}],"readingTime":1.61,"hasTruncateMarker":false,"authors":[{"name":"Daniel Jette","title":"Core Contributor to Android Testify","url":"https://github.com/DanielJette","image_url":"https://github.com/DanielJette.png","imageURL":"https://github.com/DanielJette.png"}],"frontMatter":{"title":"Customizing Screenshot Instrumentation Annotation","description":"Customizing Screenshot Instrumentation Annotation","slug":"custom-annotations","authors":[{"name":"Daniel Jette","title":"Core Contributor to Android Testify","url":"https://github.com/DanielJette","image_url":"https://github.com/DanielJette.png","imageURL":"https://github.com/DanielJette.png"}],"tags":["annotations","customization"],"hide_table_of_contents":false},"nextItem":{"title":"Accounting for platform differences","permalink":"/android-testify/blog/platform-differences"}},"content":"Testify is a powerful Android plugin that provides a selection of utility tasks for advanced use cases in screenshot testing. With Testify, developers can easily create, compare, and report screenshots of their app across different devices, locales, and screen sizes.\\n\\nIn the latest release of Testify, we\'ve added a new setting that allows developers to customize the instrumentation annotation used to identify which test is a screenshot test. This means that the @ScreenshotInstrumentation annotation is no longer a required element in your code.\\n\\n## Why This Change?\\n\\nThe @ScreenshotInstrumentation annotation was previously required for Testify to identify screenshot tests. This annotation added an additional layer of complexity to the testing process and could cause confusion for developers who were new to Testify. By introducing the ability to customize the annotation, we hope to simplify the testing process and make Testify more accessible to a wider range of developers.\\n\\n## How to Use the New Setting\\n\\nTo use the new Testify setting, developers can now add a `screenshotAnnotation` parameter to their build.gradle file. This parameter allows developers to specify the name of the annotation that they are using for their screenshot tests. Here\'s an example:\\n\\n```gradle\\ntestify {\\n  // Set the instrumentation annotation used for screenshot tests\\n  screenshotAnnotation = \\"com.example.MyScreenshotAnnotation\\"\\n}\\n```\\nIn this example, we\'re setting the screenshotAnnotation parameter to \\"com.example.MyScreenshotAnnotation\\". This tells Testify to look for tests annotated with @com.example.MyScreenshotAnnotation to identify screenshot tests.\\n\\nIf you don\'t specify a screenshotAnnotation, Testify will use the default @ScreenshotInstrumentation annotation.\\n\\n## Conclusion\\n\\nBy adding the ability to customize the instrumentation annotation used to identify screenshot tests, we\'ve made Testify even more powerful and accessible to developers. We hope that this new setting will simplify the testing process and make it easier for more developers to use Testify in their Android apps.\\n\\nIf you have any questions or feedback on this new feature, please don\'t hesitate to reach out to us on our [GitHub](https://github.com/ndtp/android-testify/issues) page."},{"id":"platform-differences","metadata":{"permalink":"/android-testify/blog/platform-differences","editUrl":"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/blog/2023-02-13-platform-differences.md","source":"@site/blog/2023-02-13-platform-differences.md","title":"Accounting for platform differences","description":"This is my first post on Docusaurus 2.","date":"2023-02-13T00:00:00.000Z","formattedDate":"February 13, 2023","tags":[{"label":"differences","permalink":"/android-testify/blog/tags/differences"},{"label":"customization","permalink":"/android-testify/blog/tags/customization"}],"readingTime":6.9,"hasTruncateMarker":true,"authors":[{"name":"Daniel Jette","title":"Core Contributor to Android Testify","url":"https://github.com/DanielJette","image_url":"https://github.com/DanielJette.png","imageURL":"https://github.com/DanielJette.png"}],"frontMatter":{"title":"Accounting for platform differences","description":"This is my first post on Docusaurus 2.","slug":"platform-differences","authors":[{"name":"Daniel Jette","title":"Core Contributor to Android Testify","url":"https://github.com/DanielJette","image_url":"https://github.com/DanielJette.png","imageURL":"https://github.com/DanielJette.png"}],"tags":["differences","customization"],"hide_table_of_contents":false},"prevItem":{"title":"Customizing Screenshot Instrumentation Annotation","permalink":"/android-testify/blog/custom-annotations"}},"content":"export const Swatch = ({children, color}) => (\\n  <span\\n    style={{\\n      backgroundColor: color,\\n      borderRadius: \'2px\',\\n      color: \'#fff\',\\n      padding: \'0.2rem\'\\n    }}>\\n    {children}\\n  </span>\\n);\\n\\nA common problem when using Testify is having to deal with undesirable differences in your screenshots caused by variations in platforms or hardware. This blog will explain how to configure Testify to account for these differences.\\n\\n\x3c!--truncate--\x3e\\n\\n---\\n\\nMuch of the UI in Android is rendered with hardware acceleration. This means that both the GPU and CPU used by your test device will impact the rendering of your screenshots. Commonly, this is caused by subtle differences in floating point rounding.\\n\\nThe elements most frequently impacted by hardware accelerated rendering are:\\n\\n1. [Shadows & elevation](https://m1.material.io/material-design/elevation-shadows.html)\\n2. [Font smoothing & anti-aliasing](https://medium.com/@ali.muzaffar/android-why-your-canvas-shapes-arent-smooth-aa2a3f450eb5)\\n3. [Image decompression and rendering](https://en.wikipedia.org/wiki/Compression_artifact)\\n4. [Alpha blending, or alpha compositing](https://en.wikipedia.org/wiki/Alpha_compositing)\\n\\n## Background\\n\\nLet\u2019s walk through a brief example that explains how these small hardware differences can impact your captured screenshots.\\n\\nIn this example, our UI uses two ARGB colors which each have some transparency. These colors are defined as **ARGB(128,189,85,85)**<Swatch color=\\"#deaaaa\\">&nbsp;&nbsp;&nbsp;&nbsp;</Swatch> and **ARGB(128,0,161,254)**<Swatch color=\\"#7fd0fe\\">&nbsp;&nbsp;&nbsp;&nbsp;</Swatch>.\\n\\nIn hardware, those would be represented as floating point numbers: **(0.502f,&nbsp;0.741f,&nbsp;0.333f,&nbsp;0.333f)** and **(0.502f,&nbsp;0f,&nbsp;0.631f,&nbsp;0.996f)** respectively.\\n\\nIf we stack these two swatches on top of a pure white background, the GPU will composite the two colors together in such a way that they appear like the following opaque color in the UI: <br/>\\n**(1.0f,&nbsp;0.619f,&nbsp;0.572f,&nbsp;0.666f)**<Swatch color=\\"#9a92aa\\">&nbsp;&nbsp;&nbsp;&nbsp;</Swatch>.\\n\\nWhen capturing a screenshot, the onscreen image is saved to a bitmap file. Testify uses PNG files to store these images. During the capture process, the pixels are translated from the hardware representation to an integer representation by multiplying the floating point value by 255. Depending on many factors, the exact transformation that takes place can result in minor variations of the same values.\\n\\n\\n- `0.619f \xd7 255` might be rounded to 158 or truncated to 157\\n- `0.572f \xd7 255` might be rounded to 146 or truncated to 145\\n- `0.666f x 255` might be rounded to 170 or truncated to 169\\n\\nThis is generally regarded as a harmless or insignificant since the difference in the resulting colors will be imperceptible to the human eye.\\n\\n![](157145169.png) or ![](158146170.png)\\n\\nYes these are different!\\n\\nThis technique of blending two colors together is common in Android UI and so is a cause of many unintentional differences in your screenshot tests.\\n\\nIf you look at the hex representation of these two colors you can see that, mathematically speaking, these numbers are quite different from one another:\\n\\n`0x9D91A9` vs. `0x9E92AA`\\n\\nThis can cause significant problems for screenshot tests.\\n\\nA bit-based comparison will immediately fail due to the numbers not being identical.\\nEven a tolerance-based comparison will likely fail as the difference between the two numbers is `0x10101`, or 65,793!\\n\\n\\n## A common problem\\n\\nThese types of hardware rounding differences are very common. A variety of configuration options can impact the captured Testify images. The CPU (M1 or x86), the GPU (discrete or integrated), emulator image (arm64, x86 or x86_64) and emulated performance settings (number of CPU cores, emulated Graphics \u2013 Hardware or Software GLES 1.1 or GLES 2.0) will all impact the mathematical calculations that take part of the rendering and capture of any bitmaps by Testify.\\n\\nIn an ideal world, you would ensure that all devices in your testing environment are configured to use identical hardware and software settings. This would be the easiest way to avoid minor baseline image differences due to hardware variations.\\n\\nHowever, it is extremely common for teams to have a variety of hardware configurations in use and it can be impossible to standardize on any one specific configration. Many large teams employ a large variety of development hardware, and the hardware configuration of many Continuous Integration Services offer limited or no control over their hardware.\\n\\nIn fact, this is so common that Testify has built-in accomodations for such differences.\\n\\n## Capture screenshots at the highest quality\\n\\nBefore we can deal with these rendering irregularities, we need to first ensure we\'re capturing high-quality data. We need to set up the screenshot tests to capture the highest-quality screenshots possible.\\n\\n### Enable PixelCopyCapture\\n\\n[`PixelCopy`](https://developer.android.com/reference/android/view/PixelCopy) is a method provided by the Android SDK since API 24 which will faithfully capture the entire `Surface` presented in the UI to a `Bitmap`. This includes elevation, shadows and other GPU-accelerated rendering features.\\n\\nFor best results, it is recommended that you enable the `PixelCopyCapture` method in Testify.\\n\\nThere are three ways you can enable `PixelCopyCapture`:\\n\\n#### In your project manifest. \\n\\nYou can enable `PixelCopyCapture` in your `AndroidManifest.xml`. This way, this capture method will be used for all tests in your entire project.\\n\\n```xml\\n<manifest package=\\"dev.testify.sample\\"\\n  xmlns:android=\\"http://schemas.android.com/apk/res/android\\">\\n  <application>\\n    <meta-data android:name=\\"testify-pixelcopy-capture\\" android:value=\\"true\\" />\\n  </application>\\n</manifest>\\n```\\n#### In your test class\\n\\nYou can enable `PixelCopyCapture` for your test class by enabling the `PixelCopyCapture` feature flag in a `@Before` block. With this method, you will enable the `PixelCopyCapture` for every test method in your class.\\n\\n```kotlin\\n@Before\\nfun before() {\\n  TestifyFeatures.PixelCopyCapture.setEnabled(true)\\n}\\n```\\n\\n#### On your test rule\\n\\nYou can enable `PixelCopyCapture` on a case-by-case basis with the `withExperimentalFeatureEnabled()` modifier on `ScreenshotTestRule`.\\n\\n```kotlin\\n@get:Rule val rule = ScreenshotRule(MainActivity::class.java)\\n\\n@ScreenshotInstrumentation\\n@Test\\nfun testDefault() {\\n  rule\\n    .withExperimentalFeatureEnabled(TestifyFeatures.PixelCopyCapture)\\n    .assertSame()\\n}\\n```\\n\\n## Ignore unimportant differences\\n\\n\\nIn order to account for the large integer differences that can exist between two visually similar pixels, we need to employ some kind of smart tolerance or heuristic to intelligently exclude irrelevant differences yet still fail on meaningful changes to the UI.\\n\\nEven though the number variance may be large, the human eye may not be able to perceive the difference in color. Thankfully, there are multiple algorithms that can tell us the distinction between an unimportant or important changes.\\n\\n> _In color science, **color difference** or **color distance** is the separation between two colors. This metric allows quantified examination of a notion that formerly could only be described with adjectives._\\n> https://en.wikipedia.org/wiki/Color_difference#CIELAB_%CE%94E*\\n\\nOne such color distance algorithm is _Delta E_.\\n\\nThe Delta E algorithm can mathematically quantify the similarity between two different colors. It allows us to ignore the differences between two pixels that the human eye would consider identical while still identifying differences in position, size or layout. Uing Delta E when comparing two images provides a reasonable allowance for small differences in the rendered result that most of us would consider as being \u201cthe same\u201d.\\n\\nTestify has an optional implementation of the Delta E color distance algorithm which can be enabled with the `setExactness()` modifier.\\n\\n### Enable Delta E comparison with an exactness value of less than 1\\n\\nYou can enable the Delta E comparison method on the Testify rule using the [`setExactness()`](http://localhost:3000/android-testify/docs/recipes/tolerance) method.\\n\\nAny value less than `1.0f` will enable the Delta E comparison method. \\n\\nA value of `0.0f` will instruct the comparison method to ignore all differences, no matter how significant. A value of between `0.7f` and `0.9f` will generally be the \\"sweet spot\\" that will ignore the majority of minor rendering variations while still failing on significant layout changes. You can experiment with your tests to find which value is best for your environment. I\'d recommend starting with `0.9f` and reducing the value in increments of `0.05f` until all of your tests pass.\\n\\n:bookmark: The Delta E comparison implementation uses a coroutine worker pool to efficiently parallelize the comparison operation. As such, it is highly recommend that you enable Multi-Core CPU features on your test target emulator.\\n\\n#### Before each test\\n\\nYou can enable the Delta E comparison for all tests in a test class by enabling it on the `ScreenshotRule` in a `@Before` method.\\n\\n```kotlin\\n@get:Rule val rule = ScreenshotRule(MainActivity::class.java)\\n\\n@Before\\nfun before() {\\n  rule.setExactness(0.8f)\\n}\\n```\\n\\n#### For one test case\\n\\nYou can enable the Delta E comparison for a single test by enabling it on the `ScreenshotRule` in the body of the test method, prior to invoking `assertSame()`.\\n\\n```kotlin\\n@get:Rule val rule = ScreenshotRule(MainActivity::class.java)\\n\\n@ScreenshotInstrumentation\\n@Test\\nfun testDefault() {\\n  rule\\n    .setExactness(0.8f)\\n    .assertSame()\\n}\\n```\\n\\n## Conclusion\\n\\nA test failure should provide you with important, actionable information. Unimportant rendering differences should not cause your tests to fail. By using `PixelCopyCapture` with a tolerance of around `0.9f`, you can safely ignore unimportant differences in your screenshots caused by variations on your testing hardware. This allows you to work more efficiently with others and focus on the visual quality of your application."}]}')}}]);