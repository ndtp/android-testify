/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 ndtp
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dev.testify.accessibility

import android.app.Activity
import android.view.View
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import dev.testify.ScreenshotRule
import dev.testify.accessibility.exception.AccessibilityErrorsException
import dev.testify.accessibility.exception.MalformedBaselineException
import dev.testify.accessibility.internal.AccessibilityScreenshotLifecycleObserver
import dev.testify.scenario.ScreenshotScenarioRule

/**
 * To help people with disabilities access Android apps, developers of those apps need to consider how their apps will
 * be presented to accessibility services. Some good practices can be checked by automated tools, such as if a View has
 * a contentDescription. Other rules require human judgment, such as whether or not a contentDescription makes sense to
 * all users.
 *
 * Enabling [assertAccessibility] on [ScreenshotRule] will run the latest set of checks as defined by the Accessibility
 * Test Framework for Android (https://github.com/google/Accessibility-Test-Framework-for-Android). This library
 * collects various accessibility-related checks on [View] objects as well as AccessibilityNodeInfo objects (which the
 * Android framework derives from Views and sends to AccessibilityServices).
 *
 * @see "https://developer.android.com/training/testing/espresso/accessibility-checking"
 *
 * All elements within the hierarchy defined by [android.R.id.content] will be available to the check.
 *
 * @throws [AccessibilityErrorsException] - Errors have been found in the hierarchy.
 * @throws [MalformedBaselineException] - The baseline json file is corrupt or cannot be loaded.
 */
fun <T : Activity> ScreenshotRule<T>.assertAccessibility(): ScreenshotRule<T> {
    val observer = AccessibilityScreenshotLifecycleObserver(getInstrumentation().context)
    this.addScreenshotObserver(observer)
    return this
}

/**
 * Extension method for [ScreenshotScenarioRule] of [assertAccessibility]
 */
fun ScreenshotScenarioRule.assertAccessibility(): ScreenshotScenarioRule {
    val observer = AccessibilityScreenshotLifecycleObserver(getInstrumentation().context)
    this.addScreenshotObserver(observer)
    return this
}
