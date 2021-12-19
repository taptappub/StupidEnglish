package io.taptap.stupidenglish.features.addword.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.insets.ProvideWindowInsets
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import io.taptap.stupidenglish.ui.theme.getContentTextColor
import io.taptap.stupidenglish.ui.theme.getTitleTextColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@Composable
fun AddWordScreen(
    context: Context,
    state: AddWordContract.State,
    effectFlow: Flow<AddWordContract.Effect>?,
    onEventSent: (event: AddWordContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: AddWordContract.Effect.Navigation) -> Unit
) {
    ProvideWindowInsets {
        StupidEnglishTheme {
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
                    }
                }?.collect()
            }

            Scaffold(
                scaffoldState = scaffoldState,
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
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
                        .constrainAs(content) {
                            top.linkTo(stick.bottom)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }) {
                        when {
                            state.word.isEmpty() && state.description.isEmpty() -> NoneScreen()
                            state.word.isNotEmpty() && state.description.isEmpty() -> HasWordScreen()
                            state.word.isNotEmpty() && state.description.isNotEmpty() -> HasDescriptionScreen()
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun NoneScreen() {
    ConstraintLayout {
        val (word) = createRefs()
        //val focusRequester = FocusRequester()
        val textState = remember { mutableStateOf(TextFieldValue()) }
        OutlinedTextField(
            value = textState.value,
            onValueChange = { textState.value = it },
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
                .constrainAs(word) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
//                .align(Alignment.Center)
                .widthIn(1.dp, Dp.Infinity)
            //.focusRequester(focusRequester)
        )

//        DisposableEffect(Unit) {
//            focusRequester.requestFocus()
//            onDispose { }
//        }
    }
}

@Composable
private fun HasWordScreen() {
    Text("Hello HasWordScreen")
}

@Composable
private fun HasDescriptionScreen() {
    Text("Hello HasDescriptionScreen")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StupidEnglishTheme {
        AddWordScreen(
            LocalContext.current,
            AddWordContract.State(
                word = "",
                description = ""
            ),
            null,
            { },
            { })
    }
}