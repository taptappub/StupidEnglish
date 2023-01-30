package io.taptap.uikit.group

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.taptap.uikit.R

sealed class GroupListModels(
    open val id: Long
)

object PlusGroup : GroupListModels(id = -2)

sealed class GroupListItemsModels(
    override val id: Long
) : GroupListModels(id)

data class GroupItemUI(
    override val id: Long,
    val name: String
) : GroupListItemsModels(id)

data class NoGroupItemUI(
    override val id: Long,
    val titleRes: Int
) : GroupListItemsModels(id)

val NoGroup = NoGroupItemUI(
    id = -1,
    titleRes = R.string.word_group_no_group_name
)

@Composable
fun GroupListItemsModels.getTitle():String {
    return when (this) {
        is GroupItemUI -> this.name
        is NoGroupItemUI -> stringResource(id = this.titleRes)
    }
}
