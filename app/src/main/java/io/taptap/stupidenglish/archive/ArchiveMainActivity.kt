package io.taptap.stupidenglish.archive

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.URI
import io.taptap.stupidenglish.archive.ArchiveNavigationKeys.Route.SENTENCES
import io.taptap.stupidenglish.archive.features.sentences.ui.SentencesListContract
import io.taptap.stupidenglish.archive.features.sentences.ui.SentencesListScreen
import io.taptap.stupidenglish.archive.features.sentences.ui.SentencesListViewModel
import io.taptap.stupidenglish.features.addsentence.navigation.AddSentenceArgumentsMapper
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceContract
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceScreen
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceViewModel
import io.taptap.stupidenglish.features.main.ui.MainContract
import io.taptap.stupidenglish.navigateToTab
import io.taptap.uikit.StupidEnglishScaffold

@ExperimentalMaterialApi
@OptIn(
    ExperimentalFoundationApi::class
)
@ExperimentalAnimationApi
@ExperimentalMaterialNavigationApi
@Composable
private fun StupidApp() {
    val navController = rememberAnimatedNavController()
    val scaffoldState = rememberScaffoldState()

    StupidEnglishScaffold(
        scaffoldState = scaffoldState
    ) { innerPaddingModifier ->
        AnimatedNavHost(
            navController = navController,
            startDestination = NavigationKeys.Route.SE_SPLASH,
            modifier = androidx.compose.ui.Modifier.padding(innerPaddingModifier)
        ) {
            composable(
                route = ArchiveNavigationKeys.BottomNavigationScreen.SE_SENTENCES.route,
                arguments = listOf(navArgument(ArchiveNavigationKeys.Arg.WORDS_IDS) {
                    nullable = true
                    defaultValue = null
                }),
                enterTransition = null
            ) {
                SentenceListDestination(
                    navController = navController,
                    onEventSent = { /*event -> mainViewModel.setEvent(event)*/ }
                )
            }
            composable(
                route = ArchiveNavigationKeys.Route.SE_ADD_SENTENCE,
                deepLinks = listOf(navDeepLink {
                    uriPattern =
                        "$URI/${NavigationKeys.Arg.SENTENCE_WORDS_ID}={${NavigationKeys.Arg.SENTENCE_WORDS_ID}}"
                }),
                enterTransition = {
                    fadeIn()
                },
                exitTransition = {
                    fadeOut()
                }
            ) {
                AddSentenceDestination(navController)
            }
        }
    }
}

@Composable
private fun AddSentenceDestination(navController: NavHostController) {
    val addSentenceViewModel: AddSentenceViewModel = hiltViewModel()
    val addSentenceState by addSentenceViewModel.viewState.collectAsState()
    val sentence = addSentenceViewModel.sentence

    AddSentenceScreen(
        context = LocalContext.current,
        state = addSentenceState,
        sentence = sentence,
        onSentenceChanged = addSentenceViewModel::setSentence,
        effectFlow = addSentenceViewModel.effect,
        onEventSent = { event -> addSentenceViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is AddSentenceContract.Effect.Navigation.BackToSentenceList) {
                navController.backQueue.removeIf {
                    it.destination.route == ArchiveNavigationKeys.Route.SE_ADD_SENTENCE
                    //|| it.destination.route == NavigationKeys.Route.SE_REMEMBER
                }
                navController.navigateToTab(route = SENTENCES) {
                    popUpTo(route = ArchiveNavigationKeys.Route.SE_ADD_SENTENCE) {
                        inclusive = true
                    }
                }
            }
        })
}


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun SentenceListDestination(
    navController: NavHostController,
    onEventSent: (event: MainContract.Event) -> Unit
) {
    val sentenceViewModel: SentencesListViewModel = hiltViewModel()
    val sentenceState by sentenceViewModel.viewState.collectAsState()

    SentencesListScreen(
        context = LocalContext.current,
        state = sentenceState,
        effectFlow = sentenceViewModel.effect,
        onEventSent = { event -> sentenceViewModel.setEvent(event) },
        onChangeBottomSheetVisibility = { visibility ->
            onEventSent(MainContract.Event.ChangeBottomSheetVisibility(visibility))
        },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is SentencesListContract.Effect.Navigation.ToAddSentence) {
                val ids = AddSentenceArgumentsMapper.mapTo(navigationEffect.wordIds)
                navController.navigate("${ArchiveNavigationKeys.Route.ADD_SENTENCE}/${ids}")
//                navController.navigate("${NavigationKeys.Route.REMEMBER}/${ids}")
            }
        }
    )
}