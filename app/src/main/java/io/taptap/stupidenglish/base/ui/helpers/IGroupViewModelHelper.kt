package io.taptap.stupidenglish.base.ui.helpers

import io.taptap.uikit.group.GroupListItemsModels
import kotlinx.coroutines.CoroutineScope

//todo зарефачить бы этот класс, а то идея с корутинами внутри Хелпера фигня как мне кажется
interface IGroupViewModelHelper {
    fun setCoroutineScope(coroutineScope: CoroutineScope)
    fun getGroupsList(
        onError: (exception: Exception) -> Unit,
        collect: (groups: List<GroupListItemsModels>) -> Unit
    )
    fun saveGroup(group: String, doOnComplete: () -> Unit)
    fun getGroupsById(ids: List<Long>, doOnSuccess: (List<GroupListItemsModels>) -> Unit)
}