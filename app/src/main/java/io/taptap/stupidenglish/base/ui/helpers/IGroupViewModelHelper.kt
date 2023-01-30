package io.taptap.stupidenglish.base.ui.helpers

import io.taptap.uikit.group.GroupListItemsModels
import kotlinx.coroutines.CoroutineScope

interface IGroupViewModelHelper {
    fun setCoroutineScope(coroutineScope: CoroutineScope)
    fun getGroupsList(
        onError: (exception: Exception) -> Unit,
        collect: (groups: List<GroupListItemsModels>) -> Unit
    )
    fun saveGroup(group: String, doOnComplete: () -> Unit)
}