package io.taptap.stupidenglish.features.addsentence.ui

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.flowlayout.FlowRow
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.ui.AddTextField
import io.taptap.stupidenglish.ui.AverageText
import io.taptap.stupidenglish.ui.NextButton
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
                is AddSentenceContract.Effect.ShowUnderConstruction ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(R.string.under_construction),
                        duration = SnackbarDuration.Short
                    )
            }
        }?.collect()
    }

    Scaffold(
        scaffoldState = scaffoldState,
    ) {
        ContentScreen(
            state,
            onEventSent
        )
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

        AverageText(
            text = stringResource(id = R.string.adds_main_hint),
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
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
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

        AddSentenceWordList(
            state = state,
            onEventSent = onEventSent,
            modifier = Modifier
                .padding(bottom = 24.dp, start = 16.dp, end = 88.dp, top = 16.dp)
                .fillMaxWidth()
                .constrainAs(words) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Box(
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(button) {
                    bottom.linkTo(parent.bottom, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                }) {
            NextButton(
                visibility = state.sentence.isNotEmpty(),
                onClick = {
                    if (state.sentence.isNotEmpty()) {
                        onEventSent(AddSentenceContract.Event.OnSaveSentence)
                    } else {
                        onEventSent(AddSentenceContract.Event.OnWaitingSentenceError)
                    }
                },
                modifier = Modifier
            )
        }
    }

    if (state.showConfirmSaveDialog) {
        AlertDialog(
            onDismissRequest = {
                onEventSent(AddSentenceContract.Event.OnSaveSentenceDeclined)
            },
            text = {
                Text(text = stringResource(id = R.string.adds_confirm_dialog_text))
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEventSent(AddSentenceContract.Event.OnSaveSentenceConfirmed)
                    }) {
                    Text(text = stringResource(id = R.string.adds_confirm_dialog_ok))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onEventSent(AddSentenceContract.Event.OnSaveSentenceDeclined)
                    }) {
                    Text(text = stringResource(id = R.string.adds_confirm_dialog_cancel))
                }
            }
        )
    }
        //todo
//    надо будет переключить на верный таб при возврате при добавлении предложения

}

@Composable
private fun AddSentenceWordList(
    state: AddSentenceContract.State,
    onEventSent: (event: AddSentenceContract.Event) -> Unit,
    modifier: Modifier
) {
    FlowRow(
        modifier = modifier,
        mainAxisSpacing = 16.dp,
        crossAxisSpacing = 16.dp
    ) {
        state.words.forEach {
            CustomChip(item = it) {
                onEventSent(AddSentenceContract.Event.OnChipClick)
            }
        }
    }
}

@Composable
private fun CustomChip(
    item: Word,
    onClicked: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        backgroundColor = MaterialTheme.colors.secondary,
        elevation = 16.dp,
        modifier = Modifier.clickable { onClicked() }
    ) {
        Text(
            text = item.word,
            textAlign = TextAlign.Left,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.subtitle1,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
        )
    }
}

/*
@Preview(showBackground = true)
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
}*/
