package io.taptap.stupidenglish.features.addword.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.base.ui.hideSheet
import io.taptap.stupidenglish.base.ui.showSheet
import io.taptap.stupidenglish.ui.ChooseGroupContent
import io.taptap.uikit.AddTextField
import io.taptap.uikit.ModalBottomSheetLayout
import io.taptap.uikit.PrimaryButton
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.StupidEnglishTopAppBar
import io.taptap.uikit.complex.AddGroupBottomSheetScreen
import io.taptap.uikit.fab.NextButton
import io.taptap.uikit.group.GroupItemHeader
import io.taptap.uikit.group.GroupItemUI
import io.taptap.uikit.group.GroupListItemsModel
import io.taptap.uikit.group.NoGroup
import io.taptap.uikit.theme.StupidEnglishTheme
import io.taptap.uikit.theme.StupidLanguageBackgroundBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalMaterialApi
@Composable
fun AddWordScreen(
    context: Context,
    state: AddWordContract.State,
    word: String,
    onWordChanged: (word: String) -> Unit,
    description: String,
    onDescriptionChanged: (description: String) -> Unit,
    group: String,
    onGroupChange: (newGroup: String) -> Unit,
    effectFlow: Flow<AddWordContract.Effect>?,
    onEventSent: (event: AddWordContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: AddWordContract.Effect.Navigation) -> Unit
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    val scope = rememberCoroutineScope()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is AddWordContract.Effect.HideBottomSheet ->
                    modalBottomSheetState.hideSheet(scope)
                is AddWordContract.Effect.ShowBottomSheet -> {
                    modalBottomSheetState.showSheet(scope)
                }
                is AddWordContract.Effect.Navigation.BackToWordList ->
                    onNavigationRequested(effect)
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
                is AddWordContract.Effect.GetWordsError ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(effect.errorRes),
                        duration = SnackbarDuration.Short
                    )
            }
        }?.collect()
    }

    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        val keyboardController = LocalSoftwareKeyboardController.current
        DisposableEffect(Unit) {
            onDispose {
                onEventSent(AddWordContract.Event.OnGroupAddingCancel)
                keyboardController?.hide()
            }
        }
    }

    BackHandler {
        onEventSent(AddWordContract.Event.OnBackClick)
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
                        onEventSent(AddWordContract.Event.OnApplyGroup)
                    }
                }
            )
        }
    ) {
        StupidEnglishScaffold(
            scaffoldState = scaffoldState
        ) {
            ContentScreen(
                word,
                onWordChanged,
                description,
                onDescriptionChanged,
                state,
                onEventSent
            )
        }
    }
}

