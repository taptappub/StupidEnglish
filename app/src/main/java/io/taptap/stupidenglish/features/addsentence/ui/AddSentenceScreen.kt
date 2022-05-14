package io.taptap.stupidenglish.features.addsentence.ui

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.flowlayout.FlowRow
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.features.addword.ui.AddWordContract
import io.taptap.uikit.AddTextField
import io.taptap.uikit.AverageText
import io.taptap.uikit.AverageTitle
import io.taptap.uikit.fab.NextButton
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.StupidEnglishTopAppBar
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
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

    StupidEnglishScaffold(
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
    StupidLanguageBackgroundBox(
        topbar = {
            StupidEnglishTopAppBar(
                text = stringResource(id = R.string.adds_topbar_title),
                onNavigationClick = { onEventSent(AddSentenceContract.Event.OnBackClick) },
            )
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding()
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

//            val focusRequester = FocusRequester()
            val focusRequester by remember {
                mutableStateOf(FocusRequester())
            }
            val focusManager = LocalFocusManager.current
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
                            focusManager.clearFocus()
                            onEventSent(AddSentenceContract.Event.OnSaveSentence)
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

            LaunchedEffect("") {
                focusRequester.requestFocus()
            }

            AddSentenceWordList(
                state = state,
                onEventSent = onEventSent,
                modifier = Modifier
                    .padding(bottom = 24.dp, start = 16.dp, end = 88.dp, top = 16.dp)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
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
                val focusManager = LocalFocusManager.current

                NextButton(
                    visibility = state.sentence.isNotEmpty(),
                    onClick = {
                        if (state.sentence.isNotEmpty()) {
                            focusManager.clearFocus()
                            onEventSent(AddSentenceContract.Event.OnSaveSentence)
                        } else {
                            onEventSent(AddSentenceContract.Event.OnWaitingSentenceError)
                        }
                    },
                    modifier = Modifier
                )
            }
        }
    }
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
        backgroundColor = MaterialTheme.colorScheme.surface,
        elevation = 4.dp,
        modifier = Modifier.clickable { onClicked() }
    ) {
        AverageTitle(
            text = item.word,
            maxLines = 1,
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
