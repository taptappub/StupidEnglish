package io.taptap.stupidenglish.features.addword.ui

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.ui.theme.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@ExperimentalMaterialApi
@Composable
fun AddWordScreen(
    context: Context,
    state: AddWordContract.State,
    effectFlow: Flow<AddWordContract.Effect>?,
    onEventSent: (event: AddWordContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: AddWordContract.Effect.Navigation) -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        val scaffoldState: ScaffoldState = rememberScaffoldState()

        // Listen for side effects from the VM
        LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
            effectFlow?.onEach { effect ->
                when (effect) {
                    is AddWordContract.Effect.Navigation.BackToWordList -> onNavigationRequested(
                        effect
                    )
                    is AddWordContract.Effect.SaveError ->
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = context.getString(effect.errorRes),
                            duration = SnackbarDuration.Short
                        )
                    is AddWordContract.Effect.WaitingForDescriptionError ->
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
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val (stick, content) = createRefs()

                Image(
                    painter = painterResource(id = R.drawable.ic_rectangle),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .constrainAs(stick) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                Box(modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(content) {
                        top.linkTo(stick.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
                    ContentScreen(
                        state,
                        onEventSent
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentScreen(
    state: AddWordContract.State,
    onEventSent: (event: AddWordContract.Event) -> Unit
) {
    ConstraintLayout(
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
    }
}

@Composable
private fun AddWordContextBox(
    state: AddWordContract.State,
    onEventSent: (event: AddWordContract.Event) -> Unit,
    modifier: Modifier
) {
    Crossfade(
        targetState = state.addWordState,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    ) { screen ->
        when (screen) {
            AddWordContract.AddWordState.None -> NoneScreen(state, onEventSent, modifier)
            AddWordContract.AddWordState.HasWord -> HasWordScreen(state, onEventSent, modifier)
        }
    }
}

@Composable
private fun NoneScreen(
    state: AddWordContract.State,
    onEventSent: (event: AddWordContract.Event) -> Unit,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        val focusRequester = FocusRequester()

        AddTextField(
            value = state.word,
            onValueChange = { onEventSent(AddWordContract.Event.OnWordChanging(it)) },
            placeholder = stringResource(id = R.string.addw_word_placeholder),
            modifier = Modifier
                .focusRequester(focusRequester)
                .align(Alignment.Center)
        )

        DisposableEffect(Unit) {
            focusRequester.requestFocus()
            onDispose { }
        }
    }
}

@Composable
private fun HasWordScreen(
    state: AddWordContract.State,
    onEventSent: (event: AddWordContract.Event) -> Unit,
    modifier: Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (word, description) = createRefs()
        AddTextField(
            value = state.word,
            onValueChange = {},
            enabled = false,
            placeholder = stringResource(id = R.string.addw_word_placeholder),
            modifier = Modifier
                .clickable {
                    onEventSent(AddWordContract.Event.BackToNoneState)
                }
                .constrainAs(word) {
                    top.linkTo(parent.top)
                    bottom.linkTo(description.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        val focusRequester = FocusRequester()

        AddTextField(
            value = state.description,
            onValueChange = { onEventSent(AddWordContract.Event.OnDescriptionChanging(it)) },
            placeholder = stringResource(id = R.string.addw_description_placeholder),
            modifier = Modifier
                .focusRequester(focusRequester)
                .constrainAs(description) {
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
    }
}

@Composable
fun AddTextField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    placeholder: String,
    modifier: Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colors.onSurface,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = if (value.isEmpty()) {
                TextAlign.Start
            } else {
                TextAlign.Center
            }
        ),
        enabled = enabled,
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = getTitleTextColor(),
            backgroundColor = Color.Transparent,
            cursorColor = getTitleTextColor(),
            disabledBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            placeholderColor = getContentTextColor()
        ),
        modifier = modifier
            .widthIn(1.dp, Dp.Infinity)
    )
}

@Composable
fun NextButton(
    visibility: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .size(52.dp)
    ) {
        AnimatedVisibility(
            visible = visibility,
            enter = fadeIn(
                initialAlpha = 0.3f
            ),
            exit = fadeOut()
        ) {
            Button(
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = getButtonBackgroundColor()),
                onClick = onClick,
                modifier = Modifier
                    .size(52.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "next",
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
                )
            }
        }
    }
}

@Composable
private fun NoneScreen111(
    state: AddWordContract.State,
    onEventSent: (event: AddWordContract.Event) -> Unit
) {
    Column {
        val focusRequester = FocusRequester()

        OutlinedTextField(
            value = state.word,
            onValueChange = { onEventSent(AddWordContract.Event.OnWordChanging(it)) },
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colors.onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.addw_word_placeholder),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = getTitleTextColor(),
                backgroundColor = Color.Transparent,
                cursorColor = getTitleTextColor(),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                placeholderColor = getContentTextColor()
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .widthIn(1.dp, Dp.Infinity)
                .focusRequester(focusRequester)
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = {
                onEventSent(AddWordContract.Event.OnDescriptionChanging(it))
            },
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colors.onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.addw_word_placeholder),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = getTitleTextColor(),
                backgroundColor = Color.Transparent,
                cursorColor = getTitleTextColor(),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                placeholderColor = getContentTextColor()
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .widthIn(1.dp, Dp.Infinity)
        )

        Button(onClick = { onEventSent(AddWordContract.Event.OnSaveWord) }) {
            Text("Button")
        }

        DisposableEffect(Unit) {
            focusRequester.requestFocus()
            onDispose { }
        }
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StupidEnglishTheme {
        AddWordScreen(
            LocalContext.current,
            AddWordContract.State(
                word = "",
                description = "",
                addWordState = AddWordContract.AddWordState.None
            ),
            null,
            { },
            { })
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun NoneScreenPreview() {
    StupidEnglishTheme {
        ContentScreen(
            AddWordContract.State(
                word = "",
                description = "",
                addWordState = AddWordContract.AddWordState.None
            ),
            { }
        )
    }
}

