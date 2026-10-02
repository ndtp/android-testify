---
keywords: [blank screenshot, empty screenshot, missing content, wait, delay, async, asynchronous, idling resource, IdlingResource, IdlingThreadPoolExecutor, CountingIdlingResource, onIdle, animation, image loading, Glide, Coil, Picasso, SurfaceView, MapView, PixelCopy, drawing cache, shadows, elevation, flaky]
---

import OpenNew from '@site/static/img/open_new.svg';

# Fixing blank or incomplete screenshots

If a screenshot is blank, is missing part of the screen, or changes from one run to the next, the cause is almost always one of two things:

1. **The content wasn't ready yet.** Data, images or animations finished after Testify took the screenshot.
2. **The capture method can't see the content.** Some views are drawn directly by the GPU, outside the part of the screen the default capture method reads.

A quick way to tell the two apart: if the missing content is a map, a video, a camera preview or another `SurfaceView`, start with [the capture method](#content-drawn-by-the-gpu). Otherwise, start with [timing](#content-that-loads-asynchronously).

## When Testify takes the screenshot

How long Testify waits depends on which rule you use, and the difference matters most here.

| Rule | Waits for | Honours idling resources |
|---|---|---|
| `ScreenshotRule` | The main thread, then [`Espresso.onIdle()` <OpenNew />](https://developer.android.com/reference/androidx/test/espresso/Espresso#onIdle()), after applying your view modifications and Espresso actions | Yes |
| `ScreenshotScenarioRule` | The main thread, via `Instrumentation.waitForIdleSync()` | **No** |
| `ComposableScreenshotRule` | The same as `ScreenshotRule`, which it extends, plus `ComposeTestRule.waitForIdle()` | Yes |
| `ComposableScreenshotScenarioRule` | The same as `ScreenshotScenarioRule`, which it extends, plus `ComposeTestRule.waitForIdle()` | **No** |

The distinction in the last column is the one that matters here. Every rule waits for the main thread to settle, so work posted to it is covered either way. Only the `ScreenshotRule` family consults Espresso, and registering an idling resource is how you tell Espresso about work that is *not* on the main thread — a network call on a background thread, or an image decode inside an image-loading library. Under the scenario rules, that resource is never consulted, and the capture happens as soon as the main thread is quiet.

### Idling with `ScreenshotScenarioRule`

`ScreenshotScenarioRule` waits for the main thread — `afterInitializeView` calls `Instrumentation.waitForIdleSync()` — but it has no Espresso integration: it never calls `Espresso.onIdle()` and has no `setEspressoActions`. With `ActivityScenario` your test already owns driving the activity, per [Android's guidance on driving an activity to a new state <OpenNew />](https://developer.android.com/guide/components/activities/testing#drive-activity-new-state), so Testify steps out of the way rather than waiting on a mechanism you may not be using.

That means you do the synchronising, inside `launchActivity { }.use { }` and before `assertSame()`. Driving Espresso directly is usually enough, because `perform()` synchronises for you:

```kotlin
launchActivity<TestHarnessActivity>().use { scenario ->
    scenario.onActivity { activity ->
        val parentView = activity.findRootView(rule.rootViewId)
        activity.layoutInflater.inflate(R.layout.view_edit_text, parentView, true)
    }

    Espresso.onView(withId(R.id.edit_text)).perform(typeText("Testify"))

    rule
        .withScenario(scenario)
        .assertSame()
}
```

See `setEspressoActions` in [ScreenshotScenarioRuleExampleTests.kt <OpenNew />](https://github.com/ndtp/android-testify/blob/main/Samples/Legacy/src/androidTest/java/dev/testify/sample/scenario/ScreenshotScenarioRuleExampleTests.kt) for the complete test. If you register an idling resource as described below, call `Espresso.onIdle()` yourself before `assertSame()`.

## Content that loads asynchronously

Tell Espresso about the background work with an idling resource, so Testify waits until the work is done. A `CountingIdlingResource` is the simplest option. Increment it when the work starts and decrement it when it finishes:

```kotlin
val idlingResource = CountingIdlingResource("ClientListLoading")

// Before the work starts
IdlingRegistry.getInstance().register(idlingResource)
idlingResource.increment()

// When the work has finished
idlingResource.decrement()
IdlingRegistry.getInstance().unregister(idlingResource)
```

Register the resource before the work starts, or Espresso may report idle before it knows the work exists.

:::tip

To check that your idling resource is registered at the right time, temporarily remove the call to `decrement()`. Under `ScreenshotRule` the test should now hang and then fail with an Espresso idling timeout that names your resource. If it captures without waiting, your resource wasn't registered in time.

This check only works where something waits on Espresso. Under `ScreenshotScenarioRule` the capture happens as soon as the main thread is idle, whether the resource is registered or not, so add an explicit `Espresso.onIdle()` before `assertSame()` and test for the timeout there.

:::

If more than one piece of work can be in flight at once, for example several images on one screen, give each resource a unique name, such as one based on the object's `hashCode()`.

## Images from Glide, Coil or Picasso

Image-loading libraries decode images on background threads, so the screenshot is often taken before the image appears. Two things make this harder than other asynchronous work:

- **A loaded image isn't a drawn image.** A library's "image ready" callback can run before the next frame draws the image. Decrement the idling resource in that callback, and let Testify's idle wait cover the frame that follows. If you drive a `ComposeTestRule` yourself, call `composeTestRule.waitForIdle()` after the image is ready.
- **Transitions keep changing the image.** Glide and Coil can cross-fade between the placeholder and the image. Turn off cross-fades in tests, and turn off animations on the emulator.

There are two practical approaches, and the Flix sample uses both.

### Make loading synchronous

The simplest fix is to configure the loader so the decode happens on the calling thread, which removes the race instead of waiting it out. For Coil, swap the dispatcher in a `@Before`:

```kotlin
@Before
fun before() {
    Coil.setImageLoader(
        ImageLoader.Builder(InstrumentationRegistry.getInstrumentation().targetContext)
            .dispatcher(Dispatchers.Unconfined)
            .build()
    )
}
```

Pair it with images the test can load without a network, such as `file:///android_asset/`. See [HomeScreenTest.kt <OpenNew />](https://github.com/ndtp/android-testify/blob/main/Samples/Flix/src/androidTest/java/dev/testify/samples/flix/ui/homescreen/HomeScreenTest.kt), which does exactly this and loads its posters from the app's assets.

### Make the loader's thread pool visible to Espresso

If you would rather keep the real threading, hand the loader an [`IdlingThreadPoolExecutor` <OpenNew />](https://developer.android.com/reference/androidx/test/espresso/idling/concurrent/IdlingThreadPoolExecutor). It registers itself with Espresso, so any work queued on it counts towards idleness and no per-request bookkeeping is needed:

```kotlin
private fun synchronousDispatcher(): CoroutineDispatcher =
    IdlingThreadPoolExecutor(
        "coilImageLoaderThreadPool",
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors(),
        0L,
        TimeUnit.MILLISECONDS,
        LinkedBlockingQueue(),
        Executors.defaultThreadFactory()
    ).asCoroutineDispatcher()

fun setSynchronousImageLoader() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    Coil.setImageLoader(ImageLoader.Builder(context).dispatcher(synchronousDispatcher()).build())
}
```

This needs `androidx.test.espresso.idling:idling-concurrent`. The full helper is [TestImageLoader.kt <OpenNew />](https://github.com/ndtp/android-testify/blob/main/Samples/Flix/FlixLibrary/src/androidTest/java/dev/testify/samples/flix/test/TestImageLoader.kt).

Picasso takes a custom `ExecutorService` the same way, through `Picasso.Builder.executor`. Glide does not: `GlideBuilder.setSourceExecutor` wants a `GlideExecutor`, which is built through its own factory methods rather than wrapped around an executor you supply.

Where you cannot hand the loader an executor, tie a `CountingIdlingResource` to each request in the library's own listener instead — Glide's `RequestListener`, Coil's `ImageRequest.Listener` or Picasso's `Callback` — using the pattern above.

## Animations

Animations that are still running when Testify captures make screenshots differ from run to run. Turn off the emulator's animations in **Developer options**: set **Window animation scale**, **Transition animation scale** and **Animator duration scale** to **Animation off**. From the command line:

```shell-session
$ adb shell settings put global window_animation_scale 0
$ adb shell settings put global transition_animation_scale 0
$ adb shell settings put global animator_duration_scale 0
```

See [Configure your emulator](../get-started/2-configuring-an-emulator.md) for the recommended emulator settings.

## Content drawn by the GPU

Testify's default capture method for views reads the activity's drawing cache. Content that the GPU draws onto its own surface isn't in that cache, so it's missing from the screenshot. This includes `SurfaceView`, maps, video and camera previews.

The drawing cache is drawn in software, so it also loses the effects that need hardware acceleration — rounded corners, shadows and elevation. That is the same limitation `TestifyConfiguration.useSoftwareRenderer` documents, reached by a different route: there you opt into software rendering, here the default capture method gets you there anyway.

Capture with `PixelCopy` instead, which reads the pixels shown on screen:

```kotlin
@ScreenshotInstrumentation
@Test
fun mapScreen() {
    rule
        .configure {
            captureMethod = ::pixelCopyCapture
            exactness = 0.95f
        }
        .assertSame()
}
```

`PixelCopy` reads pixels from the GPU, so the result can vary slightly between machines. That's why this example also sets a small `exactness` tolerance. The Compose rules already use `PixelCopy` by default.

To include content in other windows, such as dialogs and menus, use the [Fullscreen Capture Method](../extensions/fullscreen/0-overview.md). See [Selecting an alternative capture method](12-capture-method.md) for all the options.
