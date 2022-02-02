package io.taptap.stupidenglish.features.main.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.main.data.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.map
import taptap.pub.takeOrNull
import javax.inject.Inject

private const val WORDS_FOR_MOTIVATION = 3

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: MainRepository
) : BaseViewModel<MainContract.Event, MainContract.State, MainContract.Effect>() {

    private var lastWordsCount = -1

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TAGGG", "MainViewModel init")

            val pageId = stateHandle.get<String>(NavigationKeys.Arg.PAGE_ID)?.toInt() ?: 0
            val isFirstStart = repository.isFirstStart
            setState {
                copy(
                    isShownGreetings = isFirstStart
                )
            }

            val wordsCountFlow = repository.observeWords().takeOrNull()
            wordsCountFlow?.collect { words ->
                val size = words.size
                val pagerIsVisible = words.isNotEmpty()

                setState {
                    copy(
                        pageId = pageId,
                        pagerIsVisible = pagerIsVisible
                    )
                }
                repository.isSentenceMotivationShown = false
                if (lastWordsCount < size && !repository.isSentenceMotivationShown) { //todo придумать время мотивации
                    delay(2000)
                    setState { copy(timeToShowMotivationToSentence = size % WORDS_FOR_MOTIVATION == 0) }
                    lastWordsCount = size
                }
            }
        }
    }

    override fun setInitialState() =
        MainContract.State(
            pagerIsVisible = false,
            pageId = 0,
            isShownGreetings = false,
            timeToShowMotivationToSentence = false
        )

    override fun handleEvents(event: MainContract.Event) {
        when (event) {
            MainContract.Event.OnGreetingsClose -> {
                repository.isFirstStart = false
                setState { copy(isShownGreetings = false) }
            }
            MainContract.Event.OnMotivationConfirmClick -> {
                setState { copy(timeToShowMotivationToSentence = false) }
                viewModelScope.launch(Dispatchers.IO) {
                    repository.isSentenceMotivationShown = true

                    val randomWords = getRandomWords()
                    withContext(Dispatchers.Main) {
                        if (randomWords != null) {
                            setEffect {
                                MainContract.Effect.Navigation.ToAddSentence(randomWords)
                            }
                        }
                    }
                }
            }
            MainContract.Event.OnMotivationDeclineClick -> {
                setState { copy(timeToShowMotivationToSentence = false) }

                viewModelScope.launch(Dispatchers.IO) {
                    repository.isSentenceMotivationShown = true
                }

                setEffect { MainContract.Effect.CloseMotivation }
            }
            MainContract.Event.OnMotivationCancel -> {
                setState { copy(timeToShowMotivationToSentence = false) }

                viewModelScope.launch(Dispatchers.IO) {
                    repository.isSentenceMotivationShown = true
                }
            }
        }
    }

    private suspend fun getRandomWords(): List<Long>? {
        return repository.getRandomWords(3)
            .map { list ->
                list.map { it.id }
            }
            .takeOrNull()
    }
}