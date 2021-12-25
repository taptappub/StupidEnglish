package io.taptap.stupidenglish.features.addsentence.ui

import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.ui.BottomSheetScreen
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@Composable
fun AddSentenceScreen(
    context: Context,
    state: AddSentenceContract.State,
    effectFlow: Flow<AddSentenceContract.Effect>?,
    onEventSent: (event: AddSentenceContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: AddSentenceContract.Effect.Navigation) -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        val scaffoldState: ScaffoldState = rememberScaffoldState()

        // Listen for side effects from the VM
        LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
            effectFlow?.onEach { effect ->
                when (effect) {
                    is AddSentenceContract.Effect.Navigation.BackToSentenceList -> onNavigationRequested(
                        effect
                    )
                    is AddSentenceContract.Effect.SaveError ->
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = context.getString(effect.errorRes),
                            duration = SnackbarDuration.Short
                        )
                    is AddSentenceContract.Effect.WaitingForSentenceError ->
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = context.getString(effect.errorRes),
                            duration = SnackbarDuration.Short
                        )
                    is AddSentenceContract.Effect.GetWordsError ->
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = context.getString(effect.errorRes),
                            duration = SnackbarDuration.Short
                        )
                }
            }?.collect()
        }

        Scaffold(
            scaffoldState = scaffoldState,
        ) {
            BottomSheetScreen {
                ContentScreen(
                    state,
                    onEventSent
                )
            }
        }
    }
}

@Composable
private fun ContentScreen(
    state: AddSentenceContract.State,
    onEventSent: (event: AddSentenceContract.Event) -> Unit
) {
    /*ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (content, button) = createRefs()

        AddWordContextBox(
            state = state,
            onEventSent = onEventSent,
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(content) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        NextButton(
            visibility = state.word.isNotEmpty(),
            onClick = {
                when {
                    state.addWordState == AddWordContract.AddWordState.None -> {
                        onEventSent(AddWordContract.Event.OnWord)
                    }
                    state.addWordState == AddWordContract.AddWordState.HasWord
                            && state.description.isNotEmpty()
                            && state.word.isNotEmpty() -> {
                        onEventSent(AddWordContract.Event.OnSaveWord)
                    }
                    else -> {
                        onEventSent(AddWordContract.Event.OnWaitingDescriptionError)
                    }
                }
            },
            modifier = Modifier.constrainAs(button) {
                bottom.linkTo(parent.bottom, 16.dp)
                end.linkTo(parent.end, 16.dp)
            })
    }*/
}


@Composable
fun DefaultPreview() {
    StupidEnglishTheme {
        AddSentenceScreen(
            LocalContext.current,
            AddSentenceContract.State(
                sentence = "",
                words = emptyList()
            ),
            null,
            { },
            { })
    }
}