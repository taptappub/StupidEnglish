package io.taptap.stupidenglish.features.main.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.main.data.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : BaseViewModel<MainContract.Event, MainContract.State, MainContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val isFirstStart = repository.isFirstStart
            setState {
                copy(
                    isShownGreetings = isFirstStart
                )
            }
        }
    }

    override fun setInitialState() = MainContract.State(isShownGreetings = false)

    override fun handleEvents(event: MainContract.Event) {
        when (event) {
            MainContract.Event.OnGreetingsClose -> {
                repository.isFirstStart = false
                setState { copy(isShownGreetings = false) }
            }
        }
    }
}