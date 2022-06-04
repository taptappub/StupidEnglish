package io.taptap.stupidenglish.features.auth.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.taptap.authorisation.rememberAuthenticator
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.uikit.AverageText
import io.taptap.uikit.PrimaryButton
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.theme.StupidEnglishTheme
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
                is AuthContract.Effect.Navigation.ToWordsList ->
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

            if (state.isShownGreetings) {
                AlertDialog(
                    onDismissRequest = {
                        onEventSent(AuthContract.Event.OnGreetingsClose)
                    },
                    text = {
                        AverageText(
                            text = stringResource(id = R.string.auth_dialog_message),
                            maxLines = 100
                        )
                    },
                    confirmButton = {
                        PrimaryButton(
                            text = stringResource(id = R.string.auth_dialog_ok),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            onEventSent(AuthContract.Event.OnGreetingsClose)
                        }
                    }
                )
            }

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
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.ic_main_onboarding),
            alignment = Alignment.TopStart,
            contentScale = ContentScale.FillHeight,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 42.dp)
                .weight(1.0f)
        )

        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp * 52

        val titleSize = (screenHeight / 760).value.sp
//            52/720 = x/screenHeight

        Column(
            modifier = Modifier
                .weight(1.0f)
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.displayLarge,
                fontSize = titleSize,
                lineHeight = titleSize,
                maxLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, end = 28.dp, top = 16.dp)
            )

            AverageText(
                text = stringResource(id = R.string.auth_sub_title_text),
                textAlign = TextAlign.Start,
                maxLines = 20,
                selectable = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, end = 28.dp, top = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f)
                .background(color = Color(0xffff0000)))

            PrimaryButton(
                onClick = { onEventSent(AuthContract.Event.OnSignInClick) },
                text = stringResource(id = R.string.auth_sign_in_button),
                textStyle = MaterialTheme.typography.headlineLarge,
                startImagePainter = painterResource(R.drawable.ic_google),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 42.dp, end = 42.dp, top = 8.dp)
            )

            Text(
                text = stringResource(id = R.string.auth_skip_button),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 42.dp, end = 42.dp, top = 20.dp, bottom = 20.dp)
                    .clickable { onEventSent(AuthContract.Event.OnSkipClick) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenPreview() {
    StupidEnglishTheme {
        ContentScreen(
            state = AuthContract.State(
                isShownGreetings = true
            ),
            onEventSent = {}
        )
    }
}