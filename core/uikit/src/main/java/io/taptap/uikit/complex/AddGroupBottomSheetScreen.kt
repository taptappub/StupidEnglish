package io.taptap.uikit.complex

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.uikit.AddTextField
import io.taptap.uikit.AverageTitle
import io.taptap.uikit.BottomSheetScreen
import io.taptap.uikit.NextButton

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddGroupBottomSheetScreen(
    sheetTitle: String,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .imePadding()
        .focusTarget()
        .height(300.dp),
    onGroupNameChange: (String) -> Unit,
    onAddGroup: () -> Unit,
    group: () -> String
) {
    BottomSheetScreen(
        modifier = modifier
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val (title, content, button) = createRefs()
            val keyboardController = LocalSoftwareKeyboardController.current

            Box(
                modifier = modifier.constrainAs(content) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            ) {
                val focusRequester by remember {
                    mutableStateOf(FocusRequester())
                }
//                val focusRequester = FocusRequester()

                AddTextField(
                    value = group(),
                    onValueChange = onGroupNameChange,
                    placeholder = "",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            onAddGroup()
                        }
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .align(Alignment.Center)
                )

                LaunchedEffect("123") {
                    focusRequester.requestFocus()
                }
            }
            AverageTitle(
                text = sheetTitle,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
            )

            NextButton(
                visibility = group().isNotEmpty(),
                onClick = {
                    keyboardController?.hide()
                    onAddGroup()
                },
                modifier = Modifier.constrainAs(button) {
                    bottom.linkTo(parent.bottom, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                }
            )
        }
    }
}