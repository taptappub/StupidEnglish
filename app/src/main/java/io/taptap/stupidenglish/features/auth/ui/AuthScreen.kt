package io.taptap.stupidenglish.features.auth.ui

import android.content.Context
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.taptap.authorisation.rememberAuthenticator
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun AuthScreen(
    context: Context,
    state: AuthContract.State,
    effectFlow: Flow<AuthContract.Effect>?,
    onEventSent: (event: AuthContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: AuthContract.Effect.Navigation) -> Unit
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    val authenticator = rememberAuthenticator(context)
    authenticator.login {
        onEventSent(AuthContract.Event.OnSignIn(it))
    }

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is AuthContract.Effect.Navigation.GoToWordsList ->
                    onNavigationRequested(effect)
                is AuthContract.Effect.SignInWithGoogle -> {
                    authenticator.launch()
                }
            }
        }?.collect()
    }
}