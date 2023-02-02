package io.taptap.stupidenglish.features.addword.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.uikit.group.GroupListItemsModel

class AddWordContract {
    sealed class Event : ViewEvent {
        object OnWord : Event()
        object OnDescription : Event()
        object OnBackClick: Event()
        object OnNewWord: Event()

        data class OnGroupSelect(val item: GroupListItemsModel) : Event()

        object BackToNoneState : Event()
        object BackToWordState : Event()

        object OnAddGroupClick : Event()
        object OnApplyGroup : Event()
        object OnGroupAddingCancel : Event()

        object OnWaitingDescriptionError : Event()
    }

    data class State(
        val groups: List<GroupListItemsModel>,
        val isAddGroup: Boolean,
        val addWordState: AddWordState,
        val selectedGroups: List<GroupListItemsModel>,
    ) : ViewState

    enum class AddWordState {
        None,
        HasWord,
        HasDescription
    }

    sealed class Effect : ViewSideEffect {
        object HideBottomSheet : Effect()
        object ShowBottomSheet : Effect()

        data class GetGroupsError(val errorRes: Int) : Effect()

        data class SaveError(val errorRes: Int) : Effect()
        data class WaitingForDescriptionError(val errorRes: Int) : Effect()
        data class GetWordsError(val errorRes: Int) : Effect()


        sealed class Navigation : Effect() {
            object BackToWordList : Navigation()
        }
    }
}