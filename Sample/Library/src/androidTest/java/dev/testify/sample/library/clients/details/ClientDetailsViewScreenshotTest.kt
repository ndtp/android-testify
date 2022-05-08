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
package dev.testify.sample.library.clients.details

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.testify.ScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.annotation.TestifyLayout
import dev.testify.sample.library.R
import dev.testify.sample.library.test.TestHarnessActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClientDetailsViewScreenshotTest {

    @get:Rule
    var rule = ScreenshotRule(
        activityClass = TestHarnessActivity::class.java,
        rootViewId = R.id.harness_root,
        enableReporter = true
    )

    @TestifyLayout(R.layout.view_client_details)
    @ScreenshotInstrumentation
    @Test
    fun default() {
        rule
            .setViewModifications { harnessRoot ->
                val viewState = ClientDetailsViewState(
                    name = "Testify Test",
                    avatar = R.drawable.avatar1,
                    heading = "This is the heading",
                    address = "1 Address Street\nCity, State, Country\nZ1PC0D3",
                    phoneNumber = "1-234-567-8910"
                )
                val view = harnessRoot.getChildAt(0) as ClientDetailsView
                view.render(viewState)
                rule.activity.title = viewState.name
            }.assertSame()
    }

    // TODO: Determine the correct format for library project resources
//    @TestifyLayout(layoutResName = "dev.testify.sample:layout/view_client_details")
//    @ScreenshotInstrumentation
//    @Test
//    fun usingLayoutResName() {
//        rule.setViewModifications { harnessRoot ->
//            val viewState = ClientDetailsViewState(
//                name = "Using Res Name",
//                avatar = R.drawable.avatar1,
//                heading = "This is the heading",
//                address = "1 Address Street\nCity, State, Country\nZ1PC0D3",
//                phoneNumber = "1-234-567-8910"
//            )
//            val view = harnessRoot.getChildAt(0) as ClientDetailsView
//            view.render(viewState)
//            rule.activity.title = viewState.name
//        }.assertSame()
//    }
}
