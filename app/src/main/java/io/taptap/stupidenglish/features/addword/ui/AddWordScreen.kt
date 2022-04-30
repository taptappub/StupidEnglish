package io.taptap.stupidenglish.features.addword.ui

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.base.logic.sources.groups.GroupItemUI
import io.taptap.stupidenglish.base.logic.sources.groups.GroupListModels
import io.taptap.stupidenglish.base.logic.sources.groups.NoGroup
import io.taptap.stupidenglish.base.logic.sources.groups.getTitle
import io.taptap.stupidenglish.base.ui.hideSheet
import io.taptap.stupidenglish.base.ui.showSheet
import io.taptap.stupidenglish.ui.ChooseGroupBottomSheetScreen
import io.taptap.uikit.AddTextField
import io.taptap.uikit.AverageTitle
import io.taptap.uikit.Divider
import io.taptap.uikit.LetterRoundView
import io.taptap.uikit.ModalBottomSheetLayout
import io.taptap.uikit.NextButton
import io.taptap.uikit.StupidEnglishScaffold
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
                is AddWordContract.Effect.HideChooseGroupBottomSheet ->
                    modalBottomSheetState.hideSheet(scope)
                is AddWordContract.Effect.ShowChooseGroupBottomSheet -> {
                    modalBottomSheetState.showSheet(scope)
                }
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
                onEventSent(AddWordContract.Event.OnChooseGroupBottomSheetCancel)
                keyboardController?.hide()
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            ChooseGroupBottomSheetScreen(
                list = state.groups,
                selectedList = state.dialogSelectedGroups,
                buttonRes = R.string.addw_group_button,
                titleRes = R.string.addw_group_choose_group_title,
                onItemClick = { item ->
                    onEventSent(AddWordContract.Event.OnGroupSelect(item))
                },
                onButtonClick = {
                    onEventSent(AddWordContract.Event.OnGroupsChosenConfirmClick)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .animateContentSize()
            )
        }
    ) {
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
    state: AddWordContract.State,
    onEventSent: (event: AddWordContract.Event) -> Unit
) {
    StupidLanguageBackgroundBox {
        ConstraintLayout(
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding()
                .fillMaxSize()
        ) {
            val (content, button, groups) = createRefs()

            AddWordContextBox(
                state = state,
                onEventSent = onEventSent,
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(content) {
                        top.linkTo(parent.top)
                        bottom.linkTo(groups.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            GroupsStackRow(
                groups = state.selectedGroups,
                onClick = {
                    onEventSent(AddWordContract.Event.OnGroupsClick)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .constrainAs(groups) {
                        top.linkTo(content.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            val focusManager = LocalFocusManager.current

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
                            focusManager.clearFocus()
                            onEventSent(AddWordContract.Event.OnSaveWord)
                        }
                        else -> {
                            onEventSent(AddWordContract.Event.OnWaitingDescriptionError)
                        }
                    }
                },
                modifier = Modifier
                    .constrainAs(button) {
                        bottom.linkTo(parent.bottom, 16.dp + 52.dp)
                        end.linkTo(parent.end, 16.dp)
                    }
            )
        }
    }
}

@Composable
private fun GroupsStackRow(
    groups: List<GroupListModels>,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier
        .height(52.dp)
        .clickable { onClick() }
    ) {
        Divider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            when (groups.size) {
                0 -> throw IllegalStateException("Groups count can't be 0")
                1 -> OneGroup(groups.first())
                in 2..5 -> ManyGroups(
                    groups = groups,
                    text = getQuantityResource(R.plurals.addw_group_count, groups.size, groups.size)
                )
                else -> ManyGroups(
                    groups = groups.take(5),
                    text = getQuantityResource(R.plurals.addw_group_count, groups.size, groups.size)
                )
            }
            Image(
                painter = painterResource(R.drawable.ic_arrow),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
private fun ManyGroups(groups: List<GroupListModels>, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentWidth()
    ) {
        StackRowOfGroups(groups)

        AverageTitle(
            text = text,
            maxLines = 1,
            modifier = Modifier
                .padding(2.dp)
        )
    }
}

@Composable
private fun StackRowOfGroups(groups: List<GroupListModels>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy((-14).dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = 16.dp)
    ) {
        groups.forEach {
            LetterRoundView(
                letter = it.getTitle()[0].uppercaseChar(),
                selected = true,
                border1 = BorderStroke(
                    width = 2.dp,
                    color = Color.White
                ),
                border2 = null,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(28.dp)
            )
        }
    }
}

@Composable
private fun OneGroup(first: GroupListModels) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentWidth()
    ) {
        LetterRoundView(
            letter = first.getTitle()[0].uppercaseChar(),
            selected = true,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 16.dp)
                .size(28.dp)
        )
        AverageTitle(
            text = first.getTitle(),
            maxLines = 1,
            modifier = Modifier
                .padding(2.dp)
        )
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
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (state.word.isNotEmpty()) {
                        onEventSent(AddWordContract.Event.OnWord)
                    }
                }
            ),
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
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            keyboardActions = KeyboardActions.Default,
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
        val focusManager = LocalFocusManager.current
        AddTextField(
            value = state.description,
            onValueChange = { onEventSent(AddWordContract.Event.OnDescriptionChanging(it)) },
            placeholder = stringResource(id = R.string.addw_description_placeholder),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (state.word.isNotEmpty() && state.description.isNotEmpty()) {
                        focusManager.clearFocus()
                        onEventSent(AddWordContract.Event.OnSaveWord)
                    } else {
                        onEventSent(AddWordContract.Event.OnWaitingDescriptionError)
                    }
                }
            ),
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
private fun getQuantityResource(
    @PluralsRes id: Int,
    quantity: Int,
    vararg formatArgs: Any
): String {
    val resources = LocalContext.current.resources
    return resources.getQuantityString(id, quantity, *formatArgs)
}

@Preview(showBackground = true)
@Composable
fun ManyGroupsPreview() {
    StupidEnglishTheme {
        val groups = listOf(
            GroupItemUI(
                id = 1,
                name = "test name"
            ),
            GroupItemUI(
                id = 1,
                name = "dtest name"
            ),
            GroupItemUI(
                id = 1,
                name = "dtest name"
            )
        )
        ManyGroups(
            groups = groups,
            text = "text"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StackRowOfGroupsPreview() {
    StupidEnglishTheme {
        StackRowOfGroups(
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
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupsStackRowPreview() {
    StupidEnglishTheme {
        GroupsStackRow(
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
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OneGroupPreview() {
    StupidEnglishTheme {
        OneGroup(
            GroupItemUI(
                id = 1,
                name = "test name"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContentScreenPreview() {
    StupidEnglishTheme {
        ContentScreen(
            state = AddWordContract.State(
                word = "word",
                description = "",
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
                addWordState = AddWordContract.AddWordState.None,
                selectedGroups = listOf(NoGroup),
                dialogSelectedGroups = listOf(NoGroup)
            ),
            onEventSent = {}
        )
    }
}
