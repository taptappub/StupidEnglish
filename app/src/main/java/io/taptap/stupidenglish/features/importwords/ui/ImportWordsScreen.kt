package io.taptap.stupidenglish.features.importwords.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.ui.ChooseGroupContent
import io.taptap.stupidenglish.ui.GroupItemHeader
import io.taptap.uikit.LoadingBar
import io.taptap.uikit.NextButton
import io.taptap.uikit.ResultNotification
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
        ConstraintLayout(
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding()
                .fillMaxSize()
        ) {
            val (content, button) = createRefs()

            ImportWordStateScreen(
                state = state,
                onEventSent = onEventSent,
                modifier = Modifier
                    .constrainAs(content) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            NextButton(
                visibility = true,
                onClick = {
                    onEventSent(ImportWordsContract.Event.OnImportClick)
                },
                modifier = Modifier
                    .constrainAs(button) {
                        bottom.linkTo(parent.bottom, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                    }
            )
        }

        ResultNotification(
            resultNotificationState = state.parsingState.toResultNotificationState()
        )
    }
}

@Composable
private fun ImportWordStateScreen(
    state: ImportWordsContract.State,
    modifier: Modifier,
    onEventSent: (event: ImportWordsContract.Event) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(
                state = scrollState
            )
    ) {
        TextField(
            value = state.link,
            label = { Text("Label") },
            onValueChange = { text ->
                onEventSent(ImportWordsContract.Event.OnLinkChanging(text))
            },
            isError = state.isWrongLink,
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surface,
                cursorColor = MaterialTheme.colorScheme.onSurface,
                placeholderColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        if (!state.isWrongLink && state.importWordState == ImportWordsContract.ImportWordState.HasLink) {
            GroupItemHeader(
                title = "Title",
                button = "button",
                onButtonClicked = {}
            )
            GroupsChooserScreen(
                state = state,
                onEventSent = onEventSent
            )
        }
    }

    if (state.importWordState == ImportWordsContract.ImportWordState.InProgress) {
        LoadingBar()
    }
}

@Composable
fun GroupsChooserScreen(
    state: ImportWordsContract.State,
    onEventSent: (event: ImportWordsContract.Event) -> Unit
) {
    ChooseGroupContent(
        list = state.groups,
        selectedList = state.selectedGroups,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        onItemClick = { item ->
            onEventSent(ImportWordsContract.Event.OnGroupSelect(item))
        }
    )
}

private fun ImportWordsContract.ParsingState.toResultNotificationState(): ResultNotification.State {
    return when (this) {
        ImportWordsContract.ParsingState.Success -> ResultNotification.State.SUCCESS
        ImportWordsContract.ParsingState.Failed -> ResultNotification.State.FAILED
        ImportWordsContract.ParsingState.None -> ResultNotification.State.IDLE
    }
}

//1) Сделать multifab
/**
 * Поле ввода
 * Если верное, то показываю список групп, где сверху есть кнопка добавить как на главном экране, а снизу группы как в AddWord
 * Вместе с этим появляется кнопка с надписью Import
 */