package io.taptap.stupidenglish.features.auth.ui

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.auth.data.AuthRepository
import taptap.pub.doOnError
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel<AuthContract.Event, AuthContract.State, AuthContract.Effect>() {

    override fun setInitialState() = AuthContract.State()

    override suspend fun handleEvents(event: AuthContract.Event) {
        when (event) {
            is AuthContract.Event.OnSignInClick ->
                setEffect { AuthContract.Effect.SignInWithGoogle }
            is AuthContract.Event.OnSignIn -> {
                if (event.authResult == null) {
                    Log.i("ProfileViewModel", "event.authResult.idpResponse is null")
                    return
                }

                signIn(email = event.authResult.idpResponse?.email)
            }
            is AuthContract.Event.OnSkipClick ->
                setEffect { AuthContract.Effect.Navigation.BackToWordsList }
        }
    }

    private suspend fun signIn(email: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user?.email != email) {
            Log.i("AuthViewModel", "current email != selected email")
            return
        }

        val name = user?.displayName ?: ""
        val image = user?.photoUrl.toString() ?: ""
        val email = user?.email ?: ""
        val isEmailVerified = user?.isEmailVerified
        val phoneNumber = user?.phoneNumber
        val uid = user?.uid ?: ""

        repository.saveUser(name, image, email, isEmailVerified, phoneNumber, uid)
            .doOnError {
                Log.e("AuthViewModel", "signIn error ${it.message}")
            }

        setEffect { AuthContract.Effect.Navigation.BackToWordsList }
    }
}