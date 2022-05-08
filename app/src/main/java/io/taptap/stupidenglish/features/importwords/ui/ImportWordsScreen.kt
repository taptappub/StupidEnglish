package io.taptap.stupidenglish.features.importwords.ui

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.base.ui.hideSheet
import io.taptap.stupidenglish.base.ui.showSheet
import io.taptap.stupidenglish.ui.ChooseGroupContent
import io.taptap.stupidenglish.ui.GroupItemHeader
import io.taptap.uikit.LoadingBar
import io.taptap.uikit.ModalBottomSheetLayout
import io.taptap.uikit.NextButton
import io.taptap.uikit.ResultNotification
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.TextField
import io.taptap.uikit.complex.AddGroupBottomSheetScreen
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ImportWordsScreen(
    context: Context,
    state: ImportWordsContract.State,
    effectFlow: Flow<ImportWordsContract.Effect>?,
    onEventSent: (event: ImportWordsContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: ImportWordsContract.Effect.Navigation) -> Unit
) {
    val scope = rememberCoroutineScope()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        val keyboardController = LocalSoftwareKeyboardController.current
        DisposableEffect(Unit) {
            onDispose {
                onEventSent(ImportWordsContract.Event.OnGroupAddingCancel)
                keyboardController?.hide()
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            AddGroupBottomSheetScreen(
                sheetTitle = stringResource(id = R.string.impw_group_add_group_title),
                group = { state.group },
                onGroupNameChange = { onEventSent(ImportWordsContract.Event.OnGroupChanging(it)) },
                onAddGroup = {
                    if (state.group.isNotEmpty()) {
                        onEventSent(ImportWordsContract.Event.OnApplyGroup)
                    }
                }
            )
        }
    ) {
        val scaffoldState: ScaffoldState = rememberScaffoldState()

        // Listen for side effects from the VM
        LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
            effectFlow?.onEach { effect ->
                when (effect) {
                    is ImportWordsContract.Effect.GetGroupsError ->
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = context.getString(effect.errorRes),
                            duration = SnackbarDuration.Short
                        )
                    is ImportWordsContract.Effect.HideBottomSheet ->
                        modalBottomSheetState.hideSheet(scope)
                    is ImportWordsContract.Effect.ShowBottomSheet ->
                        modalBottomSheetState.showSheet(scope)
                    is ImportWordsContract.Effect.Navigation.BackToWordList -> onNavigationRequested(
                        effect
                    )
                    is ImportWordsContract.Effect.Navigation.GoToImportTutorial -> onNavigationRequested(
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

            if (state.importWordState == ImportWordsContract.ImportWordState.HasLink) {
                val focusManager = LocalFocusManager.current

                NextButton(
                    visibility = true,
                    onClick = {
                        focusManager.clearFocus()
                        onEventSent(ImportWordsContract.Event.OnImportClick)
                    },
                    modifier = Modifier
                        .constrainAs(button) {
                            bottom.linkTo(parent.bottom, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                        }
                )
            }
        }

        ResultNotification(
            resultNotificationState = state.parsingState.toResultNotificationState()
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ImportWordStateScreen(
    state: ImportWordsContract.State,
    modifier: Modifier,
    onEventSent: (event: ImportWordsContract.Event) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TextField(
            value = state.link,
            isOnFocus = false,
            labelValue = stringResource(id = R.string.impw_textfield_label),
            hintValue = if (state.importWordState is ImportWordsContract.ImportWordState.Error) {
                stringResource(id = state.importWordState.messageId)
            } else {
                ""
            },
            onValueChange = { text ->
                onEventSent(ImportWordsContract.Event.OnLinkChanging(text))
            },
            isError = state.importWordState is ImportWordsContract.ImportWordState.Error,
            modifier = modifier
                .fillMaxWidth()
        )
        if (state.importWordState == ImportWordsContract.ImportWordState.None) {
            Text(
                text = stringResource(id = R.string.impw_tutorial_header),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Text(
                text = stringResource(id = R.string.impw_tutorial_link),
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .clickable { onEventSent(ImportWordsContract.Event.OnTutorialClick) }
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        } else if (state.importWordState == ImportWordsContract.ImportWordState.HasLink) {
            if (!state.isAddGroup) {
                val keyboardController = LocalSoftwareKeyboardController.current
                keyboardController?.hide()
                val focusManager = LocalFocusManager.current
                focusManager.clearFocus()
            }

            GroupItemHeader(
                title = stringResource(id = R.string.impw_choose_groups_label),
                button = stringResource(id = R.string.impw_choose_groups_button),
                onButtonClicked = {
                    onEventSent(ImportWordsContract.Event.OnAddGroupClick)
                }
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
        ImportWordsContract.ParsingState.InProgress -> ResultNotification.State.IN_PROGRESS
        ImportWordsContract.ParsingState.Failed -> ResultNotification.State.FAILED
        ImportWordsContract.ParsingState.None -> ResultNotification.State.IDLE
    }
}

//Сделать multifab
//3) import https://rmmbr.io/import/
//Лонгрид для квизлета, google sheet, google translater в виде табов https://johncodeos.com/how-to-create-tabs-with-jetpack-compose/
//фокус не ставится на поле при добавлении группы, а на WLS ставится
//запрос не отменяется при debounce
//анимация появлкния элементов на экране