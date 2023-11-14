# Passing Intent extras to the Activity under test

Some activities may require a `Bundle` of additional information called _extras_. Extras can be used to provide extended information to the component. For example, if we have a action to send an e-mail message, we could also include extra pieces of data here to supply a subject, body, etc.
To provide extras to your Activity, you can implement the `addIntentExtras` method on `ScreenshotRule` and pass a lambda that can add to the provided `Bundle`.

```kotlin
class MainActivityScreenshotTest {

    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)

    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .addIntentExtras {
                it.putString("TOOLBAR_TITLE", "addIntentExtras")
            }
            .assertSame()
    }
}
```
