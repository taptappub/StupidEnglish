package io.taptap.stupidenglish.features.groupdetails.ui.model

import io.taptap.uikit.group.GroupListItemsModel

sealed class GroupDetailsUIModel(open val id: Long)

data class GroupDetailsWordItemUI(
    override val id: Long,
    val word: String,
    val description: String
) : GroupDetailsUIModel(id)

data class GroupDetailsEmptyUI(
    val descriptionRes: Int
) : GroupDetailsUIModel(-2)

data class GroupDetailsDynamicTitleUI(
    val currentGroup: GroupListItemsModel
) : GroupDetailsUIModel(-3)

data class GroupDetailsButtonUI(
    val buttonId: Int,
    val valueRes: Int
) : GroupDetailsUIModel(-4)
