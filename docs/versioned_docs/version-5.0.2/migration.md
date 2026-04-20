---
sidebar_position: 6
---

# Migrate to Testify 2.0

`dev.testify` replaces the original classes in the `com.shopify.testify` namespace. Only the package and Maven artifact names changed; class, method, and field names did not change.

## Prerequisites

Before you migrate, bring your app up to date. We recommend updating your project to use the final version of the Shopify plugin: version 1.2.0-alpha01. Testify 2.0 is based off of this version.

:::tip

**We recommend working in a separate branch when migrating. Also try to avoid refactoring your code while performing the migration.**
:::

#### Recommended migration strategy

1. Create a new working git branch for your project.
    
    ex. `git checkout -b upgrade-to-testify-2.0`
2. In your `build.gradle`, ensure you are currently using the 1.2.0-alpha01 version of Testify. Check that the `classpath` entry for Testify is set to `com.shopify.testify:plugin:1.2.0-alpha01`. If not, update to version 1.2.0-alpha01, resync your gralde project, and fix any build errors in your project.
3. Update to Testify 2.0.0-alpha01. In your root `build.gradle` file, change the `classpath` entry for Testify to `dev.testify:plugin:2.0.0-alpha01`. Resync your gradle configuration.
4. Perform a global find & replace in your entire project. Replace all instances of `com.shopify.testify` with `dev.testify`.
5. Build your project and run your tests.

Once these steps are complete, you can commit your changes to your working branch, have your changes reviewed, and merge them to your main development branch.

## Mappings

## Artifact Mappings 

The following table lists the current mappings from old Shopify library artifacts to `dev.testify`.

| Old build artifact | Testify 2.0 build artifact |
| - | - |
| com.shopify.testify:plugin | dev.testify:plugin |
| com.shopify.testify:testify | dev.testify:testify |
| com.shopify.testify:testify-compose | dev.testify:testify-compose |

## Class Mappings

The following table lists the current mappings from the old Shopify library packages to the new `dev.testify` packages.

| Old Shopify class | New class |
| - | - |
| com.shopify.testify.ComposableScreenshotRule | dev.testify.ComposableScreenshotRule |
| com.shopify.testify.ComposableTestActivity | dev.testify.ComposableTestActivity |
| com.shopify.testify.internal.DeviceStringFormatter | dev.testify.internal.DeviceStringFormatter |
| com.shopify.testify.internal.exception.ActivityMustImplementResourceOverrideException | dev.testify.internal.exception.ActivityMustImplementResourceOverrideException |
| com.shopify.testify.internal.exception.ActivityNotRegisteredException | dev.testify.internal.exception.ActivityNotRegisteredException |
| com.shopify.testify.internal.exception.AssertSameMustBeLastException | dev.testify.internal.exception.AssertSameMustBeLastException |
| com.shopify.testify.internal.exception.MissingAssertSameException | dev.testify.internal.exception.MissingAssertSameException |
| com.shopify.testify.internal.exception.MissingScreenshotInstrumentationAnnotationException | dev.testify.internal.exception.MissingScreenshotInstrumentationAnnotationException |
| com.shopify.testify.internal.exception.NoScreenshotsOnUiThreadException | dev.testify.internal.exception.NoScreenshotsOnUiThreadException |
| com.shopify.testify.internal.exception.RootViewNotFoundException | dev.testify.internal.exception.RootViewNotFoundException |
| com.shopify.testify.internal.exception.ScreenshotBaselineNotDefinedException | dev.testify.internal.exception.ScreenshotBaselineNotDefinedException |
| com.shopify.testify.internal.exception.ScreenshotDirectoryNotFoundException | dev.testify.internal.exception.ScreenshotDirectoryNotFoundException |
| com.shopify.testify.internal.exception.ScreenshotIsDifferentException | dev.testify.internal.exception.ScreenshotIsDifferentException |
| com.shopify.testify.internal.exception.TestMustLaunchActivityException | dev.testify.internal.exception.TestMustLaunchActivityException |
| com.shopify.testify.internal.exception.TestMustWrapContextException | dev.testify.internal.exception.TestMustWrapContextException |
| com.shopify.testify.internal.exception.UnexpectedOrientationException | dev.testify.internal.exception.UnexpectedOrientationException |
| com.shopify.testify.internal.exception.ViewModificationException | dev.testify.internal.exception.ViewModificationException |
| com.shopify.testify.internal.helpers.WrappedFontScale | dev.testify.internal.helpers.WrappedFontScale |
| com.shopify.testify.internal.helpers.WrappedLocale | dev.testify.internal.helpers.WrappedLocale |
| com.shopify.testify.internal.modification.HideCursorViewModification | dev.testify.internal.modification.HideCursorViewModification |
| com.shopify.testify.internal.modification.HidePasswordViewModification | dev.testify.internal.modification.HidePasswordViewModification |
| com.shopify.testify.internal.modification.HideScrollbarsViewModification | dev.testify.internal.modification.HideScrollbarsViewModification |
| com.shopify.testify.internal.modification.HideTextSuggestionsViewModification | dev.testify.internal.modification.HideTextSuggestionsViewModification |
| com.shopify.testify.internal.modification.ReplacementCharSequence | dev.testify.internal.modification.ReplacementCharSequence |
| com.shopify.testify.internal.modification.SoftwareRenderViewModification | dev.testify.internal.modification.SoftwareRenderViewModification |
| com.shopify.testify.internal.modification.ViewModification | dev.testify.internal.modification.ViewModification |
| com.shopify.testify.internal.processor.compare.SameAsCompare | dev.testify.internal.processor.compare.SameAsCompare |
| com.shopify.testify.internal.processor.diff.HighContrastDiff | dev.testify.internal.processor.diff.HighContrastDiff |
| com.shopify.testify.internal.processor.ParallelPixelProcessor | dev.testify.internal.processor.ParallelPixelProcessor |
| com.shopify.testify.internal.processor.ParallelPixelProcessor.TransformResult | dev.testify.internal.processor.ParallelPixelProcessor.TransformResult |
| com.shopify.testify.ScreenshotRule | dev.testify.ScreenshotRule |
| com.shopify.testify.ScreenshotUtility | dev.testify.ScreenshotUtility |
| com.shopify.testify.TestActivity | dev.testify.TestActivity |

