package io.taptap.stupidenglish.features.main.ui

import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceContract
import io.taptap.stupidenglish.features.words.ui.WordListContract

class MainContract {
    sealed class Event : ViewEvent {
        data class OnTabSelected(val item: NavigationKeys.BottomNavigationScreen) : Event()
        data class ChangeBottomSheetVisibility(val visibility: Boolean) : Event()

        object OnGreetingsClose : Event()
    }

    data class State(
        val isBottomBarShown: Boolean,
        val isShownGreetings: Boolean,
        val bottomBarTabs: List<NavigationKeys.BottomNavigationScreen>
    ) : ViewState

    sealed class Effect : ViewSideEffect {

        sealed class Navigation : Effect() {
            data class OnTabSelected(val route: String) : Navigation()
        }
    }
}