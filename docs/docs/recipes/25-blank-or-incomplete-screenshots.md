---
keywords: [blank screenshot, empty screenshot, missing content, wait, delay, async, asynchronous, idling resource, IdlingResource, animation, image loading, Glide, Coil, Picasso, SurfaceView, MapView, PixelCopy, flaky]
---

import OpenNew from '@site/static/img/open_new.svg';

# Fixing blank or incomplete screenshots

If a screenshot is blank, is missing part of the screen, or changes from one run to the next, the cause is almost always one of two things:

1. **The content wasn't ready yet.** Data, images or animations finished after Testify took the screenshot.
2. **The capture method can't see the content.** Some views are drawn directly by the GPU, outside the part of the screen the default capture method reads.

A quick way to tell the two apart: if the missing content is a map, a video, a camera preview or another `SurfaceView`, start with [the capture method](#content-drawn-by-the-gpu). Otherwise, start with [timing](#content-that-loads-asynchronously).

## When Testify takes the screenshot

Testify waits for your test's UI to be idle before it captures. It launches the activity, applies your view modifications and Espresso actions, then waits on [`Espresso.onIdle()` <OpenNew />](https://developer.android.com/reference/androidx/test/espresso/Espresso#onIdle()). The Compose rules also wait for the composition to be idle.

Espresso only knows about work on the main thread and in [idling resources <OpenNew />](https://developer.android.com/training/testing/espresso/idling-resource) that you register. Anything else, such as a network call on a background thread or an image decoding in an image-loading library, can still be running when Testify captures.

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

To check that your idling resource is registered at the right time, temporarily remove the call to `decrement()`. The test should now hang and then fail with an Espresso idling timeout that names your resource. If the test captures without waiting, your resource wasn't registered in time.

:::

If more than one piece of work can be in flight at once, for example several images on one screen, give each resource a unique name, such as one based on the object's `hashCode()`.

## Images from Glide, Coil or Picasso

Image-loading libraries decode images on background threads, so the screenshot is often taken before the image appears. Two things make this harder than other asynchronous work:

- **A loaded image isn't a drawn image.** A library's "image ready" callback can run before the next frame draws the image. Decrement the idling resource in that callback, and let Testify's idle wait cover the frame that follows. If you drive a `ComposeTestRule` yourself, call `composeTestRule.waitForIdle()` after the image is ready.
- **Transitions keep changing the image.** Glide and Coil can cross-fade between the placeholder and the image. Turn off cross-fades in tests, and turn off animations on the emulator.

Tie the idling resource to the individual image request. Register and increment it when you create the request listener, and decrement and unregister it when the image is ready or fails to load. This example uses a Glide 4 `RequestListener`:

```kotlin
val idlingResource = CountingIdlingResource("image-${model.hashCode()}")
IdlingRegistry.getInstance().register(idlingResource)
idlingResource.increment()

val listener = object : RequestListener<Drawable> {
    override fun onResourceReady(
        resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean
    ): Boolean {
        idlingResource.decrement()
        IdlingRegistry.getInstance().unregister(idlingResource)
        return false
    }

    override fun onLoadFailed(
        e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean
    ): Boolean {
        idlingResource.decrement()
        IdlingRegistry.getInstance().unregister(idlingResource)
        return false
    }
}
```

The same approach works with Coil's `ImageRequest.Listener` and Picasso's `Callback`.

## Animations

Animations that are still running when Testify captures make screenshots differ from run to run. Turn off the emulator's animations in **Developer options**: set **Window animation scale**, **Transition animation scale** and **Animator duration scale** to **Animation off**. From the command line:

```shell-session
$ adb shell settings put global window_animation_scale 0
$ adb shell settings put global transition_animation_scale 0
$ adb shell settings put global animator_duration_scale 0
```

See [Configure your emulator](../get-started/2-configuring-an-emulator.md) for the recommended emulator settings.

## Content drawn by the GPU

Testify's default capture method for views reads the activity's drawing cache. Content that the GPU draws onto its own surface isn't in that cache, so it's missing from the screenshot. This includes `SurfaceView`, maps, video and camera previews, and some shadows and elevation.

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
