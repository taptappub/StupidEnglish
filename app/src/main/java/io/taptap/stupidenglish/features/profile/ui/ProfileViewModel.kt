package io.taptap.stupidenglish.features.profile.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.taptap.stupidenglish.BuildConfig
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.BaseViewModel
import io.taptap.stupidenglish.base.logic.share.ShareUtil
import io.taptap.stupidenglish.features.profile.data.ProfileRepository
import io.taptap.uikit.prefs.ThemeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import taptap.pub.doOnError
import taptap.pub.handle
import javax.inject.Inject

private const val GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=io.taptap.stupidenglish"
private const val FEEDBACK_URL = "taptap4pub@gmail.com"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val shareUtil: ShareUtil,
) : BaseViewModel<ProfileContract.Event, ProfileContract.State, ProfileContract.Effect>() {

    init {
        viewModelScope.launch(Dispatchers.IO) { observeSavedUser() }
        viewModelScope.launch(Dispatchers.IO) { observeTheme() }
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
                setEffect { ProfileContract.Effect.SignInWithGoogle }
            is ProfileContract.Event.OnSignIn -> {
                if (event.authResult == null) {
                    Log.i("ProfileViewModel", "event.authResult.idpResponse is null")
                    return
                }

                signIn(email = event.authResult.idpResponse?.email)
            }
            is ProfileContract.Event.OnSwitchModeClick -> {
                val newMode = !viewState.value.isDarkMode
                changeTheme(newMode)
            }
            is ProfileContract.Event.OnLogoutClick ->
                setEffect { ProfileContract.Effect.Logout }
            is ProfileContract.Event.OnTermAndConditionsClick ->
                setEffect { ProfileContract.Effect.Navigation.GoToTermsAndConditions }
            is ProfileContract.Event.OnLogout -> {
                logout()
            }
            is ProfileContract.Event.OnRateUsClick ->
                setEffect { ProfileContract.Effect.RateUs(GOOGLE_PLAY_URL) }
            ProfileContract.Event.OnShareUsClick ->
                shareUtil.share(GOOGLE_PLAY_URL, null)
            ProfileContract.Event.OnFeedBackClick -> {
                val text = "Build version : ${BuildConfig.VERSION_NAME}\n" +
                        "Android version : ${android.os.Build.VERSION.SDK_INT}\n" +
                        "Phone model : ${android.os.Build.MODEL}\n\n" +
                        "<Your text>"

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(FEEDBACK_URL))
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                setEffect { ProfileContract.Effect.FeedBack(intent) }
            }
        }
    }

    private fun changeTheme(isDarkMode: Boolean) {
        repository.theme = when(isDarkMode) {
            true -> ThemeType.DARK
            false -> ThemeType.LIGHT
        }

        setState { copy(isDarkMode = isDarkMode) }
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

    //todo переделать на новый способ хранения, там есть Flow
    private suspend fun observeTheme() {
        val isDarkMode = when (repository.theme) {
            ThemeType.LIGHT -> false
            ThemeType.DARK -> true
        }

        setState { copy(isDarkMode = isDarkMode) }
    }

    private suspend fun observeSavedUser() {
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