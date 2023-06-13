package io.taptap.stupidenglish.features.main.ui

import android.content.Intent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.main.data.MainRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : BaseViewModel<MainContract.Event, MainContract.State, MainContract.Effect>() {

    override fun setInitialState() = MainContract.State(
        isBottomBarShown = true,
        bottomBarTabs = listOf(
            NavigationKeys.BottomNavigationScreen.SE_WORDS,
            NavigationKeys.BottomNavigationScreen.SE_SETS
        )
    )

    override suspend fun handleEvents(event: MainContract.Event) {
        when (event) {
            is MainContract.Event.OnTabSelected -> {
                val route = event.item.route
                setEffect { MainContract.Effect.Navigation.OnTabSelected(route) }
            }
            is MainContract.Event.ChangeBottomSheetVisibility ->
                setState { copy(isBottomBarShown = event.visibility) }

            is MainContract.Event.OnNewIntent -> {
                val word: String? = event.intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
                if (word != null) {
                    setEffect { MainContract.Effect.Navigation.ToAddWord(word) }
                }
            }
        }
    }
}