---

## Annotation Mappings

The following table lists the current mappings from the old Shopify library packages to the new `dev.testify` packages.

| Old Shopify annotation | New annotation |
| - | - |
| com.shopify.testify.annotation.BitmapComparisonExactness | dev.testify.annotation.BitmapComparisonExactness |
| com.shopify.testify.annotation.ScreenshotInstrumentation | dev.testify.annotation.ScreenshotInstrumentation |
| com.shopify.testify.annotation.TestifyLayout | dev.testify.annotation.TestifyLayout |

---

## Enum Mappings

The following table lists the current mappings from the old Shopify library packages to the new `dev.testify` packages.

| Old Shopify enum | New enum |
| - | - |
| com.shopify.testify.report.ErrorCause | dev.testify.report.ErrorCause |
| com.shopify.testify.report.ErrorCause.ACTIVITY_OVERRIDE | dev.testify.report.ErrorCause.ACTIVITY_OVERRIDE |
| com.shopify.testify.report.ErrorCause.ASSERT_LAST | dev.testify.report.ErrorCause.ASSERT_LAST |
| com.shopify.testify.report.ErrorCause.DIFFERENT | dev.testify.report.ErrorCause.DIFFERENT |
| com.shopify.testify.report.ErrorCause.LAUNCH_ACTIVITY | dev.testify.report.ErrorCause.LAUNCH_ACTIVITY |
| com.shopify.testify.report.ErrorCause.NO_ACTIVITY | dev.testify.report.ErrorCause.NO_ACTIVITY |
| com.shopify.testify.report.ErrorCause.NO_ANNOTATION | dev.testify.report.ErrorCause.NO_ANNOTATION |
| com.shopify.testify.report.ErrorCause.NO_ASSERT | dev.testify.report.ErrorCause.NO_ASSERT |
| com.shopify.testify.report.ErrorCause.NO_BASELINE | dev.testify.report.ErrorCause.NO_BASELINE |
| com.shopify.testify.report.ErrorCause.NO_DIRECTORY | dev.testify.report.ErrorCause.NO_DIRECTORY |
| com.shopify.testify.report.ErrorCause.NO_ROOT_VIEW | dev.testify.report.ErrorCause.NO_ROOT_VIEW |
| com.shopify.testify.report.ErrorCause.UI_THREAD | dev.testify.report.ErrorCause.UI_THREAD |
| com.shopify.testify.report.ErrorCause.UNEXPECTED_ORIENTATION | dev.testify.report.ErrorCause.UNEXPECTED_ORIENTATION |
| com.shopify.testify.report.ErrorCause.UNKNOWN | dev.testify.report.ErrorCause.UNKNOWN |
| com.shopify.testify.report.ErrorCause.VIEW_MODIFICATION | dev.testify.report.ErrorCause.VIEW_MODIFICATION |
| com.shopify.testify.report.ErrorCause.WRAP_CONTEXT | dev.testify.report.ErrorCause.WRAP_CONTEXT |
| com.shopify.testify.report.TestStatus | dev.testify.report.TestStatus |
| com.shopify.testify.report.TestStatus.FAIL | dev.testify.report.TestStatus.FAIL |
| com.shopify.testify.report.TestStatus.PASS | dev.testify.report.TestStatus.PASS |
| com.shopify.testify.TestifyFeatures | dev.testify.TestifyFeatures |
| com.shopify.testify.TestifyFeatures.CanvasCapture | dev.testify.TestifyFeatures.CanvasCapture |
| com.shopify.testify.TestifyFeatures.GenerateDiffs | dev.testify.TestifyFeatures.GenerateDiffs |
| com.shopify.testify.TestifyFeatures.Locale | dev.testify.TestifyFeatures.Locale |
| com.shopify.testify.TestifyFeatures.PixelCopyCapture | dev.testify.TestifyFeatures.PixelCopyCapture |
| com.shopify.testify.TestifyFeatures.Reporter | dev.testify.TestifyFeatures.Reporter |

---

## Function Mappings

The following table lists the current mappings from the old Shopify library packages to the new `dev.testify` packages.

| Old Shopify top-level function | New top-level function |
| - | - |
| com.shopify.testify.internal.disposeComposition | dev.testify.internal.disposeComposition |
| com.shopify.testify.internal.processor.capture.createBitmapFromCanvas | dev.testify.internal.processor.capture.createBitmapFromCanvas |
| com.shopify.testify.internal.processor.capture.createBitmapFromDrawingCache | dev.testify.internal.processor.capture.createBitmapFromDrawingCache |
| com.shopify.testify.internal.processor.capture.createBitmapUsingPixelCopy | dev.testify.internal.processor.capture.createBitmapUsingPixelCopy |
| com.shopify.testify.internal.processor.compare.colorspace.calculateDeltaE | dev.testify.internal.processor.compare.colorspace.calculateDeltaE |
| com.shopify.testify.internal.processor.createBitmap | dev.testify.internal.processor.createBitmap |

---
