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
package dev.testify.internal.modification

import android.view.View

/**
 * A [ViewModification] that sets the layer type to [View.LAYER_TYPE_SOFTWARE].
 *
 * A software layer is backed by a bitmap and causes the view to be rendered using Android's software rendering pipeline,
 * even if hardware acceleration is enabled.
 *
 * Software layers can occasionally be useful to work around inconsistencies present when using hardware acceleration on
 * a variety of different devices. For example, some devices may render a view differently when hardware acceleration is
 * enabled, so a software layer can be used to ensure that the view is rendered consistently across all devices.
 *
 * However, it is important to note that the software rendered is significantly slower than the hardware accelerated.
 * And, some features (such as rounded corners, shadows and elevation) are not supported when using software rendering.
 * This may result in undesirable artifacts when using software rendering.
 *
 * If you are using a custom view that is not rendering consistently across devices, you may want to consider the
 * techniques described in the
 * [Accounting for platform differences](https://testify.dev/blog/platform-differences) blog post.
 *
 * @see TestifyConfiguration.useSoftwareRenderer
 */
class SoftwareRenderViewModification : ViewModification() {

    override fun performModification(view: View) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun qualifies(view: View): Boolean {
        return true
    }
}
