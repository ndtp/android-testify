/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2020 Shopify Inc.
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
package dev.testify.sample.test

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import dev.testify.resources.TestifyResourcesOverride
import dev.testify.sample.TestingResourceConfigurationsExampleTest
import java.util.*

/**
 * This Activity is used to demonstrate the use of [TestifyResourcesOverride].
 *
 * Starting in Android API Version 24 (Nougat), the proper way to dynamically alter an Activity's
 * resources and locale is to wrap the base [Context] in [AppCompatActivity.attachBaseContext]
 * with a Context that has been updated with a new [Locale]. Testify provides a helper interface,
 * [TestifyResourcesOverride] which provides a Context extension method, [TestifyResourcesOverride.wrap].
 *
 */
@VisibleForTesting
class TestLocaleHarnessActivity : TestHarnessActivity(), TestifyResourcesOverride {

    /**
     * This is required to correctly support dynamic Locale changes
     *
     * See [TestingResourceConfigurationsExampleTest]
     */
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.wrap())
    }
}
