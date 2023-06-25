package io.taptap.stupidenglish.features.groups.ui

import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState
import io.taptap.uikit.group.GroupListItemsModel
import io.taptap.uikit.group.GroupListModel

class GroupListContract {
    sealed class Event : ViewEvent {
        data class OnDismiss(val item: GroupListItemsModel) : Event()
        object OnRecover : Event()
        object OnApplyDismiss : Event()
        data class OnRecovered(val item: GroupListItemsModel) : Event()

        object OnBackClick: Event()

        data class OnGroupClick(val group: GroupListItemsModel) : Event()
        data class OnShareClick(val group: GroupListItemsModel) : Event()
    }

    data class State(
        val groups: List<GroupListModel>,
        val isLoading: Boolean = false,
        val deletedGroupsIds: MutableList<Long>
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class GetGroupsError(val errorRes: Int) : Effect()
        object ShowRecover : Effect()

        sealed class Navigation : Effect() {
            object BackToWordList : Navigation()
            data class ToGroupDetails(val group: GroupListItemsModel) : Navigation()
        }
    }
}
