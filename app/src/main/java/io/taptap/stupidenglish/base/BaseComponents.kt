package io.taptap.stupidenglish.base

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.timer

const val LAUNCH_LISTEN_FOR_EFFECTS = "launch-listen-to-effects"

interface ViewState

interface ViewEvent

interface ViewSideEffect

abstract class BaseViewModel<Event : ViewEvent, UiState : ViewState, Effect : ViewSideEffect> :
    ViewModel() {

    private val initialState: UiState by lazy { setInitialState() }
    abstract fun setInitialState(): UiState

    private val _viewState: MutableState<UiState> = mutableStateOf(initialState)
    val viewState: State<UiState> = _viewState

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    fun setEvent(event: Event) {
        viewModelScope.launch {
            Log.d("StupidEnglishState", "event = $event")
            _event.emit(event)
        }
    }

    protected fun setState(reducer: UiState.() -> UiState) {
        viewModelScope.launch {
            val newState = viewState.value.reducer()
            Log.d("StupidEnglishState", "state = $newState")
            _viewState.value = newState
        }
    }

    protected fun setTemporaryState(
        tempReducer: UiState.() -> UiState,
        duration: Long
    ) {
        viewModelScope.launch {
            val oldValue = _viewState.value

            val newState = viewState.value.tempReducer()
            Log.d("StupidEnglishState", "state = $newState")
            _viewState.value = newState

            delay(duration)

            _viewState.value = oldValue
        }
    }

    private fun subscribeToEvents() {
        viewModelScope.launch {
            _event.collect {
                Log.d("StupidEnglishState", "handleEvents = $it")
                handleEvents(it)
            }
        }
    }

    abstract fun handleEvents(event: Event)

    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch {
            Log.d("StupidEnglishState", "effectValue = $effectValue")
            _effect.send(effectValue)
        }
    }

}
