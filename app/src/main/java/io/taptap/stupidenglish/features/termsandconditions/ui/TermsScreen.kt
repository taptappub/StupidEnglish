package io.taptap.stupidenglish.features.termsandconditions.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.StupidEnglishTopAppBar
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun TermsScreen(
    state: TermsContract.State,
    effectFlow: Flow<TermsContract.Effect>?,
    onEventSent: (event: TermsContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: TermsContract.Effect.Navigation) -> Unit
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is TermsContract.Effect.Navigation.BackToProfile ->
                    onNavigationRequested(effect)
            }
        }?.collect()
    }

    StupidEnglishScaffold(
        scaffoldState = scaffoldState,
    ) {
        StupidLanguageBackgroundBox(
            topbar = {
                StupidEnglishTopAppBar(
                    text = stringResource(id = R.string.term_topbar_title),
                    onNavigationClick = { onEventSent(TermsContract.Event.OnBackClick) },
                )
            }
        ) {
            ContentScreen(
                state = state
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun ContentScreen(
    state: TermsContract.State
) {
    val webViewState = rememberWebViewState(state.termsUrl)
    WebView(
        state = webViewState,
        onCreated = { it.settings.javaScriptEnabled = true },
        modifier = Modifier.fillMaxSize()
    )
}