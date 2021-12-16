package io.taptap.stupidenglish.features.main.ui

sealed interface MainListListModels

data class WordListItemUI(
    val id: String,
    val word: String,
    val description: String
) : MainListListModels

data class TitleUI(
    val valueRes: Int
) : MainListListModels

data class NewWordUI(
    val valueRes: Int
) : MainListListModels