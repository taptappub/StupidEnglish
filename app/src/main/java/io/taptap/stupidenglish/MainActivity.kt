package io.taptap.stupidenglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import dagger.hilt.android.AndroidEntryPoint
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceContract
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceScreen
import io.taptap.stupidenglish.features.addsentence.ui.AddSentenceViewModel
import io.taptap.stupidenglish.features.addword.ui.AddWordContract
import io.taptap.stupidenglish.features.addword.ui.AddWordScreen
import io.taptap.stupidenglish.features.addword.ui.AddWordViewModel
import io.taptap.stupidenglish.features.alarm.ui.AlarmScheduler
import io.taptap.stupidenglish.features.auth.ui.AuthContract
import io.taptap.stupidenglish.features.auth.ui.AuthScreen
import io.taptap.stupidenglish.features.auth.ui.AuthViewModel
import io.taptap.stupidenglish.features.groupdetails.ui.GroupDetailsContract
import io.taptap.stupidenglish.features.groupdetails.ui.GroupDetailsScreen
import io.taptap.stupidenglish.features.groupdetails.ui.GroupDetailsViewModel
import io.taptap.stupidenglish.features.groups.ui.GroupListContract
import io.taptap.stupidenglish.features.groups.ui.GroupListScreen
import io.taptap.stupidenglish.features.groups.ui.GroupListViewModel
import io.taptap.stupidenglish.features.importwords.ui.ImportWordsContract
import io.taptap.stupidenglish.features.importwords.ui.ImportWordsScreen
import io.taptap.stupidenglish.features.importwords.ui.ImportWordsViewModel
import io.taptap.stupidenglish.features.importwordstutorial.ui.ImportWordsTutorialContract
import io.taptap.stupidenglish.features.importwordstutorial.ui.ImportWordsTutorialScreen
import io.taptap.stupidenglish.features.importwordstutorial.ui.ImportWordsTutorialViewModel
import io.taptap.stupidenglish.features.main.ui.MainContract
import io.taptap.stupidenglish.features.main.ui.MainViewModel
import io.taptap.stupidenglish.features.profile.ui.ProfileContract
import io.taptap.stupidenglish.features.profile.ui.ProfileScreen
import io.taptap.stupidenglish.features.profile.ui.ProfileViewModel
import io.taptap.stupidenglish.features.splash.ui.SplashContract
import io.taptap.stupidenglish.features.splash.ui.SplashScreen
import io.taptap.stupidenglish.features.splash.ui.SplashViewModel
import io.taptap.stupidenglish.features.stack.ui.StackContract
import io.taptap.stupidenglish.features.stack.ui.StackScreen
import io.taptap.stupidenglish.features.stack.ui.StackViewModel
import io.taptap.stupidenglish.features.termsandconditions.ui.TermsContract
import io.taptap.stupidenglish.features.termsandconditions.ui.TermsScreen
import io.taptap.stupidenglish.features.termsandconditions.ui.TermsViewModel
import io.taptap.stupidenglish.features.words.ui.WordListContract
import io.taptap.stupidenglish.features.words.ui.WordListScreen
import io.taptap.stupidenglish.features.words.ui.WordListViewModel
import io.taptap.stupidenglish.ui.StupidEnglishBottomBar
import io.taptap.uikit.StupidEnglishScaffold
import io.taptap.uikit.theme.StupidEnglishTheme
import java.util.*
import javax.inject.Inject

