package io.taptap.stupidenglish.features.addword.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.ui.theme.StupidEnglishTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import com.google.accompanist.insets.ProvideWindowInsets
import io.taptap.stupidenglish.features.main.ui.*
import kotlinx.coroutines.flow.onEach


@Composable
fun AddWordScreen(
    state: AddWordContract.State,
    effectFlow: Flow<AddWordContract.Effect>?,
    onEventSent: (event: AddWordContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: AddWordContract.Effect.Navigation) -> Unit
) {
    ProvideWindowInsets {
        StupidEnglishTheme {
            val scaffoldState: ScaffoldState = rememberScaffoldState()

            // Listen for side effects from the VM
            LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
                effectFlow?.onEach { effect ->
                    when (effect) {
                        is AddWordContract.Effect.DataWasLoaded ->
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = "Food categories are loaded.",
                                duration = SnackbarDuration.Short
                            )
                        is AddWordContract.Effect.Navigation.ToCategoryDetails -> onNavigationRequested(
                            effect
                        )
                    }
                }?.collect()
            }

            Scaffold(
                scaffoldState = scaffoldState,
                backgroundColor = MaterialTheme.colors.background,
            ) {
//        backgroundColor = MaterialTheme.colors.surface,
                Box {
//            FoodCategoriesList(wordItems = state.categories) { itemId ->
//                onEventSent(MainListContract.Event.CategorySelection(itemId))
//            }
                    if (state.isLoading) {
                        LoadingBar()
                    }
                }
            }
        }
    }

    /*val scaffoldState: ScaffoldState = rememberScaffoldState()

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is MainListContract.Effect.DataWasLoaded ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "Food categories are loaded.",
                        duration = SnackbarDuration.Short
                    )
                is MainListContract.Effect.Navigation.ToCategoryDetails -> onNavigationRequested(
                    effect
                )
            }
        }?.collect()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.surface,
    ) {
//        backgroundColor = MaterialTheme.colors.surface,
        Box {
//            FoodCategoriesList(wordItems = state.categories) { itemId ->
//                onEventSent(MainListContract.Event.CategorySelection(itemId))
//            }
            MainList(wordItems = state.mainList)
            if (state.isLoading) {
                LoadingBar()
            }
        }
    }*/

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StupidEnglishTheme {
        MainListScreen(MainListContract.State(), null, { }, { })
    }
}