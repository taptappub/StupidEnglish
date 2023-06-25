package io.taptap.stupidenglish.features.main.ui

import android.content.Intent
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.uikit.group.GroupListItemsModel

class MainContract {
    sealed class Event : ViewEvent {
        data class OnTabSelected(val item: NavigationKeys.BottomNavigationScreen) : Event()
        data class ChangeBottomSheetVisibility(val visibility: Boolean) : Event()
        data class OnNewIntent(val intent: Intent) : Event()
    }

    data class State(
        val isBottomBarShown: Boolean,
        val bottomBarTabs: List<NavigationKeys.BottomNavigationScreen>
    ) : ViewState

    sealed class Effect : ViewSideEffect {

        sealed class Navigation : Effect() {
            data class OnTabSelected(val route: String) : Navigation()
            data class ToAddWord(val word: String = "") : Navigation()
            data class ToFlashCards(val group: GroupListItemsModel) : Navigation()
        }
    }
}