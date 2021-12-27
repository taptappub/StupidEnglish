package io.taptap.stupidenglish.features.main.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.main.data.MainRepository
import kotlinx.coroutines.launch
import taptap.pub.takeOrNull
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: MainRepository
) : BaseViewModel<MainContract.Event, MainContract.State, MainContract.Effect>() {

    init {
        viewModelScope.launch {
            val pageId = stateHandle.get<String>(NavigationKeys.Arg.PAGE_ID)?.toInt() ?: 0

            val wordsCount = repository.getWordsCount().takeOrNull()
            val pagerIsVisible = pageId == 1 || (wordsCount != null && wordsCount >= 3)
            setState { copy(pageId = pageId, pagerIsVisible = pagerIsVisible) }
        }
    }

    override fun setInitialState() =
        MainContract.State(pagerIsVisible = false, pageId = 0)

    override fun handleEvents(event: MainContract.Event) {
    }

}