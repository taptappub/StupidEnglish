package io.taptap.stupidenglish.features.words.ui.model

import io.taptap.stupidenglish.base.logic.groups.GroupListModels

sealed class WordListListModels(open val id: Long)

data class WordListItemUI(
    override val id: Long,
    val word: String,
    val description: String,
    val groupsIds: List<Long>
) : WordListListModels(id)

data class WordListEmptyUI(
    val descriptionRes: Int
) : WordListListModels(-4)

data class WordListGroupUI(
    val titleRes: Int,
    val buttonRes: Int,
    val groups: List<GroupListModels>
) : WordListListModels(-3)

data class WordListTitleUI(
    val valueRes: Int
) : WordListListModels(-1)

object OnboardingWordUI : WordListListModels(-2)