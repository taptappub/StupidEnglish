package io.taptap.stupidenglish.features.importwords.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun ImportWordsScreen(
    context: Context,
    state: ImportWordsContract.State,
    effectFlow: Flow<ImportWordsContract.Effect>?,
    onEventSent: (event: ImportWordsContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: ImportWordsContract.Effect.Navigation) -> Unit
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    val scope = rememberCoroutineScope()

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is ImportWordsContract.Effect.GetGroupsError ->
                    scaffoldState.snackbarHostState.showSnackbar(
                    message = context.getString(effect.errorRes),
                    duration = SnackbarDuration.Short
                )
                is ImportWordsContract.Effect.Navigation.BackToWordList -> onNavigationRequested(
                    effect
                )
            }
        }?.collect()
    }
    StupidEnglishScaffold(
        scaffoldState = scaffoldState
    ) {
        ContentScreen(
            state,
            onEventSent
        )
    }
}

@Composable
private fun ContentScreen(
    state: ImportWordsContract.State,
    onEventSent: (event: ImportWordsContract.Event) -> Unit
) {
    StupidLanguageBackgroundBox {
        TextField(
            value = state.link,
            label = { Text("Label") },
            onValueChange = { text ->
                onEventSent(ImportWordsContract.Event.OnLinkChanging(text))
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colorScheme.onSurface,
                backgroundColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                placeholderColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

//1) Сделать multifab