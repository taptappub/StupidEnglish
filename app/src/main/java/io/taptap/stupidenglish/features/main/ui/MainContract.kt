package io.taptap.stupidenglish.features.main.ui

import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState

class MainContract {
    sealed class Event : ViewEvent {
        data class OnTabSelected(val item: NavigationKeys.BottomNavigationScreen) : Event()
        data class ChangeBottomSheetVisibility(val visibility: Boolean) : Event()
    }

    data class State(
        val isBottomBarShown: Boolean,
        val bottomBarTabs: List<NavigationKeys.BottomNavigationScreen>
    ) : ViewState

    sealed class Effect : ViewSideEffect {

        sealed class Navigation : Effect() {
            data class OnTabSelected(val route: String) : Navigation()
        }
    }
}