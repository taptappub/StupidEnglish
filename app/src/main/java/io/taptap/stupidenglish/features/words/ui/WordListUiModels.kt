package io.taptap.stupidenglish.features.words.ui

sealed interface WordListListModels

data class WordListItemUI(
    val word: String,
    val description: String
) : WordListListModels

data class WordListTitleUI(
    val valueRes: Int
) : WordListListModels

object OnboardingWordUI : WordListListModels