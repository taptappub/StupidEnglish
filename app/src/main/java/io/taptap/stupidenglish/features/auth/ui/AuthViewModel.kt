package io.taptap.stupidenglish.features.auth.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.auth.data.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import taptap.pub.doOnError
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel<AuthContract.Event, AuthContract.State, AuthContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val isFirstStart = repository.isFirstStart
            setState {
                copy(
                    isShownGreetings = isFirstStart
                )
            }
        }
    }

    override fun setInitialState() = AuthContract.State(
        isShownGreetings = false
    )

    override suspend fun handleEvents(event: AuthContract.Event) {
        when (event) {
            is AuthContract.Event.OnSignInClick ->
                setEffect { AuthContract.Effect.SignInWithGoogle }
            is AuthContract.Event.OnSignIn -> {
                if (event.authResult == null) {
                    Log.i("ProfileViewModel", "event.authResult.idpResponse is null")
                    return
                }

                signIn(event.authResult.idpResponse?.email)
            }
            is AuthContract.Event.OnGreetingsClose -> {
                repository.isFirstStart = false
                setState { copy(isShownGreetings = false) }
            }
            is AuthContract.Event.OnSkipClick ->
                setEffect { AuthContract.Effect.Navigation.ToWordsList }

        }
    }

    private suspend fun signIn(selectedEmail: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.i("AuthViewModel", "user is null")
            return
        }
        if (user.email.isNullOrEmpty()) {
            Log.i("AuthViewModel", "email is null or empty")
            return
        }
        if (user.email != selectedEmail) {
            Log.i("AuthViewModel", "current email != selected email")
            return
        }

        val name = user.displayName ?: ""
        val image = user.photoUrl.toString()
        val email = user.email ?: ""
        val isEmailVerified = user.isEmailVerified
        val phoneNumber = user.phoneNumber
        val uid = user.uid

        repository.saveUser(name, image, email, isEmailVerified, phoneNumber, uid)
            .doOnError {
                Log.e("AuthViewModel", "signIn error ${it.message}")
            }

        setEffect { AuthContract.Effect.Navigation.ToWordsList }
    }
}