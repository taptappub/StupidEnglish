package io.taptap.stupidenglish.features.addsentence.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.ui.AddTextField
import io.taptap.stupidenglish.ui.BottomSheetScreen
import io.taptap.stupidenglish.ui.NextButton
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import kotlinx.coroutines.flow.Flow
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.features.addword.ui.AddWordContract
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
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (sentence, words, button, hint) = createRefs()

        Text(
            text = stringResource(id = R.string.adds_main_hint),
            fontSize = 12.sp,
            fontFamily = FontFamily(
                Font(R.font.rubik_regular, FontWeight.Normal),
                Font(R.font.rubik_medium, FontWeight.Medium),
                Font(R.font.rubik_bold, FontWeight.Bold)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 38.dp)
                .constrainAs(hint) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        val focusRequester = FocusRequester()

        AddTextField(
            value = state.sentence,
            onValueChange = { text ->
                onEventSent(AddSentenceContract.Event.OnSentenceChanging(text))
            },
            placeholder = stringResource(id = R.string.adds_sentence_placeholder),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (state.sentence.isNotEmpty()) {
                        onEventSent(AddSentenceContract.Event.OnSaveSentence) //TODO надо показывать диалог с подтверждением
                    } else {
                        onEventSent(AddSentenceContract.Event.OnWaitingSentenceError)
                    }
                }
            ),
            modifier = Modifier
                .focusRequester(focusRequester)
                .constrainAs(sentence) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        DisposableEffect(Unit) {
            focusRequester.requestFocus()
            onDispose { }
        }

        /*AddSentenceWordList(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(words) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )*/

        NextButton(
            visibility = state.sentence.isNotEmpty(),
            onClick = {
                if (state.sentence.isNotEmpty()) {
                    onEventSent(AddSentenceContract.Event.OnSaveSentence) //TODO надо показывать диалог с подтверждением
                } else {
                    onEventSent(AddSentenceContract.Event.OnWaitingSentenceError)
                }
            },
            modifier = Modifier.constrainAs(button) {
                bottom.linkTo(parent.bottom, 16.dp)
                end.linkTo(parent.end, 16.dp)
            })
    }
}

@Composable
private fun AddSentenceContextBox(
    state: AddSentenceContract.State,
    onEventSent: (event: AddSentenceContract.Event) -> Unit,
    modifier: Modifier
) {

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