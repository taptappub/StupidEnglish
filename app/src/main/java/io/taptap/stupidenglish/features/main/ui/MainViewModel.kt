package io.taptap.stupidenglish.features.main.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
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

    override fun setInitialState() = MainContract.State(
        isShownGreetings = false,
        bottomBarTabs = listOf(
            NavigationKeys.BottomNavigationScreen.SE_WORDS,
            NavigationKeys.BottomNavigationScreen.SE_SENTENCES
        )
    )

    override fun handleEvents(event: MainContract.Event) {
        when (event) {
            is MainContract.Event.OnGreetingsClose -> {
                repository.isFirstStart = false
                setState { copy(isShownGreetings = false) }
            }
            is MainContract.Event.OnTabSelected -> {
                val route = event.item.route
                setEffect { MainContract.Effect.Navigation.OnTabSelected(route) }
            }
        }
    }
}
