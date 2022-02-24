package io.taptap.stupidenglish.features.sentences.ui

sealed class SentencesListListModels(open val id: Long)

data class SentencesListItemUI(
    override val id: Long,
    val sentence: String
) : SentencesListListModels(id)

data class SentencesListTitleUI(
    val valueRes: Int
) : SentencesListListModels(-1)
