/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 ndtp
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
package dev.testify.core.exception

/**
 * Exception thrown when attempting to use a resource configuration with a ScenarioRule.
 * ScenarioRule controls the lifecycle of the activity, and therefore the resource configuration
 * must be applied before the activity is launched.
 *
 * @param cause the type of the resource configuration
 * @param value the value of the resource configuration being set
 */
class NoResourceConfigurationOnScenarioException(cause: String, value: String, activity: String) :
    TestifyException(
        "INVALID_RESOURCE_CONFIGURATION",
        "\n\n* Configuration `$cause` can not be used with a `ScenarioRule`\n" +
            "* To configure $cause, use `overrideResourceConfiguration<$activity>($cause = $value)` " +
            "before calling `launchActivity<$activity>()`\n"
    )