const val SCHEME = "https"
const val AUTHORITY = "stupidenglish.app"
const val URI = "$SCHEME://$AUTHORITY"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @OptIn(
        ExperimentalMaterialApi::class,
        ExperimentalAnimationApi::class,
        ExperimentalMaterialNavigationApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmScheduler.enableNotifications()
        alarmScheduler.schedulePushNotifications()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            StupidEnglishTheme {
                StupidApp()
            }
        }
    }

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

        val mainViewModel: MainViewModel = hiltViewModel()
        val mainState by mainViewModel.viewState.collectAsState()

        StupidEnglishScaffold(
            scaffoldState = scaffoldState,
//            bottomBar = {
//                if (mainState.shouldShowBottomBar(navController)) {
//                    StupidEnglishBottomBar(
//                        state = mainState,
//                        currentRoute = navController.currentRoute!!,
//                        effectFlow = mainViewModel.effect,
//                        onEventSent = { event -> mainViewModel.setEvent(event) },
//                        onNavigationRequested = { navigationEffect ->
//                            if (navigationEffect is MainContract.Effect.Navigation.OnTabSelected) {
//                                if (navigationEffect.route != navController.currentRoute) {
//                                    navController.navigateToTab(navigationEffect.route)
//                                }
//                            }
//                        }
//                    )
//                }
//            }
        ) { innerPaddingModifier ->
            AnimatedNavHost(
                navController = navController,
                startDestination = NavigationKeys.Route.SE_SPLASH,
                modifier = Modifier.padding(innerPaddingModifier)
            ) {
                composable(
                    route = NavigationKeys.Route.SE_SPLASH
                ) {
                    SplashDestination(navController)
                }
                navigation(
                    startDestination = NavigationKeys.BottomNavigationScreen.SE_WORDS.route,
                    route = NavigationKeys.Route.SE_MAIN
                ) {
                    composable(
                        route = NavigationKeys.BottomNavigationScreen.SE_WORDS.route,
                        enterTransition = null
                    ) {
                        WordListDestination(
                            navController = navController,
                            onEventSent = { event -> mainViewModel.setEvent(event) }
                        )
                    }
                    composable(
                        route = NavigationKeys.BottomNavigationScreen.SE_SETS.route,
                        enterTransition = null
                    ) {
                        SetsListDestination(
                            navController = navController,
                            onEventSent = { event -> mainViewModel.setEvent(event) }
                        )
                    }
                }
                composable(
                    route = NavigationKeys.Route.SE_ADD_WORD,
                    arguments = listOf(navArgument(NavigationKeys.Arg.GROUP_ID) {
                        type = NavType.StringType
                        nullable = true
                    }),
                    enterTransition = {
                        fadeIn()
//                        slideInVertically(initialOffsetY = { 1000 })
                    },
                    exitTransition = {
                        fadeOut()
//                        slideOutVertically(targetOffsetY = { 1000 })
                    }
                ) {
                    AddWordDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_GROUPS
                ) {
                    GroupListDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_IMPORT_WORDS
                ) {
                    ImportWordsDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_TERMS
                ) {
                    TermsDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_IMPORT_WORDS_TUTORIAL
                ) {
                    ImportWordsTutorialDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_PROFILE
                ) {
                    ProfileDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_AUTH
                ) {
                    AuthDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_ADD_SENTENCE,
                    deepLinks = listOf(navDeepLink {
                        uriPattern =
                            "$URI/learn/{${NavigationKeys.Arg.GROUP_ID}}?${NavigationKeys.Arg.WORDS_IDS}={${NavigationKeys.Arg.WORDS_IDS}}"
                    }),
                    arguments = listOf(navArgument(NavigationKeys.Arg.WORDS_IDS) {
                        type = NavType.StringType
                        nullable = true
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
                composable(
                    route = NavigationKeys.Route.SE_ADD_SENTENCE,
                    deepLinks = listOf(navDeepLink {
                        uriPattern = "$URI/learn/{${NavigationKeys.Arg.GROUP_ID}}"
                    }),
                    arguments = listOf(navArgument(NavigationKeys.Arg.GROUP_ID) {
                        type = NavType.StringType
                        nullable = true
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
                composable(
                    route = NavigationKeys.Route.SE_REMEMBER,
                    deepLinks = listOf(navDeepLink {
                        uriPattern = "$URI/flash/{${NavigationKeys.Arg.GROUP_ID}}"
                    }),
                    arguments = listOf(navArgument(NavigationKeys.Arg.GROUP_ID) {
                        type = NavType.StringType
                        nullable = true
                    }),
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
                    }
                ) {
                    StackDestination(navController)
                }
                composable(
                    route = NavigationKeys.Route.SE_GROUP_DETAILS,
                    deepLinks = listOf(navDeepLink {
                        uriPattern = "$URI/details/{${NavigationKeys.Arg.GROUP_ID}}"
                    }),
                    arguments = listOf(navArgument(NavigationKeys.Arg.GROUP_ID) {
                        type = NavType.StringType
                        nullable = true
                    }),
                ) {
                    GroupDetailsDestination(navController)
                }
            }

            if (mainState.shouldShowBottomBar(navController)) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val (bottomBar) = createRefs()
                    StupidEnglishBottomBar(
                        tabs = mainState.bottomBarTabs,
                        //TODO move selected tab into state and rely on bottom bar recomposition
                        selectedTab = mainState.bottomBarTabs.first { it.route == navController.currentRoute!! },
                        effectFlow = mainViewModel.effect,
                        onEventSent = { event -> mainViewModel.setEvent(event) },
                        onNavigationRequested = { navigationEffect ->
                            if (navigationEffect is MainContract.Effect.Navigation.OnTabSelected) {
                                if (navigationEffect.route != navController.currentRoute) {
                                    navController.navigateToTab(navigationEffect.route)
                                }
                            }
                        },
                        modifier = Modifier.constrainAs(bottomBar) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
private fun SplashDestination(
    navController: NavHostController
) {
    val splashViewModel: SplashViewModel = hiltViewModel()
    val splashState by splashViewModel.viewState.collectAsState()

    SplashScreen(
        context = LocalContext.current,
        state = splashState,
        effectFlow = splashViewModel.effect,
        onEventSent = { event -> splashViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is SplashContract.Effect.Navigation.ToAuthScreen -> {
                    navController.navigate(route = NavigationKeys.Route.SE_AUTH) {
                        popUpTo(route = NavigationKeys.Route.SE_SPLASH) {
                            inclusive = true
                        }
                    }
                }
                is SplashContract.Effect.Navigation.ToWordListScreen -> {
                    navController.navigate(route = NavigationKeys.Route.SE_MAIN) {
                        popUpTo(route = NavigationKeys.Route.SE_SPLASH) {
                            inclusive = true
                        }
                    }
                }
            }
        })
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
private fun GroupListDestination(
    navController: NavHostController
) {
    val groupListViewModel: GroupListViewModel = hiltViewModel()
    val groupListState by groupListViewModel.viewState.collectAsState()

    GroupListScreen(
        context = LocalContext.current,
        state = groupListState,
        effectFlow = groupListViewModel.effect,
        onEventSent = { event -> groupListViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is GroupListContract.Effect.Navigation.BackToWordList ->
                    navController.popBackStack()
                is GroupListContract.Effect.Navigation.ToGroupDetails -> {
                    val groupId = navigationEffect.group.id
                    navController.navigate("${NavigationKeys.Route.GROUP_DETAILS}/${groupId}")
                }
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
private fun AddWordDestination(
    navController: NavHostController
) {
    val addWordViewModel: AddWordViewModel = hiltViewModel()
    val addWordState by addWordViewModel.viewState.collectAsState()

    AddWordScreen(
        context = LocalContext.current,
        state = addWordState,
        word = addWordViewModel.word,
        onWordChanged = addWordViewModel::setWord,
        description = addWordViewModel.description,
        onDescriptionChanged = addWordViewModel::setDescription,
        group = addWordViewModel.group,
        onGroupChange = addWordViewModel::setGroup,
        effectFlow = addWordViewModel.effect,
        onEventSent = { event -> addWordViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is AddWordContract.Effect.Navigation.BackToWordList) {
                navController.popBackStack()
            }
        })
}

@ExperimentalMaterialApi
@Composable
private fun ImportWordsDestination(
    navController: NavHostController
) {
    val importWordsViewModel: ImportWordsViewModel = hiltViewModel()
    val importWordsState by importWordsViewModel.viewState.collectAsState()

    ImportWordsScreen(
        context = LocalContext.current,
        state = importWordsState,
        group = importWordsViewModel.group,
        link = importWordsViewModel.link,
        onLinkChange = importWordsViewModel::setLink,
        onGroupChange = importWordsViewModel::setGroup,
        effectFlow = importWordsViewModel.effect,
        onEventSent = { event -> importWordsViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is ImportWordsContract.Effect.Navigation.BackToWordList -> {
                    navController.popBackStack()
                }
                is ImportWordsContract.Effect.Navigation.GoToImportTutorial -> {
                    navController.navigate(NavigationKeys.Route.SE_IMPORT_WORDS_TUTORIAL)
                }
            }
        })
}


@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
private fun GroupDetailsDestination(
    navController: NavHostController
) {
    val groupDetailsViewModel: GroupDetailsViewModel = hiltViewModel()
    val groupDetailsState by groupDetailsViewModel.viewState.collectAsState()

    GroupDetailsScreen(
        context = LocalContext.current,
        state = groupDetailsState,
        effectFlow = groupDetailsViewModel.effect,
        onEventSent = { event -> groupDetailsViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is GroupDetailsContract.Effect.Navigation.ToImportWords -> TODO()
                is GroupDetailsContract.Effect.Navigation.BackTo -> TODO()
                is GroupDetailsContract.Effect.Navigation.ToAddSentence -> TODO()
                is GroupDetailsContract.Effect.Navigation.ToAddWordWithGroup -> TODO()
                is GroupDetailsContract.Effect.Navigation.ToFlashCards -> TODO()
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
private fun TermsDestination(
    navController: NavHostController
) {
    val termsViewModel: TermsViewModel = hiltViewModel()
    val termsState by termsViewModel.viewState.collectAsState()

    TermsScreen(
        context = LocalContext.current,
        state = termsState,
        effectFlow = termsViewModel.effect,
        onEventSent = { event -> termsViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is TermsContract.Effect.Navigation.BackToProfile -> {
                    navController.popBackStack()
                }
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
private fun ImportWordsTutorialDestination(
    navController: NavHostController
) {
    val importWordsTutorialViewModel: ImportWordsTutorialViewModel = hiltViewModel()
    val importWordsTutorialState by importWordsTutorialViewModel.viewState.collectAsState()

    ImportWordsTutorialScreen(
        context = LocalContext.current,
        state = importWordsTutorialState,
        effectFlow = importWordsTutorialViewModel.effect,
        onEventSent = { event -> importWordsTutorialViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is ImportWordsTutorialContract.Effect.Navigation.BackToImportWords) {
                navController.popBackStack()
            }
        })
}

@ExperimentalMaterialApi
@Composable
private fun ProfileDestination(
    navController: NavHostController
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val profileState by profileViewModel.viewState.collectAsState()

    ProfileScreen(
        context = LocalContext.current,
        state = profileState,
        effectFlow = profileViewModel.effect,
        onEventSent = { event -> profileViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is ProfileContract.Effect.Navigation.BackToWordsList -> {
                    navController.popBackStack()
                }
                is ProfileContract.Effect.Navigation.GoToTermsAndConditions -> {
                    navController.navigate(NavigationKeys.Route.SE_TERMS)
                }
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
private fun AuthDestination(
    navController: NavHostController
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.viewState.collectAsState()

    AuthScreen(
        context = LocalContext.current,
        state = authState,
        effectFlow = authViewModel.effect,
        onEventSent = { event -> authViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is AuthContract.Effect.Navigation.ToWordsList) {
                navController.navigate(route = NavigationKeys.Route.SE_MAIN) {
                    popUpTo(route = NavigationKeys.Route.SE_SPLASH)
                }
            }
        }
    )
}

@Composable
private fun StackDestination(navController: NavHostController) {
    val stackViewModel: StackViewModel = hiltViewModel()
    val stackState by stackViewModel.viewState.collectAsState()

    StackScreen(
        context = LocalContext.current,
        state = stackState,
        effectFlow = stackViewModel.effect,
        onEventSent = { event -> stackViewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is StackContract.Effect.Navigation.BackToSentenceList -> {
                    navController.popBackStack()
                }
            }
        })
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
            if (navigationEffect is AddSentenceContract.Effect.Navigation.BackToWordList) {
                navController.popBackStack()
//                navController.backQueue.removeIf {
//                    it.destination.route == NavigationKeys.Route.SE_ADD_SENTENCE
//                    //|| it.destination.route == NavigationKeys.Route.SE_REMEMBER
//                }
//                navController.navigateToTab(route = ArchiveNavigationKeys.Route.SENTENCES) {
//                    popUpTo(route = NavigationKeys.Route.SE_ADD_SENTENCE) {
//                        inclusive = true
//                    }
//                }
            }
        })
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun SetsListDestination(
    navController: NavHostController,
    onEventSent: (event: MainContract.Event) -> Unit
) {

}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun WordListDestination(
    navController: NavHostController,
    onEventSent: (event: MainContract.Event) -> Unit
) {
    val wordViewModel: WordListViewModel = hiltViewModel()
    val wordState by wordViewModel.viewState.collectAsState()

    WordListScreen(
        context = LocalContext.current,
        state = wordState,
        group = wordViewModel.groupName,
        onGroupChange = wordViewModel::setNewGroupName,
        effectFlow = wordViewModel.effect,
        onEventSent = { event -> wordViewModel.setEvent(event) },
        onChangeBottomSheetVisibility = { visibility ->
            onEventSent(MainContract.Event.ChangeBottomSheetVisibility(visibility))
        },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is WordListContract.Effect.Navigation.ToAddWord -> {
                    navController.navigate(NavigationKeys.Route.SE_ADD_WORD)
                }
                is WordListContract.Effect.Navigation.ToAddWordWithGroup -> {
                    val groupId = navigationEffect.group.id
                    navController.navigate("${NavigationKeys.Route.ADD_WORD}?${NavigationKeys.Arg.GROUP_ID}=${groupId}")
                }
                is WordListContract.Effect.Navigation.ToImportWords -> {
                    navController.navigate(NavigationKeys.Route.SE_IMPORT_WORDS)
                }
                is WordListContract.Effect.Navigation.ToProfile -> {
                    navController.navigate(NavigationKeys.Route.SE_PROFILE)
                }
                is WordListContract.Effect.Navigation.ToAddSentence -> {
                    val groupId = navigationEffect.group.id
                    navController.navigate("${NavigationKeys.Route.ADD_SENTENCE}/${groupId}")
//                    navController.navigate("${NavigationKeys.Route.ADD_SENTENCE}/${groupId}?${NavigationKeys.Arg.WORDS_IDS}=${testIds}")
                }
                is WordListContract.Effect.Navigation.ToFlashCards -> {
                    val groupId = navigationEffect.group.id
                    navController.navigate("${NavigationKeys.Route.REMEMBER}/${groupId}")
                }
                is WordListContract.Effect.Navigation.ToGroupList -> {
                    navController.navigate(NavigationKeys.Route.SE_GROUPS)
                }
                is WordListContract.Effect.Navigation.ToGroupDetails -> {
                    val groupId = navigationEffect.group.id
                    navController.navigate("${NavigationKeys.Route.GROUP_DETAILS}/${groupId}")
                }
            }
        })
}

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

/**
 * Copied from similar function in NavigationUI.kt
 *
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt
 */
private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}

private val NavHostController.currentRoute: String?
    get() = currentDestination?.route


@Composable
private fun MainContract.State.shouldShowBottomBar(navController: NavHostController): Boolean {
    val tabs = bottomBarTabs.map { it.route }
    val curRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    return tabs.containsRoute(curRoute) && isBottomBarShown
}

private fun List<String>.containsRoute(curRoute: String?): Boolean {
    forEach {
        if (curRoute?.contains(it) == true) {
            return true
        }
    }
    return false
}

fun NavController.navigateToTab(
    route: String,
    builder: (NavOptionsBuilder.() -> Unit)? = null
) {
    try {
        navigate(route) {
            launchSingleTop = true
            restoreState = true
            // Pop up backstack to the first destination and save state. This makes going back
            // to the start destination when pressing back in any other bottom tab.
            popUpTo(findStartDestination(graph).id) {
                saveState = true
            }
            builder?.let { this.it() }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

//todo
//3) Добавление одинаковых слов
//5) Сохранения слова без подсказки
//6) Добавление картинки, как подсказки
//7) Редактирование
//9) ОНБОРДИНГ (+состояния пустых списков)
//10) A/b тестирование
//11) Перетаскивание в папку слов драг энд дропом. Список групп вылезает сбоку, с анимацией волны, и ты перетягиваешь слово в нужную папку
//12) импорт из Quizlet по ссылке модуля
//13) Можно создавать имя группы при импорте из названия страницы
//4) механизм переключения темы (темная, светлая или системная)
//6) Сделать магазин с карточками, чтобы можно было себе оттуда добавлять уже готовые наборы


//Гугл аналитика без play service'ов
//https://developers.google.com/analytics/devguides/collection/android/v4?hl=ru

//FIXME БАГИ
// - если кучу раз нажать на слово, то блокируется UI
// - если из импорта перейти на боттмошит для добавления группы, то диалог открывается, но фокус на него не ставится и клавиатура не поднимается
// - в gmail не подставляется subject при отправке письма из настроек
// - Если на этом этапе нажать вне попапа, то происходит попытка отправки репорта с ошибкой


//Следующий билд
//2) https://stackoverflow.com/questions/64362801/how-to-handle-visibility-of-a-text-in-jetpack-compose ДЛЯ экрана импорта
//1) Обложить все аналитикой, чтобы смотреть, куда нажимает пользователь (1) Катя не поняла, что внизу табы, 2) нажимала на слово, чтобы сделать предложение, 3) нажимала на слова в ADD_SENTENCE
//6) Статистика по изучению
//7) https://stackoverflow.com/questions/67252538/jetpack-compose-update-composable-when-list-changes


////-----------------------------------------------------------------
//Экран детайлей группы как в Квизлет
//переверстай AddWordScreen, чтобы не прыгало ничего (Не забудь добавить Галочку "Принять" в верхний правый угол)
//поправить диалог добавления группы
//Сейчас достаются ВСЕ слова, а потом сортируются согласно groupId. Это херня. Надо по группе доставать
//придется переделать связь Group и Word, т.к. должно быть многие ко многим
//писать на AddSentenceScreen что это за группа (StackView использовать) - теперь нельзя поделиться группой
//Переделать StackView экран, писать что за группа и сколько слов
//расшаривание групп
//GroupList добавить поиск сверху как в Quizlet