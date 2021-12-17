package io.taptap.stupidenglish.features.main.ui

sealed interface MainListListModels

data class WordListItemUI(
    val word: String,
    val description: String
) : MainListListModels

data class WordListTitleUI(
    val valueRes: Int
) : MainListListModels

data class NewWordUI(
    val valueRes: Int
) : MainListListModels

object OnboardingWordUI : MainListListModels