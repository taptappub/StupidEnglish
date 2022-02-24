package io.taptap.stupidenglish.features.words.ui

sealed class WordListListModels(open val id: Long)

data class WordListItemUI(
    override val id: Long,
    val word: String,
    val description: String
) : WordListListModels(id)

data class WordListTitleUI(
    val valueRes: Int
) : WordListListModels(-1)

object OnboardingWordUI : WordListListModels(-2)