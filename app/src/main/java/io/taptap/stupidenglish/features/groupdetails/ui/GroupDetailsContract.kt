package io.taptap.stupidenglish.features.groupdetails.ui

import androidx.annotation.StringRes
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsUIModel
import io.taptap.stupidenglish.features.groupdetails.ui.model.GroupDetailsWordItemUI
import io.taptap.uikit.group.GroupListItemsModel

class GroupDetailsContract {
    sealed class Event : ViewEvent {
        data class OnDismiss(val item: GroupDetailsWordItemUI) : Event()

        data class OnRecovered(val item: GroupDetailsWordItemUI) : Event()
        object OnRecover : Event()
        object OnApplyDismiss : Event()

        object OnBackClick : Event()

        object OnAddWordClick : Event()
        object OnImportWordsClick : Event()
        object OnRemoveGroupClick : Event()
        object ToFlashCards : Event()
        object ToAddSentence : Event()
    }

    data class State(
        val group: GroupListItemsModel,
        val wordList: List<GroupDetailsUIModel>,
        val isLoading: Boolean = false,
        val deletedWordIds: MutableList<Long>
    ) : ViewState

    /*enum class BottomNavigationScreen(
        val route: String,
        val title: Int,
        @DrawableRes val icon: Int
    ) {
        SE_SENTENCES(
            route = ArchiveNavigationKeys.Route.SE_SENTENCES,
            title = R.string.main_bottom_bar_sentences_title,
            icon = R.drawable.ic_cloud_icon
        )
    }*/
    object ButtonId {
        const val flashcards: Int = 0
        const val learn: Int = 1
        const val share: Int = 2
        const val remove: Int = 3
    }

    sealed class Effect : ViewSideEffect {
        data class GetWordsError(@StringRes val errorRes: Int) : Effect()
        data class GetGroupsError(@StringRes val errorRes: Int) : Effect()
        object ShowRecover : Effect()

        sealed class Navigation : Effect() {
            object BackTo : Navigation()

            data class ToAddWordWithGroup(val group: GroupListItemsModel) : Navigation()
            data class ToFlashCards(val group: GroupListItemsModel) : Navigation()
            data class ToAddSentence(val group: GroupListItemsModel) : Navigation()
            data class ToImportWords(val group: GroupListItemsModel) : Navigation()
        }
    }
}
