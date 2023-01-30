package io.taptap.stupidenglish.features.addword.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.uikit.group.GroupListItemsModels
import io.taptap.uikit.group.GroupListModels

class AddWordContract {
    sealed class Event : ViewEvent {
        object OnWord : Event()
        object OnBackClick: Event()

        data class OnGroupSelect(val item: GroupListItemsModels) : Event()

        object BackToNoneState : Event()

        object OnSaveWord : Event()

        object OnChooseGroupBottomSheetCancel : Event()
        object OnGroupsChosenConfirmClick : Event()
        object OnGroupsClick : Event()

        object OnWaitingDescriptionError : Event()
    }

    data class State(
        val groups: List<GroupListItemsModels>,
        val addWordState: AddWordState,
        val selectedGroups: List<GroupListItemsModels>,
        val dialogSelectedGroups: List<GroupListItemsModels>
    ) : ViewState

    enum class AddWordState {
        None,
        HasWord
    }

    sealed class Effect : ViewSideEffect {
        data class SaveError(val errorRes: Int) : Effect()
        data class WaitingForDescriptionError(val errorRes: Int) : Effect()
        data class GetWordsError(val errorRes: Int) : Effect()

        object HideChooseGroupBottomSheet : Effect()
        object ShowChooseGroupBottomSheet : Effect()

        sealed class Navigation : Effect() {
            object BackToWordList : Navigation()
        }
    }
}