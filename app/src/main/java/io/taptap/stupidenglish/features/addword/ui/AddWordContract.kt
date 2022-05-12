package io.taptap.stupidenglish.features.addword.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.base.logic.sources.groups.read.GroupListModels

class AddWordContract {
    sealed class Event : ViewEvent {
        object OnWord : Event()

        data class OnWordChanging(val value: String) : Event()
        data class OnDescriptionChanging(val value: String) : Event()
        data class OnGroupSelect(val item: GroupListModels) : Event()

        object BackToNoneState : AddWordContract.Event()

        object OnSaveWord : Event()

        object OnChooseGroupBottomSheetCancel : Event()
        object OnGroupsChosenConfirmClick : Event()
        object OnGroupsClick : Event()

        object OnWaitingDescriptionError : Event()
    }

    data class State(
        val word: String,
        val description: String,
        val groups: List<GroupListModels>,
        val addWordState: AddWordState,
        val selectedGroups: List<GroupListModels>,
        val dialogSelectedGroups: List<GroupListModels>
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