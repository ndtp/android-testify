---
keywords: [dialog, AlertDialog, menu, overflow menu, popup, popover, PopupWindow, DropdownMenu, bottom sheet, window, fullscreen]
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Capturing dialogs, menus and popups

Dialogs, menus, popups and dropdowns are drawn in their own window, on top of your activity. Testify's standard capture methods only capture the activity's own view hierarchy, so these elements are missing from the screenshot.

To include them, use the [Fullscreen Capture Method](../extensions/fullscreen/0-overview.md). It captures the whole device screen, including every window. This includes:

- `AlertDialog`, `Dialog` and `DialogFragment`
- Options and overflow menus, `PopupMenu` and `PopupWindow`
- Bottom sheet dialogs
- In Compose, `AlertDialog`, `Dialog`, `DropdownMenu`, `ModalBottomSheet` and `Popup`

## Set up

The Fullscreen Capture Method is a separate artifact. Add it to your `androidTest` dependencies as described in [Set up testify-fullscreen](../extensions/fullscreen/1-setup.md).

A full-screen capture includes the status bar and the navigation bar, which change between runs. Exclude them from the comparison with `excludeSystemUi()`, and allow a small color tolerance with `exactness`.

## Views

Open the dialog, menu or popup before `assertSame()` runs. Show a dialog from `setViewModifications`, or open a menu with an Espresso action.

<Tabs>
<TabItem value="dialog" label="Dialog">

```kotlin
class DialogScreenshotTest {

    @get:Rule
    val rule = ScreenshotRule(
        activityClass = MainActivity::class.java,
        configuration = TestifyConfiguration(exactness = 0.95f)
    )

    @ScreenshotInstrumentation
    @Test
    fun withDialog() {
        rule
            .captureFullscreen()
            .configure {
                excludeSystemUi()
            }
            .setViewModifications {
                MaterialAlertDialogBuilder(it.context)
                    .setMessage("Hello, world!")
                    .show()
            }
            .assertSame()
    }
}
```

</TabItem>
<TabItem value="menu" label="Overflow menu">

```kotlin
class MenuScreenshotTest {

    @get:Rule
    val rule = ScreenshotRule(
        activityClass = MainActivity::class.java,
        configuration = TestifyConfiguration(exactness = 0.95f)
    )

    @ScreenshotInstrumentation
    @Test
    fun withMenu() {
        rule
            .captureFullscreen()
            .configure {
                excludeSystemUi()
            }
            .setEspressoActions {
                openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
            }
            .assertSame()
    }
}
```

</TabItem>
</Tabs>

For a `PopupWindow` or `PopupMenu`, open it the same way your app does, for example by clicking the view that shows it in `setEspressoActions`.

## Jetpack Compose

With the [Compose extension](../extensions/compose/2-test.md), you can open a menu with a Compose action, or put a dialog directly in the composition.

<Tabs>
<TabItem value="dropdown" label="DropdownMenu">

```kotlin
@ScreenshotInstrumentation
@Test
fun dropdownMenu() {
    rule
        .setCompose {
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                Box {
                    Button(onClick = { expanded = true }, modifier = Modifier.testTag("menuButton")) {
                        Text("Options")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Edit") }, onClick = {})
                        DropdownMenuItem(text = { Text("Delete") }, onClick = {})
                    }
                }
            }
        }
        .setComposeActions { composeTestRule ->
            composeTestRule.onNodeWithTag("menuButton").performClick()
        }
        .captureFullscreen()
        .configure {
            excludeSystemUi()
        }
        .assertSame()
}
```

</TabItem>
<TabItem value="dialog" label="AlertDialog">

```kotlin
@ScreenshotInstrumentation
@Test
fun dialog() {
    rule
        .configure {
            captureMethod = ::fullscreenCapture
            excludeSystemUi()
        }
        .setCompose {
            Box(modifier = Modifier.fillMaxSize()) {
                AlertDialog(
                    onDismissRequest = {},
                    text = { Text("Hello, Testify!") },
                    confirmButton = { Button(onClick = {}) { Text("OK") } }
                )
            }
        }
        .assertSame()
}
```

</TabItem>
</Tabs>

The Compose rules already use an `exactness` of `0.9` by default. These examples use Material 3.

More examples are in the sample project's [FullscreenCaptureExampleTests.kt](https://github.com/ndtp/android-testify/blob/main/Samples/Legacy/src/androidTest/java/dev/testify/sample/FullscreenCaptureExampleTests.kt) and [ComposableScreenshotTest.kt](https://github.com/ndtp/android-testify/blob/main/Samples/Legacy/src/androidTest/java/dev/testify/sample/compose/ComposableScreenshotTest.kt).
