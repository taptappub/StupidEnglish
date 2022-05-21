package io.taptap.stupidenglish.features.auth.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.auth.data.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel<AuthContract.Event, AuthContract.State, AuthContract.Effect>() {

    override fun setInitialState() = AuthContract.State()

    override suspend fun handleEvents(event: AuthContract.Event) {
        when (event) {
            is AuthContract.Event.OnSignIn -> TODO()
            is AuthContract.Event.OnSignInClick -> TODO()
            is AuthContract.Event.OnSkipClick -> TODO()
        }
    }

}