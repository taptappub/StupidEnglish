package io.taptap.stupidenglish.archive.features.sentences.ui

import io.taptap.stupidenglish.features.words.ui.model.WordListListModels

sealed class SentencesListListModels(open val id: Long)

data class SentencesListItemUI(
    override val id: Long,
    val sentence: String
) : SentencesListListModels(id)

data class SentencesListEmptyUI(
    val descriptionRes: Int
) : SentencesListListModels(-2)

data class SentencesListTitleUI(
    val valueRes: Int
) : SentencesListListModels(-1)
