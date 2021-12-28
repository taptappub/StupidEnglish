package io.taptap.stupidenglish.features.words.ui

import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewState
import io.taptap.stupidenglish.features.sentences.navigation.SentenceNavigation

class WordListContract {
    sealed class Event : ViewEvent {
        object OnAddWordClick : Event()
        object OnOnboardingClick : Event()
    }

    data class State(
        val wordList: List<WordListListModels> = listOf(),
        val isLoading: Boolean = false
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class GetRandomWordsError(val errorRes: Int) : Effect()
        data class GetWordsError(val errorRes: Int) : Effect()

        sealed class Navigation : Effect() {
            object ToAddWord : Navigation()
            data class ToAddSentence(val sentenceNavigation: SentenceNavigation) : Navigation()
        }
    }
}
