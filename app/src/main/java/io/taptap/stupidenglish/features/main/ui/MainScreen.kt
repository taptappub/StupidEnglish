package io.taptap.stupidenglish.features.main.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
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
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
@Composable
fun MainScreen(
    state: MainContract.State,
    effectFlow: Flow<MainContract.Effect>?,
    onEventSent: (event: MainContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit,
    navController: NavHostController
) {
    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is MainContract.Effect.Navigation.ToAddSentence ->
                    onNavigationRequested(effect)
            }
        }?.collect()
    }

    WordListDestination(
        navController = navController
    )
//    AnimatedNavHost(
//        navController = navController,
//        startDestination = NavigationKeys.BottomNavigationScreen.SE_WORDS.route
//    ) {
//        composable( NavigationKeys.BottomNavigationScreen.SE_WORDS.route) {
//            WordListDestination(
//                navController = navController
//            )
//        }
//        composable(NavigationKeys.BottomNavigationScreen.SE_SENTENCES.route) {
//            SentenceListDestination(navController = navController)
//        }
//    }

    if (state.isShownGreetings) {
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
private fun WordListDestination(
    navController: NavHostController
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
