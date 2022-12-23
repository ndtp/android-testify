"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[53],{1109:e=>{e.exports=JSON.parse('{"pluginId":"default","version":"current","label":"2.0.0-alpha02","banner":null,"badge":true,"noIndex":false,"className":"docs-version-current","isLast":true,"docsSidebars":{"tutorialSidebar":[{"type":"link","label":"Android Screenshot Testing","href":"/android-testify/docs/intro","docId":"intro"},{"type":"category","label":"Get Started","collapsible":true,"collapsed":true,"items":[{"type":"link","label":"Set up an Android application project to use Testify","href":"/android-testify/docs/get-started/setup","docId":"get-started/setup"},{"type":"link","label":"Configure your emulator to run Testify tests","href":"/android-testify/docs/get-started/configuring-an-emulator","docId":"get-started/configuring-an-emulator"},{"type":"link","label":"Write a test","href":"/android-testify/docs/get-started/write-a-test","docId":"get-started/write-a-test"},{"type":"link","label":"Update your baseline","href":"/android-testify/docs/get-started/update-baseline","docId":"get-started/update-baseline"},{"type":"link","label":"Verify the tests","href":"/android-testify/docs/get-started/verify-tests","docId":"get-started/verify-tests"},{"type":"link","label":"Install and use the Android Studio Plugin","href":"/android-testify/docs/get-started/set-up-intellij-plugin","docId":"get-started/set-up-intellij-plugin"}],"href":"/android-testify/docs/category/get-started"},{"type":"category","label":"Extensions","collapsible":true,"collapsed":true,"items":[{"type":"category","label":"Jetpack Compose","collapsible":true,"collapsed":true,"items":[{"type":"link","label":"Set up testify-compose","href":"/android-testify/docs/extensions/compose/setup","docId":"extensions/compose/setup"},{"type":"link","label":"Write a test","href":"/android-testify/docs/extensions/compose/test","docId":"extensions/compose/test"},{"type":"link","label":"Using Testify with a ComposeTestRule","href":"/android-testify/docs/extensions/compose/ComposeTestRule","docId":"extensions/compose/ComposeTestRule"},{"type":"link","label":"Changing the font scale","href":"/android-testify/docs/extensions/compose/FontScale","docId":"extensions/compose/FontScale"},{"type":"link","label":"Changing the locale","href":"/android-testify/docs/extensions/compose/Locale","docId":"extensions/compose/Locale"}],"href":"/android-testify/docs/category/jetpack-compose"}],"href":"/android-testify/docs/category/extensions"},{"type":"category","label":"Recipes","collapsible":true,"collapsed":true,"items":[{"type":"link","label":"Taking a screenshot of an area less than that of the entire Activity","href":"/android-testify/docs/recipes/view-provider","docId":"recipes/view-provider"},{"type":"link","label":"Changing the Locale in a test","href":"/android-testify/docs/recipes/locale","docId":"recipes/locale"},{"type":"link","label":"Changing the font scale in a test","href":"/android-testify/docs/recipes/font-scale","docId":"recipes/font-scale"},{"type":"link","label":"Increase the matching tolerance","href":"/android-testify/docs/recipes/tolerance","docId":"recipes/tolerance"},{"type":"link","label":"Using @TestifyLayout with library projects","href":"/android-testify/docs/recipes/testify-layout-library","docId":"recipes/testify-layout-library"},{"type":"link","label":"Passing Intent extras to the Activity under test","href":"/android-testify/docs/recipes/intents","docId":"recipes/intents"},{"type":"link","label":"Specifying a layout resource programmatically","href":"/android-testify/docs/recipes/layout-resource","docId":"recipes/layout-resource"},{"type":"link","label":"Use Espresso UI tests with Testify","href":"/android-testify/docs/recipes/espresso","docId":"recipes/espresso"},{"type":"link","label":"Writing a test in Java","href":"/android-testify/docs/recipes/java","docId":"recipes/java"},{"type":"link","label":"Changing the orientation of the screen","href":"/android-testify/docs/recipes/orientation","docId":"recipes/orientation"},{"type":"link","label":"Debugging with the Layout Inspector","href":"/android-testify/docs/recipes/layout-inspector","docId":"recipes/layout-inspector"},{"type":"link","label":"Selecting an alternative capture method","href":"/android-testify/docs/recipes/capture-method","docId":"recipes/capture-method"},{"type":"link","label":"Force software rendering","href":"/android-testify/docs/recipes/software-rendering","docId":"recipes/software-rendering"},{"type":"link","label":"Excluding a region from the comparison","href":"/android-testify/docs/recipes/exclude-regions","docId":"recipes/exclude-regions"},{"type":"link","label":"Placing the keyboard focus on a specific view","href":"/android-testify/docs/recipes/keyboard-focus","docId":"recipes/keyboard-focus"},{"type":"link","label":"Customizing the captured bitmap","href":"/android-testify/docs/recipes/custom-bitmap","docId":"recipes/custom-bitmap"},{"type":"link","label":"Providing a custom comparison method","href":"/android-testify/docs/recipes/custom-capture","docId":"recipes/custom-capture"}],"href":"/android-testify/docs/category/recipes"},{"type":"link","label":"Migrate to Testify 2.0","href":"/android-testify/docs/migration","docId":"migration"}]},"docs":{"extensions/compose/ComposeTestRule":{"id":"extensions/compose/ComposeTestRule","title":"Using Testify with a ComposeTestRule","description":"A ComposeTestRule is a TestRule that allows you to test and control composables and applications using Compose.","sidebar":"tutorialSidebar"},"extensions/compose/FontScale":{"id":"extensions/compose/FontScale","title":"Changing the font scale","description":"ComposableScreenshotRule allows you to change the current Activity scaling factor for fonts, relative to the base density scaling. This allows you to simulate the impact of a user modifying the default font size on their device, such as tiny, large or huge.","sidebar":"tutorialSidebar"},"extensions/compose/Locale":{"id":"extensions/compose/Locale","title":"Changing the locale","description":"It is often desirable to test your Activity in multiple locales. ComposableScreenshotRule allows you to dynamically change the locale on a per-test basis.","sidebar":"tutorialSidebar"},"extensions/compose/setup":{"id":"extensions/compose/setup","title":"Set up testify-compose","description":"Root build.gradle","sidebar":"tutorialSidebar"},"extensions/compose/test":{"id":"extensions/compose/test","title":"Write a test","description":"In order to test a @Composable function, you must first declare an instance variable of the ComposableScreenshotRule class. Then, you can invoke the setCompose() method on the rule instance and declare any Compose UI functions you wish to test.","sidebar":"tutorialSidebar"},"get-started/configuring-an-emulator":{"id":"get-started/configuring-an-emulator","title":"Configure your emulator to run Testify tests","description":"The Sample application includes a baseline for an emulator that\'s compatible with GitHub Actions. To configure an AVD locally, create a new virtual device with the following settings in the Android Virtual Device (AVD) configuration:","sidebar":"tutorialSidebar"},"get-started/set-up-intellij-plugin":{"id":"get-started/set-up-intellij-plugin","title":"Install and use the Android Studio Plugin","description":"Testify screenshot tests are built on top of Android Instrumentation tests and so already integrate seamlessly with existing test suites. Screenshots can be captured directly from within Android Studio or using the Gradle command-line tools.","sidebar":"tutorialSidebar"},"get-started/setup":{"id":"get-started/setup","title":"Set up an Android application project to use Testify","description":"Before building your screenshot test with Testify, make sure to set a dependency reference to the Testify plugin:","sidebar":"tutorialSidebar"},"get-started/update-baseline":{"id":"get-started/update-baseline","title":"Update your baseline","description":"Testify works by referencing a PNG baseline found in your androidTest/assets directory for each test case that you write. As you write and run tests, an updated baseline image is maintained on your device or emulator. In order to update the baseline, you need to copy or pull the image from the device to your local development environment. Testify offers a variety of Gradle tasks to simplify the copying of your baseline images.","sidebar":"tutorialSidebar"},"get-started/verify-tests":{"id":"get-started/verify-tests","title":"Verify the tests","description":"Run all the screenshot tests in your app and fail if any differences from the baseline are detected.","sidebar":"tutorialSidebar"},"get-started/write-a-test":{"id":"get-started/write-a-test","title":"Write a test","description":"Testify is a subclass of Android\'s ActivityTestRule. The testing framework launches the activity under test before each test method annotated with @Test and before any method annotated with @Before.","sidebar":"tutorialSidebar"},"intro":{"id":"intro","title":"Android Screenshot Testing","description":"Testify","sidebar":"tutorialSidebar"},"migration":{"id":"migration","title":"Migrate to Testify 2.0","description":"dev.testify replaces the original classes in the com.shopify.testify namespace. Only the package and Maven artifact names changed; class, method, and field names did not change.","sidebar":"tutorialSidebar"},"recipes/capture-method":{"id":"recipes/capture-method","title":"Selecting an alternative capture method","description":"Testify provides three bitmap capture method. Each method will capture slightly different results based primarily on API level.","sidebar":"tutorialSidebar"},"recipes/custom-bitmap":{"id":"recipes/custom-bitmap","title":"Customizing the captured bitmap","description":"Testify provides the setCaptureMethod() on ScreenshotRule which can be used to override the default mechanism for creating a bitmap","sidebar":"tutorialSidebar"},"recipes/custom-capture":{"id":"recipes/custom-capture","title":"Providing a custom comparison method","description":"You can customize the algorithm used to compare the captured bitmap against the baseline by using the setCompareMethod() on ScreenshotRule.","sidebar":"tutorialSidebar"},"recipes/espresso":{"id":"recipes/espresso","title":"Use Espresso UI tests with Testify","description":"ScreenshotRule.setEspressoActions accepts a lambda of type EspressoActions in which you may define any number of Espresso actions. These actions are executed after the activity is fully inflated and any view modifications have been applied.","sidebar":"tutorialSidebar"},"recipes/exclude-regions":{"id":"recipes/exclude-regions","title":"Excluding a region from the comparison","description":"For some Views, it may be impossible to guarantee a stable, consistent rendering. For instance, if the content is dynamic or randomized.","sidebar":"tutorialSidebar"},"recipes/font-scale":{"id":"recipes/font-scale","title":"Changing the font scale in a test","description":"Testify allows you to change the current Activity scaling factor for fonts, relative to the base density scaling. This allows you to simulate the impact of a user modifying the default font size on their device, such as tiny, large or huge.","sidebar":"tutorialSidebar"},"recipes/intents":{"id":"recipes/intents","title":"Passing Intent extras to the Activity under test","description":"Some activities may require a Bundle of additional information called extras. Extras can be used to provide extended information to the component. For example, if we have a action to send an e-mail message, we could also include extra pieces of data here to supply a subject, body, etc.","sidebar":"tutorialSidebar"},"recipes/java":{"id":"recipes/java","title":"Writing a test in Java","description":"","sidebar":"tutorialSidebar"},"recipes/keyboard-focus":{"id":"recipes/keyboard-focus","title":"Placing the keyboard focus on a specific view","description":"It\'s possible that users can navigate your app using a keyboard, because the Android system enables most of the necessary behaviors by default.","sidebar":"tutorialSidebar"},"recipes/layout-inspector":{"id":"recipes/layout-inspector","title":"Debugging with the Layout Inspector","description":"You may use Android Studio\'s Layout Inspector in conjunction with your screenshot test. It can sometimes be useful to pause your test so that you can capture the layout hierarchy for further debugging in Android Studio. In order to do so, invoke the setLayoutInspectionModeEnabled method on the test rule. This will pause the test after all ViewModifications have been applied and prior to the screenshot being taken. The test is paused for 5 minutes, allowing plenty of time to capture the layout.","sidebar":"tutorialSidebar"},"recipes/layout-resource":{"id":"recipes/layout-resource","title":"Specifying a layout resource programmatically","description":"As an alternative to using the TestifyLayout annotation, you may also specific a layout file to be loaded programmatically.","sidebar":"tutorialSidebar"},"recipes/locale":{"id":"recipes/locale","title":"Changing the Locale in a test","description":"API 24+","sidebar":"tutorialSidebar"},"recipes/orientation":{"id":"recipes/orientation","title":"Changing the orientation of the screen","description":"Use the setOrientation method to select between portrait and landscape mode.","sidebar":"tutorialSidebar"},"recipes/software-rendering":{"id":"recipes/software-rendering","title":"Force software rendering","description":"In some instances it may be desirable to use the software renderer, not Android\'s default hardware renderer. Differences in GPU hardware from device to device (and emulators running on different architectures) may cause flakiness in rendering.","sidebar":"tutorialSidebar"},"recipes/testify-layout-library":{"id":"recipes/testify-layout-library","title":"Using @TestifyLayout with library projects","description":"The TestifyLayout annotation allows you to specify a layout resource to be automatically loaded into the host Activity for testing.","sidebar":"tutorialSidebar"},"recipes/tolerance":{"id":"recipes/tolerance","title":"Increase the matching tolerance","description":"In some cases, the captured screenshot may inherently contain randomness. It may then be desirable to allow for an inexact matching. By default, Testify employs an exact, pixel-by-pixel matching algorithm.","sidebar":"tutorialSidebar"},"recipes/view-provider":{"id":"recipes/view-provider","title":"Taking a screenshot of an area less than that of the entire Activity","description":"It is often desirable to capture only a portion of your screen or to capture a single View.","sidebar":"tutorialSidebar"}}}')}}]);