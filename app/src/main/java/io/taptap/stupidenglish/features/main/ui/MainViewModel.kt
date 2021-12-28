package io.taptap.stupidenglish.features.main.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.main.data.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taptap.pub.takeOrNull
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: MainRepository
) : BaseViewModel<MainContract.Event, MainContract.State, MainContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TAGGG", "MainViewModel init")
            val pageId = stateHandle.get<String>(NavigationKeys.Arg.PAGE_ID)?.toInt() ?: 0

            val wordsCountFlow = repository.observeWordsCount().takeOrNull()
            wordsCountFlow?.collect { words ->
                val pagerIsVisible = words.size >= 3
                setState { copy(pageId = pageId, pagerIsVisible = pagerIsVisible) }
            }
        }
    }

    override fun setInitialState() =
        MainContract.State(pagerIsVisible = false, pageId = 0)

    override fun handleEvents(event: MainContract.Event) {
    }

}