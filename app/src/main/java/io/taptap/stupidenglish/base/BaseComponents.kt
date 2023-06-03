package io.taptap.stupidenglish.base

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val LAUNCH_LISTEN_FOR_EFFECTS = "launch-listen-to-effects"

interface ViewState

interface ViewEvent

interface ViewSideEffect

abstract class BaseViewModel<Event : ViewEvent, UiState : ViewState, Effect : ViewSideEffect> :
    ViewModel() {

    private val initialState: UiState by lazy { setInitialState() }
    abstract fun setInitialState(): UiState

    private val _viewState: MutableStateFlow<UiState> = MutableStateFlow(initialState)
    val viewState: StateFlow<UiState> = _viewState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    fun setEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("StupidEnglishState", "event = $event")
            _event.emit(event)
        }
    }

    protected fun setState(reducer: UiState.() -> UiState) {
        viewModelScope.launch(Dispatchers.Main) {
            val newState = viewState.value.reducer()
            Log.d("StupidEnglishState", "state = $newState")
            _viewState.value = newState
        }
//        viewModelScope.launch(Dispatchers.Main) {
//            _viewState.update(reducer)
//        }
    }

    protected fun setTemporaryState(
        tempReducer: UiState.() -> UiState,
        duration: Long
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val oldValue = _viewState.value

            val newState = viewState.value.tempReducer()
            Log.d("StupidEnglishState", "state = $newState")
            _viewState.value = newState

            delay(duration)

            _viewState.value = oldValue
        }
    }

    private fun subscribeToEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            _event.collect {
                Log.d("StupidEnglishState", "handleEvents = $it")
                handleEvents(it)
            }
        }
    }

    abstract suspend fun handleEvents(event: Event)

    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch {
            Log.d("StupidEnglishState", "effectValue = $effectValue")
            _effect.send(effectValue)
        }
    }

}
