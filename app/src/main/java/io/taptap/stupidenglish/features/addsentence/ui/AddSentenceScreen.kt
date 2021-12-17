package io.taptap.stupidenglish.features.addsentence.ui

import android.content.Context
import android.widget.EditText
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import com.google.accompanist.insets.ProvideWindowInsets
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.features.main.ui.*
import io.taptap.stupidenglish.ui.theme.getContentTextColor
import io.taptap.stupidenglish.ui.theme.getTitleTextColor
import kotlinx.coroutines.flow.onEach


@Composable
fun AddSentenceScreen(
    context: Context,
    state: AddSentenceContract.State,
    effectFlow: Flow<AddSentenceContract.Effect>?,
    onEventSent: (event: AddSentenceContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: AddSentenceContract.Effect.Navigation) -> Unit
) {
    ProvideWindowInsets {
        StupidEnglishTheme {
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
                        AddSentenceContent()
                    }

                }
            }
        }
    }
}

@Composable
private fun AddSentenceContent() {

}

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
}