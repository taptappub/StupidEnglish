package io.taptap.stupidenglish.features.groups.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.archive.features.sentences.ui.SentencesListEmptyUI
import io.taptap.stupidenglish.archive.features.sentences.ui.SentencesListItemUI
import io.taptap.stupidenglish.archive.features.sentences.ui.SentencesListTitleUI
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.features.addword.ui.AddWordContract
import io.taptap.stupidenglish.features.groups.data.GroupListRepository
import io.taptap.uikit.group.GroupItemUI
import io.taptap.uikit.group.GroupListItemsModel
import io.taptap.uikit.group.GroupListModel
import io.taptap.uikit.group.GroupListTitleUI
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import taptap.pub.handle
import taptap.pub.takeOrReturn
import javax.inject.Inject

@HiltViewModel
class GroupListViewModel @Inject constructor(
    private val repository: GroupListRepository
) : BaseViewModel<GroupListContract.Event, GroupListContract.State, GroupListContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getGroupList()
        }
    }

    override fun setInitialState() = GroupListContract.State(
        groups = emptyList(),
        isLoading = true,
        deletedGroupsIds = mutableListOf()
    )

    override suspend fun handleEvents(event: GroupListContract.Event) {
        when (event) {
            GroupListContract.Event.OnApplyDismiss -> {
                deleteGroup()
            }
            is GroupListContract.Event.OnDismiss -> {
                predeleteGroup(event.item)
            }
            GroupListContract.Event.OnRecover -> {
                repository.getGroupList()
                    .handle(
                        success = {
                            val resultGroupList = makeGroupsList(it)
                            makeGroupList(resultGroupList)
                        },
                        error = {
                            setEffect { GroupListContract.Effect.GetGroupsError(R.string.grps_get_groups_error) }
                            setState { copy(deletedGroupsIds = mutableListOf()) }
                        }
                    )
            }
            is GroupListContract.Event.OnRecovered -> {
                setState { copy(deletedGroupsIds = mutableListOf()) }
            }
            is GroupListContract.Event.OnGroupClick -> {
                setEffect { GroupListContract.Effect.Navigation.ToGroupDetails(group = event.group) }
            }
            is GroupListContract.Event.OnShareClick -> error("Не сделал расшаривание")
            is GroupListContract.Event.OnBackClick -> {
                setEffect { GroupListContract.Effect.Navigation.BackToWordList }
            }
        }
    }

    private suspend fun deleteGroup() {
        deleteGroup(viewState.value.deletedGroupsIds)
        setState { copy(deletedGroupsIds = mutableListOf()) }
    }

    private suspend fun deleteGroup(list: List<Long>) {
        repository.removeGroups(list)
    }

    private fun predeleteGroup(item: GroupListItemsModel) {
        val mutableDeletedSentenceIds = viewState.value.deletedGroupsIds.toMutableList()
        mutableDeletedSentenceIds.add(item.id)
        val list = viewState.value.groups.toMutableList()
        list.remove(item)
        setState { copy(groups = list, deletedGroupsIds = mutableDeletedSentenceIds) }
        setEffect { GroupListContract.Effect.ShowRecover }
    }

    private suspend fun getGroupList() {
        val groupList = repository.observeGroupList().takeOrReturn {
            setEffect { GroupListContract.Effect.GetGroupsError(R.string.grps_get_groups_error) }
            return
        }

        groupList.collect {
            val resultGroupList = makeGroupsList(it)
            makeGroupList(resultGroupList)
        }
    }

    private fun makeGroupList(resultGroupList: List<GroupListModel>) {
        setState {
            copy(groups = resultGroupList, isLoading = false)
        }
    }

    private fun makeGroupsList(groupsList: List<Group>): List<GroupListModel> {
        val groupList = mutableListOf<GroupListModel>()

        groupList.add(GroupListTitleUI(valueRes = R.string.grps_list_list_title))

        groupList.add(NoGroup)
        groupList.addAll(groupsList.reversed().map {
            GroupItemUI(
                id = it.id,
                name = it.name
            )
        })

        return groupList
    }
}
