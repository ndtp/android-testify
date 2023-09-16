/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2019 Shopify Inc.
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

package dev.testify.annotation

import android.view.View.NO_ID
import androidx.annotation.LayoutRes

/**
 * The [TestifyLayout] annotation allows you to specify a layout resource to be automatically
 * loaded into the host Activity for testing.
 *
 * @sample dev.testify.ExperimentalPixelCopyTest.withoutPixelCopy
 *
 * @param layoutId: This integer parameter is a layout resource reference
 *      (e.g. {@code android.R.layout.list_content}).
 * @param layoutResName: A fully qualified resource name of the form "package:type/entry"
 *      (e.g. {@code android:layout/list_content}).
 *      Unfortunately R fields are not constants in Android library projects. LayoutRes IDs cannot
 *      be used as annotations parameters.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class TestifyLayout(
    @LayoutRes val layoutId: Int = NO_ID,
    val layoutResName: String = ""
)
