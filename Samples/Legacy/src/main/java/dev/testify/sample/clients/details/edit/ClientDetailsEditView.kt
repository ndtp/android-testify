/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 ndtp
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
package dev.testify.sample.clients.details.edit

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.isVisible
import dev.testify.sample.databinding.ViewEditClientDetailsBinding

class ClientDetailsEditView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    lateinit var binding: ViewEditClientDetailsBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ViewEditClientDetailsBinding.bind(this)
    }

    fun render(state: ClientDetailsEditViewState) {
        binding.profileImage.setImageResource(state.avatar)
        binding.heading.text = Editable.Factory().newEditable(state.name)

        binding.addressRow.isVisible = state.address != null
        binding.address.text = Editable.Factory().newEditable(state.address)

        binding.phoneRow.isVisible = state.phoneNumber != null
        binding.phone.text = Editable.Factory().newEditable(state.phoneNumber)
    }
}
