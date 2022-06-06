package io.taptap.stupidenglish.features.splash.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.splash.data.SplashRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import taptap.pub.handle
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: SplashRepository
) : BaseViewModel<SplashContract.Event, SplashContract.State, SplashContract.Effect>() {

    private var nextScreen: NextScreen = NextScreen.MAIN

    init {
        viewModelScope.launch(Dispatchers.IO) { findNextScreen() }
    }

    override fun setInitialState() = SplashContract.State(
        list = listOf("Language", "English","Spanish", "French", "Russian", "English","Spanish", "French", "Russian", "English", "Language"),
        startAnimationDelay = 300,
        startAnimationDuration = 3000,
        endAnimationDelay = 1000
    )

    override suspend fun handleEvents(event: SplashContract.Event) {
        when (event) {
            SplashContract.Event.OnAnimationEnd -> {
                delay(viewState.value.endAnimationDelay)
                showNextScreen(nextScreen)
            }
        }
    }

    private suspend fun findNextScreen() {
        if (repository.isRegistrationAsked) {
            showNextScreen(NextScreen.MAIN)
        } else {
            repository.isRegistrationAsked = true
            repository.observeSavedUser()
                .handle(
                    success = { userFlow ->
                        userFlow.collect { user ->
                            nextScreen = if (user != null) {
                                NextScreen.MAIN
                            } else {
                                NextScreen.AUTH
                            }
                        }
                    },
                    error = {
                        Log.e("SplashViewModel", "showNextScreen exception $it")
                        nextScreen = NextScreen.MAIN
                    }
                )
        }
    }

    private suspend fun showNextScreen(nextScreen: NextScreen) {
        val effect = when (nextScreen) {
            NextScreen.MAIN -> SplashContract.Effect.Navigation.ToWordListScreen
            NextScreen.AUTH -> SplashContract.Effect.Navigation.ToAuthScreen
        }
        setEffect { effect }
    }

    private enum class NextScreen {
        MAIN,
        AUTH
    }
}
