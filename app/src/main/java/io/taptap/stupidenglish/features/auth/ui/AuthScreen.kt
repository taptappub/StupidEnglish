package io.taptap.stupidenglish.features.auth.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.authorisation.rememberAuthenticator
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.uikit.AverageText
import io.taptap.uikit.PrimaryButton
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
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
                is AuthContract.Effect.Navigation.BackToWordsList ->
                    onNavigationRequested(effect)
                is AuthContract.Effect.SignInWithGoogle -> {
                    authenticator.launch()
                }
            }
        }?.collect()
    }

    StupidEnglishScaffold(
        scaffoldState = scaffoldState
    ) {
        StupidLanguageBackgroundBox {
            ContentScreen(
                state = state,
                onEventSent = onEventSent
            )
        }
    }
}

@Composable
private fun ContentScreen(
    state: AuthContract.State,
    onEventSent: (event: AuthContract.Event) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (image, title, subtitle, signInButton, skipButton) = createRefs()

        Image(
            painter = painterResource(R.drawable.ic_main_onboarding),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    bottom.linkTo(title.top)
                    start.linkTo(
                        anchor = parent.start,
                        margin = 42.dp,
                    )
                    end.linkTo(
                        anchor = parent.end,
                        margin = (-42).dp,
                        goneMargin = 0.dp
                    )
                }
        )

        Text(
            text = stringResource(id = R.string.app_name),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.displayLarge,
            maxLines = 2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp, end = 28.dp, top = 20.dp)
                .constrainAs(title) {
                    bottom.linkTo(subtitle.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        AverageText(
            text = stringResource(id = R.string.auth_sub_title_text),
            textAlign = TextAlign.Start,
            maxLines = 20,
            selectable = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp, end = 28.dp, top = 12.dp)
                .constrainAs(subtitle) {
                    bottom.linkTo(signInButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        PrimaryButton(
            onClick = { onEventSent(AuthContract.Event.OnSignInClick) },
            text = stringResource(id = R.string.auth_sign_in_button),
            textStyle = MaterialTheme.typography.headlineLarge,
            startImagePainter = painterResource(R.drawable.ic_google),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 42.dp, end = 42.dp, top = 66.dp)
                .constrainAs(signInButton) {
                    bottom.linkTo(skipButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = stringResource(id = R.string.auth_skip_button),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 42.dp, end = 42.dp, top = 24.dp, bottom = 36.dp)
                .clickable { onEventSent(AuthContract.Event.OnSkipClick) }
                .constrainAs(skipButton) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
    }
}
