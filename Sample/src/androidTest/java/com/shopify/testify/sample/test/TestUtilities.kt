/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Shopify Inc.
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

package com.shopify.testify.sample.test

import android.content.Context
import android.view.ViewGroup
import com.shopify.testify.sample.R
import com.shopify.testify.sample.clients.details.ClientDetailsView
import com.shopify.testify.sample.clients.details.ClientDetailsViewState

val ViewGroup.clientDetailsView: ClientDetailsView
    get() {
        return this.getChildAt(0) as ClientDetailsView
    }

fun Context.getViewState(name: String): ClientDetailsViewState {
    return ClientDetailsViewState(
        name = name,
        avatar = R.drawable.avatar1,
        heading = this.getString(R.string.client_since, "2020"),
        address = "1 Address Street\nCity, State, Country\nZ1PC0D3",
        phoneNumber = "1-234-567-8910"
    )
}
