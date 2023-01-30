package io.taptap.stupidenglish.features.profile.ui

import android.content.Intent
import androidx.annotation.StringRes
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import io.taptap.stupidenglish.base.ViewEvent
import io.taptap.stupidenglish.base.ViewSideEffect
import io.taptap.stupidenglish.base.ViewState

class ProfileContract {
    sealed class Event : ViewEvent {
        object OnSignInClick : Event()
        data class OnSignIn(val authResult: FirebaseAuthUIAuthenticationResult?) : Event()

        object OnLogoutClick : Event()
        object OnLogout : Event()

        object OnBackClick : Event()

        object OnTermAndConditionsClick : Event()
        object OnRateUsClick : Event()
        object OnSwitchModeClick : Event()
        object OnShareUsClick : Event()
        object OnFeedBackClick : Event()
    }

    data class State(
        val name: String,
        val avatar: String,
        val isDarkMode: Boolean,
        val isRegistered: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        object SignInWithGoogle : Effect()
        object Logout : Effect()
        data class RateUs(val url: String) : Effect()
        data class GetUserError(@StringRes val errorRes: Int) : Effect()
        data class FeedBack(val intent: Intent) : Effect()

        sealed class Navigation : Effect() {
            object BackToWordsList : Navigation()
            object GoToTermsAndConditions : Navigation()
        }
    }
}