package io.taptap.stupidenglish.features.splash.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.splash.data.SplashRepository
import io.taptap.stupidenglish.features.words.ui.WordListContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import taptap.pub.handle
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: SplashRepository
) : BaseViewModel<SplashContract.Event, SplashContract.State, SplashContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) { showNextScreen() }
    }

    override fun setInitialState() = SplashContract.State()

    override suspend fun handleEvents(event: SplashContract.Event) {
    }

    private suspend fun showNextScreen() {
        repository.observeSavedUser()
            .handle(
                success = { userFlow ->
                    userFlow.collect { user ->
                        if (user != null) {
                            setEffect { SplashContract.Effect.Navigation.ToWordListScreen }
                        } else {
                            if (!repository.isRegistrationAsked) {
                                repository.isRegistrationAsked = true
                                setEffect { SplashContract.Effect.Navigation.ToAuthScreen }
                            } else {
                                setEffect { SplashContract.Effect.Navigation.ToWordListScreen }
                            }
                        }
                    }
                },
                error = {
                    Log.e("SplashViewModel", "showNextScreen exception $it")
                    setEffect { SplashContract.Effect.Navigation.ToWordListScreen }
                }
            )
    }

}
