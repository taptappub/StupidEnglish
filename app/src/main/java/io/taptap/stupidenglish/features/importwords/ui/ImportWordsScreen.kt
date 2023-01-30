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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.base.ui.hideSheet
import io.taptap.stupidenglish.base.ui.showSheet
import io.taptap.stupidenglish.ui.ChooseGroupContent
import io.taptap.uikit.group.GroupItemHeader
import io.taptap.uikit.LoadingBar
import io.taptap.uikit.ModalBottomSheetLayout
import io.taptap.uikit.ResultNotification
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.StupidEnglishTopAppBar
import io.taptap.uikit.TextField
import io.taptap.uikit.complex.AddGroupBottomSheetScreen
import io.taptap.uikit.fab.NextButton
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ImportWordsScreen(
    context: Context,
    state: ImportWordsContract.State,
    link: String,
    onLinkChange: (newUrl: String) -> Unit,
    group: String,
    onGroupChange: (newGroup: String) -> Unit,
    effectFlow: Flow<ImportWordsContract.Effect>?,
    onEventSent: (event: ImportWordsContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: ImportWordsContract.Effect.Navigation) -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

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
                group = group,
                onGroupNameChange = onGroupChange,
                onAddGroup = {
                    if (group.isNotEmpty()) {
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
                    is ImportWordsContract.Effect.ShowBottomSheet -> {
                        modalBottomSheetState.showSheet(scope)
                    }
                    is ImportWordsContract.Effect.Navigation.BackToWordList ->
                        onNavigationRequested(effect)
                    is ImportWordsContract.Effect.Navigation.GoToImportTutorial -> {
                        focusManager.clearFocus()
                        onNavigationRequested(effect)
                    }
                }
            }?.collect()
        }
        StupidEnglishScaffold(
            scaffoldState = scaffoldState
        ) {
            ContentScreen(
                link,
                onLinkChange,
                state,
                onEventSent
            )
        }
    }
}

@Composable
private fun ContentScreen(
    link: String,
    onLinkChange: (newUrl: String) -> Unit,
    state: ImportWordsContract.State,
    onEventSent: (event: ImportWordsContract.Event) -> Unit
) {
    StupidLanguageBackgroundBox(
        topbar = {
            StupidEnglishTopAppBar(
                text = stringResource(id = R.string.impw_topbar_title),
                onNavigationClick = { onEventSent(ImportWordsContract.Event.OnBackClick) },
            )
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding()
                .fillMaxSize()
        ) {
            val (content, button) = createRefs()

            ImportWordStateScreen(
                state = state,
                link = link,
                onLinkChange = onLinkChange,
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

@Composable
private fun ImportWordStateScreen(
    state: ImportWordsContract.State,
    link: String,
    onLinkChange: (newUrl: String) -> Unit,
    modifier: Modifier,
    onEventSent: (event: ImportWordsContract.Event) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TextField(
            value = link,
            isOnFocus = false,
            labelValue = stringResource(id = R.string.impw_textfield_label),
            hintValue = if (state.importWordState is ImportWordsContract.ImportWordState.Error) {
                stringResource(id = state.importWordState.messageId)
            } else {
                ""
            },
            onValueChange = onLinkChange,
            isError = state.importWordState is ImportWordsContract.ImportWordState.Error,
            modifier = modifier
                .fillMaxWidth()
        )
        if (state.importWordState == ImportWordsContract.ImportWordState.None) {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,

                        )
                    ) {
                        append(stringResource(id = R.string.impw_tutorial_header) + "\n")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        append(stringResource(id = R.string.impw_tutorial_link))
                    }
                },
                maxLines = 5,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .clickable {
                        focusManager.clearFocus()
                        onEventSent(ImportWordsContract.Event.OnTutorialClick)
                    }
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        } else if (state.importWordState == ImportWordsContract.ImportWordState.HasLink) {
            if (!state.isAddGroup) {
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
