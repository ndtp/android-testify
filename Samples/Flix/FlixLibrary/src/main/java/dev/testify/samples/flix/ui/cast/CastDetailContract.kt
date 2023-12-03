package dev.testify.samples.flix.ui.cast

import dev.testify.samples.flix.data.model.FlixPerson
import dev.testify.samples.flix.ui.base.BaseEffect
import dev.testify.samples.flix.ui.base.BaseEvent
import dev.testify.samples.flix.ui.base.BaseState
import dev.testify.samples.flix.ui.common.util.ImagePromise

sealed class CastDetailEvent : BaseEvent {
    data class Initialize(val castId: Int) : CastDetailEvent()
    object RetryClick : CastDetailEvent()
}

sealed class CastDetailState : BaseState {
    object Uninitialized : CastDetailState()
    object Loading : CastDetailState()
    data class Loaded(
        val person: FlixPerson
    ) : CastDetailState()

    data class Error(val castId: Int) : CastDetailState()
}

sealed class CastDetailEffect : BaseEffect

const val ARG_CAST_ID = "arg_cast_id"