@Composable
private fun ContentScreen(
    word: String,
    onWordChanged: (word: String) -> Unit,
    description: String,
    onDescriptionChanged: (description: String) -> Unit,
    state: AddWordContract.State,
    onEventSent: (event: AddWordContract.Event) -> Unit
) {
    StupidLanguageBackgroundBox(
        topbar = {
            StupidEnglishTopAppBar(
                text = stringResource(id = R.string.addw_topbar_title),
                onNavigationClick = { onEventSent(AddWordContract.Event.OnBackClick) },
            )
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding()
                .fillMaxSize()
        ) {
            val (contentRef, buttonRef) = createRefs()

            AddWordContextBox(
                word = word,
                onWordChanged = onWordChanged,
                description = description,
                onDescriptionChanged = onDescriptionChanged,
                state = state,
                onEventSent = onEventSent,
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(contentRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            val focusManager = LocalFocusManager.current

            BottomButton(
                showTextButton = state.addWordState == AddWordContract.AddWordState.HasDescription
                        && description.isNotEmpty()
                        && word.isNotEmpty(),
                visibility = word.isNotEmpty(),
                onClick = {
                    when {
                        state.addWordState == AddWordContract.AddWordState.None -> {
                            onEventSent(AddWordContract.Event.OnWord)
                        }
                        state.addWordState == AddWordContract.AddWordState.HasWord
                                && description.isNotEmpty()
                                && word.isNotEmpty() -> {
                            focusManager.clearFocus()
                            onEventSent(AddWordContract.Event.OnDescription)
                        }
                        state.addWordState == AddWordContract.AddWordState.HasDescription
                                && description.isNotEmpty()
                                && word.isNotEmpty() -> {
                            onEventSent(AddWordContract.Event.OnNewWord)
                        }
                        else -> {
                            onEventSent(AddWordContract.Event.OnWaitingDescriptionError)
                        }
                    }
                },
                modifier = Modifier
                    .constrainAs(buttonRef) {
                        bottom.linkTo(parent.bottom, 16.dp + 52.dp)
                        end.linkTo(parent.end, 16.dp)
                    }
            )
        }
    }
}

@Composable
private fun BottomButton(
    showTextButton: Boolean,
    visibility: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    if (showTextButton) {
        PrimaryButton(
            startImagePainter = painterResource(id = R.drawable.ic_plus),
            text = stringResource(id = R.string.addw_bottom_button_text),
            onClick = onClick,
            modifier = modifier
                .height(52.dp)
        )
    } else {
        NextButton(
            visibility = visibility,
            onClick = onClick,
            modifier = modifier
        )
    }
}

@Composable
private fun AddWordContextBox(
    word: String,
    onWordChanged: (word: String) -> Unit,
    description: String,
    onDescriptionChanged: (description: String) -> Unit,
    state: AddWordContract.State,
    onEventSent: (event: AddWordContract.Event) -> Unit,
    modifier: Modifier
) {
    Crossfade(
        targetState = state.addWordState,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    ) { screen ->
        when (screen) {
            AddWordContract.AddWordState.None -> NoneScreen(
                word,
                onWordChanged,
                onEventSent,
                modifier
            )
            AddWordContract.AddWordState.HasWord -> HasWordScreen(
                word,
                description,
                onDescriptionChanged,
                onEventSent,
                modifier
            )
            else -> HasDescriptionScreen(
                word = word,
                description = description,
                groups = state.groups,
                selectedGroups = state.selectedGroups,
                onEventSent = onEventSent,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun NoneScreen(
    word: String,
    onWordChanged: (word: String) -> Unit,
    onEventSent: (event: AddWordContract.Event) -> Unit,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        val focusRequester by remember {
            mutableStateOf(FocusRequester())
        }
//        val focusRequester = FocusRequester()

        AddTextField(
            value = word,
            onValueChange = onWordChanged,
            placeholder = stringResource(id = R.string.addw_word_placeholder),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (word.isNotEmpty()) {
                        onEventSent(AddWordContract.Event.OnWord)
                    }
                }
            ),
            modifier = Modifier
                .focusRequester(focusRequester)
                .align(Alignment.Center)
        )

        LaunchedEffect("") {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun HasWordScreen(
    word: String,
    description: String,
    onDescriptionChanged: (description: String) -> Unit,
    onEventSent: (event: AddWordContract.Event) -> Unit,
    modifier: Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (wordRef, descriptionRef) = createRefs()
        AddTextField(
            value = word,
            onValueChange = {},
            enabled = false,
            placeholder = stringResource(id = R.string.addw_word_placeholder),
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier
                .clickable {
                    onEventSent(AddWordContract.Event.BackToNoneState)
                }
                .constrainAs(wordRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(descriptionRef.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        val focusRequester by remember {
            mutableStateOf(FocusRequester())
        }
        val focusManager = LocalFocusManager.current
        AddTextField(
            value = description,
            onValueChange = onDescriptionChanged,
            placeholder = stringResource(id = R.string.addw_description_placeholder),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (word.isNotEmpty() && description.isNotEmpty()) {
                        focusManager.clearFocus()
                        onEventSent(AddWordContract.Event.OnDescription)
                    } else {
                        onEventSent(AddWordContract.Event.OnWaitingDescriptionError)
                    }
                }
            ),
            modifier = Modifier
                .focusRequester(focusRequester)
                .constrainAs(descriptionRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        LaunchedEffect("") {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun HasDescriptionScreen(
    word: String,
    description: String,
    groups: List<GroupListItemsModel>,
    selectedGroups: List<GroupListItemsModel>,
    onEventSent: (event: AddWordContract.Event) -> Unit,
    modifier: Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (wordRef, descriptionRef, groupRef) = createRefs()
        AddTextField(
            value = word,
            onValueChange = {},
            enabled = false,
            placeholder = stringResource(id = R.string.addw_word_placeholder),
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier
                .clickable {
                    onEventSent(AddWordContract.Event.BackToNoneState)
                }
                .constrainAs(wordRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(descriptionRef.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        AddTextField(
            value = description,
            onValueChange = {},
            enabled = false,
            placeholder = stringResource(id = R.string.addw_description_placeholder),
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier
                .clickable {
                    onEventSent(AddWordContract.Event.BackToWordState)
                }
                .constrainAs(descriptionRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(groupRef.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Column(
            modifier = Modifier
                .constrainAs(groupRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            GroupItemHeader(
                title = stringResource(id = R.string.impw_choose_groups_label),
                button = stringResource(id = R.string.impw_choose_groups_button),
                onButtonClicked = {
                    onEventSent(AddWordContract.Event.OnAddGroupClick)
                }
            )
            ChooseGroupContent(
                list = groups,
                selectedList = selectedGroups,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                onItemClick = { item ->
                    onEventSent(AddWordContract.Event.OnGroupSelect(item))
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ContentScreenPreview() {
    StupidEnglishTheme {
        ContentScreen(
            word = "Type your word",
            description = "Asss",
            onWordChanged = {},
            onDescriptionChanged = {},
            state = AddWordContract.State(
                isAddGroup = false,
                groups = listOf(
                    GroupItemUI(
                        id = 1,
                        name = "test name"
                    ),
                    GroupItemUI(
                        id = 1,
                        name = "test name"
                    ),
                    GroupItemUI(
                        id = 1,
                        name = "test name"
                    )
                ),
                addWordState = AddWordContract.AddWordState.HasDescription,
                selectedGroups = listOf(NoGroup)
            ),
            onEventSent = {}
        )
    }
}
