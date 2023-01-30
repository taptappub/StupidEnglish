package io.taptap.stupidenglish.base.ui.helpers

import io.taptap.stupidenglish.base.logic.sources.groups.read.IReadGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.write.IWriteGroupsDataSource
import io.taptap.stupidenglish.base.model.Group
import io.taptap.uikit.group.GroupItemUI
import io.taptap.uikit.group.GroupListItemsModels
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import taptap.pub.doOnComplete
import taptap.pub.handle
import javax.inject.Singleton

@Singleton
class GroupViewModelHelper(
    private val readGroupsDataSource: IReadGroupsDataSource,
    private val writeGroupsDataSource: IWriteGroupsDataSource
) : IGroupViewModelHelper {

    private lateinit var coroutineScope: CoroutineScope

    override fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    override fun getGroupsList(
        onError: (exception: Exception) -> Unit,
        collect: (groups: List<GroupListItemsModels>) -> Unit
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            readGroupsDataSource.observeGroupList()
                .handle(
                    success = { groupList ->
                        groupList.collect { list ->
                            val groups = makeGroupsList(list)
                            collect(groups)
                        }
                    },
                    error = {
                        onError(it)
                    }
                )
        }
    }

    private fun makeGroupsList(groupsList: List<Group>): List<GroupListItemsModels> {
        val groupList = mutableListOf<GroupListItemsModels>()
        groupList.add(NoGroup)

        groupList.addAll(groupsList.map {
            GroupItemUI(
                id = it.id,
                name = it.name
            )
        })

        return groupList
    }

    override fun saveGroup(group: String, doOnComplete: () -> Unit) {
        val trimGroup = group.trim()
        coroutineScope.launch(Dispatchers.IO) {
            writeGroupsDataSource.saveGroup(trimGroup)
                .doOnComplete(doOnComplete)
        }
    }
}
