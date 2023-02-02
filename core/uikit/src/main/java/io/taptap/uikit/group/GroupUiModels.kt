package io.taptap.uikit.group

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.taptap.uikit.R

open class GroupListModel(
    open val id: Long
)

object PlusGroup : GroupListModel(id = -2)

data class GroupListTitleUI(
    val valueRes: Int
) : GroupListModel(-3)

sealed class GroupListItemsModel(
    override val id: Long
) : GroupListModel(id)

data class GroupItemUI(
    override val id: Long,
    val name: String
) : GroupListItemsModel(id)

data class NoGroupItemUI(
    override val id: Long,
    val titleRes: Int
) : GroupListItemsModel(id)

val NoGroup = NoGroupItemUI(
    id = -1,
    titleRes = R.string.word_group_no_group_name
)



@Composable
fun GroupListItemsModel.getTitle():String {
    return when (this) {
        is GroupItemUI -> this.name
        is NoGroupItemUI -> stringResource(id = this.titleRes)
    }
}
