package io.taptap.stupidenglish.features.profile.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import io.taptap.authorisation.rememberAuthenticator
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.uikit.AverageText
import io.taptap.uikit.Divider
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.StupidEnglishTopAppBar
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun ProfileScreen(
    context: Context,
    state: ProfileContract.State,
    effectFlow: Flow<ProfileContract.Effect>?,
    onEventSent: (event: ProfileContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: ProfileContract.Effect.Navigation) -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    val authenticator = rememberAuthenticator(context)
    authenticator.login {
        onEventSent(ProfileContract.Event.OnSignIn(it))
    }

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is ProfileContract.Effect.Navigation.BackToWordsList ->
                    onNavigationRequested(effect)
                is ProfileContract.Effect.SighInWithGoogle ->
                    authenticator.launch()
                is ProfileContract.Effect.Logout ->
                    authenticator.logout {
                        onEventSent(ProfileContract.Event.OnLogout)
                    }
                is ProfileContract.Effect.GetUserError ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(effect.errorRes),
                        duration = SnackbarDuration.Short
                    )
            }
        }?.collect()
    }

    StupidEnglishScaffold(
        scaffoldState = scaffoldState
    ) {
        StupidLanguageBackgroundBox(
            topbar = {
                StupidEnglishTopAppBar(
                    text = stringResource(id = R.string.prof_topbar_title),
                    onNavigationClick = { onEventSent(ProfileContract.Event.OnBackClick) },
                )
            }
        ) {
            ContentScreen(
                state = state,
                onEventSent = onEventSent
            )
        }
    }
}

@Composable
private fun ContentScreen(
    state: ProfileContract.State,
    onEventSent: (event: ProfileContract.Event) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isRegistered) {
            RegisteredContentScreen(
                state = state,
                onEventSent = onEventSent
            )
        } else {
            NotRegisteredContentScreen(
                state = state,
                onEventSent = onEventSent
            )
        }
    }
}

@Composable
private fun ColumnScope.RegisteredContentScreen(
    state: ProfileContract.State,
    onEventSent: (event: ProfileContract.Event) -> Unit
) {
    Image(
        painter = rememberAsyncImagePainter(state.avatar),
        contentDescription = null,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .size(100.dp)
    )

    Text(
        text = state.name,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .padding(12.dp)
    )

    MenuScreen(
        state = state,
        onEventSent = onEventSent
    )
}

@Composable
private fun ColumnScope.NotRegisteredContentScreen(
    state: ProfileContract.State,
    onEventSent: (event: ProfileContract.Event) -> Unit
) {
    Image(
        painter = painterResource(R.drawable.ic_profile),
        contentDescription = null,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
        modifier = Modifier
            .size(100.dp)
            .clickable { onEventSent(ProfileContract.Event.OnSignInClick) }
    )

    Text(
        text = stringResource(id = R.string.prof_sign_in),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier
            .padding(12.dp)
            .clickable { onEventSent(ProfileContract.Event.OnSignInClick) }
    )

    MenuScreen(
        state = state,
        onEventSent = onEventSent
    )
}

@Composable
fun MenuScreen(
    state: ProfileContract.State,
    onEventSent: (event: ProfileContract.Event) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp, horizontal = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(shape = RoundedCornerShape(12.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onEventSent(ProfileContract.Event.OnTermAndConditionsClick) }
        ) {
            AverageText(
                text = stringResource(id = R.string.prof_terms),
                textAlign = TextAlign.Start,
                selectable = false,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 14.dp)
                    .weight(1f)
            )
            Image(
                painter = painterResource(R.drawable.ic_arrow),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            )
        }
        Divider(modifier = Modifier.padding(horizontal = 12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AverageText(
                text = stringResource(id = R.string.prof_dark_mode),
                textAlign = TextAlign.Start,
                selectable = false,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 14.dp)
                    .weight(1f)
            )
            Switch(
                checked = state.isDarkMode,
                onCheckedChange = { onEventSent(ProfileContract.Event.OnSwitchModeClick) },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
            )

        }

//        if (isRegistered) {
        if (state.isRegistered) {
            Divider(modifier = Modifier.padding(horizontal = 12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onEventSent(ProfileContract.Event.OnLogoutClick) }
            ) {
                AverageText(
                    text = stringResource(id = R.string.prof_logout),
                    color = MaterialTheme.colorScheme.error,
                    selectable = false,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 14.dp)
                        .weight(1f)
                )
            }
        }
    }
}

/**
 * заменить storage на новую штуку
 * механизм переключения темы (темная, светлая или системная)
 * экран авторизации
 * OnTermAndConditionsClick
 */