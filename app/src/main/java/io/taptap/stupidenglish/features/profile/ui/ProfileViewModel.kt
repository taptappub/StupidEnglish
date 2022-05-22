package io.taptap.stupidenglish.features.profile.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.features.profile.data.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import taptap.pub.doOnError
import taptap.pub.handle
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : BaseViewModel<ProfileContract.Event, ProfileContract.State, ProfileContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) { getSavedUser() }
    }

    override fun setInitialState() = ProfileContract.State(
        name = "",
        avatar = "",
        isDarkMode = false,
        isRegistered = false
    )

    override suspend fun handleEvents(event: ProfileContract.Event) {
        when (event) {
            is ProfileContract.Event.OnBackClick ->
                setEffect { ProfileContract.Effect.Navigation.BackToWordsList }
            is ProfileContract.Event.OnSignInClick ->
                setEffect { ProfileContract.Effect.SighInWithGoogle }
            is ProfileContract.Event.OnSignIn -> {
                if (event.authResult == null) {
                    Log.i("ProfileViewModel", "event.authResult.idpResponse is null")
                    return
                }

                signIn(email = event.authResult.idpResponse?.email)
            }

            is ProfileContract.Event.OnSwitchModeClick -> {
                val darkMode = viewState.value.isDarkMode
                setState { copy(isDarkMode = !darkMode) }
            }
            is ProfileContract.Event.OnLogoutClick ->
                setEffect { ProfileContract.Effect.Logout }

            is ProfileContract.Event.OnTermAndConditionsClick -> TODO()
            is ProfileContract.Event.OnLogout -> {
                logout()
            }
        }
    }

    private suspend fun logout() {
        repository.clearUser()
            .doOnError {
                Log.e("ProfileViewModel", "logout Can't clear user")
            }
        setState { copy(name = "", avatar = "", isRegistered = false) }
    }

    private suspend fun signIn(email: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user?.email != email) {
            Log.i("ProfileViewModel", "current email != selected email")
            return
        }

        val name = user?.displayName ?: ""
        val image = user?.photoUrl.toString() ?: ""
        val email = user?.email ?: ""
        val isEmailVerified = user?.isEmailVerified
        val phoneNumber = user?.phoneNumber
        val uid = user?.uid ?: ""

        setState { copy(name = name, avatar = image, isRegistered = true) }

        repository.saveUser(name, image, email, isEmailVerified, phoneNumber, uid)
            .doOnError {
                Log.e("ProfileViewModel", "User was not saved")
            }
    }

    private suspend fun getSavedUser() {
        repository.observeSavedUser()
            .handle(
                success = { userFlow ->
                    userFlow.collect { user ->
                        if (user != null) {
                            setState {
                                copy(
                                    name = user.name,
                                    avatar = user.avatar,
                                    isRegistered = true
                                )
                            }
                        } else {
                            setState { copy(name = "", avatar = "", isRegistered = false) }
                        }
                    }
                },
                error = {
                    setEffect { ProfileContract.Effect.GetUserError(R.string.prod_get_user_error) }
                }
            )
    }
}