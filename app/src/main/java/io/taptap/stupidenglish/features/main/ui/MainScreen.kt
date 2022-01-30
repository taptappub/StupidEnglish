package io.taptap.stupidenglish.features.main.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.features.addsentence.navigation.AddSentenceArgumentsMapper
import io.taptap.stupidenglish.features.sentences.ui.SentencesListContract
import io.taptap.stupidenglish.features.sentences.ui.SentencesListScreen
import io.taptap.stupidenglish.features.sentences.ui.SentencesListViewModel
import io.taptap.stupidenglish.features.words.ui.WordListContract
import io.taptap.stupidenglish.features.words.ui.WordListScreen
import io.taptap.stupidenglish.features.words.ui.WordListViewModel
import io.taptap.stupidenglish.ui.BottomSheetScreen
import io.taptap.stupidenglish.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@InternalCoroutinesApi
@ExperimentalPagerApi
@Composable
fun MainScreen(
    state: MainContract.State,
    effectFlow: Flow<MainContract.Effect>?,
    onEventSent: (event: MainContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is MainContract.Effect.CloseMotivation ->
                    modalBottomSheetState.hideSheet(scope)
                is MainContract.Effect.Navigation.ToAddSentence ->
                    onNavigationRequested(effect)
            }
        }?.collect()
    }

    //https://stackoverflow.com/questions/69052660/listen-modalbottomsheetlayout-state-change-in-jetpack-compose
//    LaunchedEffect(modalBottomSheetState.currentValue) {
//        when (modalBottomSheetState.currentValue) {
//            ModalBottomSheetValue.Hidden -> TODO()
//            ModalBottomSheetValue.Expanded -> TODO()
//            ModalBottomSheetValue.HalfExpanded -> TODO()
//        }
//    }
    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                onEventSent(MainContract.Event.OnMotivationCancel)
                //modalBottomSheetState.hideSheet(scope)
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            MotivationBottomSheetScreen(
                onEventSent = onEventSent,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        },
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        if (state.pagerIsVisible) {
            MainScreenWithPager(
                state = state,
                scope = scope,
                modalBottomSheetState = modalBottomSheetState,
                onEventSent = onEventSent,
                navController = navController
            )
        } else {
            WordListDestination(
                navController = navController,
                isShownGreetings = state.isShownGreetings,
                onEventSent = onEventSent
            )
        }
    }
}

@Composable
private fun MotivationBottomSheetScreen(
    modifier: Modifier,
    onEventSent: (event: MainContract.Event) -> Unit
) {
    BottomSheetScreen(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .size(52.dp)
                    .background(
                        color = MaterialTheme.colors.secondary,
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_pen),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Center)
                )
            }

            Text(
                text = stringResource(id = R.string.addw_motivation_title),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(
                    Font(R.font.rubik_regular, FontWeight.Normal),
                    Font(R.font.rubik_medium, FontWeight.Medium),
                    Font(R.font.rubik_bold, FontWeight.Bold)
                ),
                color = getTitleTextColor(),
                style = MaterialTheme.typography.subtitle1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )

            Text(
                text = stringResource(id = R.string.addw_motivation_message),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = getContentTextColor(),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 44.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = getSecondaryButtonBackgroundColor()),
                    onClick = {
                        onEventSent(MainContract.Event.OnMotivationDeclineClick)
                    }) {
                    Text(
                        text = stringResource(id = R.string.addw_motivation_decline),
                        color = Black200,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = getPrimaryButtonBackgroundColor()),
                    onClick = {
                        onEventSent(MainContract.Event.OnMotivationConfirmClick)
                    }) {
                    Text(
                        text = stringResource(id = R.string.addw_motivation_confirm),
                        color = White100
                    )
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@InternalCoroutinesApi
@SuppressLint("CoroutineCreationDuringComposition")
@ExperimentalPagerApi
@Composable
private fun MainScreenWithPager(
    state: MainContract.State,
    navController: NavHostController,
    scope: CoroutineScope,
    onEventSent: (event: MainContract.Event) -> Unit,
    modalBottomSheetState: ModalBottomSheetState
) {
    val pagerState = rememberPagerState()

    scope.launch {
        pagerState.scrollToPage(state.pageId)
    }

    if (state.timeToShowMotivationToSentence) {
        scope.launch {
            if (!modalBottomSheetState.isAnimationRunning) {
                if (!modalBottomSheetState.isVisible) {
                    modalBottomSheetState.show()
                }
            }
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            HorizontalPagerIndicator(
                indicatorWidth = 10.dp,
                indicatorHeight = 10.dp,
                inactiveColor = getIndicatorInactiveColor(),
                activeColor = getIndicatorActiveColor(),
                pagerState = pagerState,
                spacing = 12.dp,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        HorizontalPager(
            count = 2,
            state = pagerState
        ) { page ->
            when (page) {
                0 -> WordListDestination(
                    navController = navController,
                    onEventSent = onEventSent
                )
                1 -> SentenceListDestination(navController = navController)
            }
        }
    }
}

@Composable
private fun WordListDestination(
    isShownGreetings: Boolean = false,
    navController: NavHostController,
    onEventSent: (event: MainContract.Event) -> Unit
) {
    val wordViewModel: WordListViewModel = hiltViewModel()
    val wordState = wordViewModel.viewState.value

    WordListScreen(
        context = LocalContext.current,
        state = wordState,
        effectFlow = wordViewModel.effect,
        onEventSent = { event -> wordViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is WordListContract.Effect.Navigation.ToAddWord -> {
                    navController.navigate(NavigationKeys.Route.SE_ADD_WORD)
                }
                is WordListContract.Effect.Navigation.ToAddSentence -> {
                    val ids = AddSentenceArgumentsMapper.mapTo(navigationEffect.wordIds)
                    navController.navigate("${NavigationKeys.Route.SE_REMEMBER}/${ids}")
                }
            }
        })
    Log.d("StupidEnglish", "isShownGreetings = $isShownGreetings")
    if (isShownGreetings) {
        AlertDialog(
            onDismissRequest = {
                onEventSent(MainContract.Event.OnGreetingsClose)
            },
            text = {
                Text(text = stringResource(id = R.string.main_dialog_message))
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEventSent(MainContract.Event.OnGreetingsClose)
                    }) {
                    Text(text = stringResource(id = R.string.main_dialog_ok))
                }
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun SentenceListDestination(
    navController: NavHostController
) {
    val sentenceViewModel: SentencesListViewModel = hiltViewModel()
    val sentenceState = sentenceViewModel.viewState.value

    SentencesListScreen(
        context = LocalContext.current,
        state = sentenceState,
        effectFlow = sentenceViewModel.effect,
        onEventSent = { event -> sentenceViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is SentencesListContract.Effect.Navigation.ToAddSentence) {
                val ids = AddSentenceArgumentsMapper.mapTo(navigationEffect.wordIds)
                navController.navigate("${NavigationKeys.Route.SE_REMEMBER}/${ids}")
            }
        })
}

@ExperimentalMaterialApi
private fun ModalBottomSheetState.hideSheet(scope: CoroutineScope) {
    scope.launch {
        if (!isAnimationRunning) {
            hide()
        }
    }
}

@ExperimentalMaterialApi
private fun ModalBottomSheetState.showSheet(scope: CoroutineScope) {
    scope.launch {
        if (!isAnimationRunning) {
            show()
        }
    }
}
