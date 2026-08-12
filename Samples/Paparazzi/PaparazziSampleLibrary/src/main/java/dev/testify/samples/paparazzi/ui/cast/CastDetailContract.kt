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
package dev.testify.samples.paparazzi.ui.cast

import dev.testify.samples.paparazzi.data.model.PaparazziPerson
import dev.testify.samples.paparazzi.ui.base.BaseEffect
import dev.testify.samples.paparazzi.ui.base.BaseEvent
import dev.testify.samples.paparazzi.ui.base.BaseState
import dev.testify.samples.paparazzi.ui.common.util.ImagePromise

sealed class CastDetailEvent : BaseEvent {
    data class Initialize(val castId: Int) : CastDetailEvent()
    object RetryClick : CastDetailEvent()
}

sealed class CastDetailState : BaseState {
    object Uninitialized : CastDetailState()
    object Loading : CastDetailState()
    data class Loaded(
        val person: PaparazziPerson
    ) : CastDetailState()

    data class Error(val castId: Int) : CastDetailState()
}

sealed class CastDetailEffect : BaseEffect

const val ARG_CAST_ID = "arg_cast_id"
