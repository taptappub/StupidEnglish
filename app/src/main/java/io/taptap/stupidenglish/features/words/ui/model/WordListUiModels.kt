package io.taptap.stupidenglish.features.words.ui.model

import io.taptap.uikit.group.GroupListItemsModel

sealed class WordListListModels(open val id: Long)

data class WordListItemUI(
    override val id: Long,
    val word: String,
    val description: String
) : WordListListModels(id)

data class WordListEmptyUI(
    val descriptionRes: Int
) : WordListListModels(-4)

data class WordListGroupUI(
    val titleRes: Int,
    val buttonRes: Int,
    val groups: List<GroupListItemsModel>
) : WordListListModels(-3)

data class WordListTitleUI(
    val valueRes: Int
) : WordListListModels(-1)

data class WordListDynamicTitleUI(
    val currentGroup: GroupListItemsModel
) : WordListListModels(-5)

object OnboardingWordUI : WordListListModels(-2)