package io.taptap.stupidenglish.features.sentences.ui

sealed interface SentencesListListModels

data class SentencesListItemUI(
    val id: Long,
    val sentence: String
) : SentencesListListModels

data class SentencesListTitleUI(
    val valueRes: Int
) : SentencesListListModels
