package io.taptap.stupidenglish.features.sentences.ui

sealed interface SentencesListListModels

data class SentencesListItemUI(
    val id: String,
    val sentence: String
) : SentencesListListModels

data class SentencesListTitleUI(
    val valueRes: Int
) : SentencesListListModels

data class SentencesListNewSentenceUI(
    val valueRes: Int
) : SentencesListListModels