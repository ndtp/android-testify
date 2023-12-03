package dev.testify.samples.flix.ui.cast

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.testify.samples.flix.data.model.FlixPerson
import dev.testify.samples.flix.repository.CastMemberRepository
import dev.testify.samples.flix.ui.base.BaseEvent
import dev.testify.samples.flix.ui.base.BaseViewModel
import dev.testify.samples.flix.ui.cast.CastDetailEvent.Initialize
import dev.testify.samples.flix.ui.cast.CastDetailEvent.RetryClick
import dev.testify.samples.flix.ui.cast.CastDetailState.Error
import dev.testify.samples.flix.ui.cast.CastDetailState.Loaded
import dev.testify.samples.flix.ui.cast.CastDetailState.Loading
import dev.testify.samples.flix.ui.cast.CastDetailState.Uninitialized
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CastDetailViewModel @Inject constructor(
    private val castMemberRepository: CastMemberRepository
) : BaseViewModel<CastDetailState, CastDetailEvent, CastDetailEffect>() {

    override val initialState = Uninitialized

    private var fetchDataJob: Job? = null

    override fun handleEvent(event: BaseEvent) {
        when (event) {
            is Initialize -> onInitialize(event.castId)
            is RetryClick -> onRetryClick()
        }
    }

    private fun onInitialize(castId: Int) {
        withState<Uninitialized> {
            setState(Loading)
            fetchData(castId)
        }
    }

    private fun fetchData(castId: Int) {
        fetchDataJob?.cancel()
        fetchDataJob = viewModelScope.launch {
            castMemberRepository.getCastMember(castId).collect { person ->
                if (person == null) {
                    setState(Error(castId))
                } else {
                    handleEmission(person)
                }
            }
        }
    }

    private fun handleEmission(person: FlixPerson) {
        withState<Loading> {
            setState(Loaded(person = person))
        }
    }

    private fun onRetryClick() {
        withState<Error> { error ->
            setState(Loading)
            fetchData(error.castId)
        }
    }
}
