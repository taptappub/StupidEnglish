package io.taptap.stupidenglish.features.auth.ui

import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState

class AuthContract {
    sealed class Event : ViewEvent {
        data class OnSignIn(val authResult: FirebaseAuthUIAuthenticationResult?) : Event()

        object OnSignInClick : Event()
        object OnSkipClick : Event()
    }

    class State : ViewState

    sealed class Effect : ViewSideEffect {
        object SignInWithGoogle : Effect()

        sealed class Navigation : Effect() {
            object GoToWordsList : Navigation()
        }
    }
}
