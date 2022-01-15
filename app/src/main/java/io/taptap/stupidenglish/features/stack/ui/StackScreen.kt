package io.taptap.stupidenglish.features.stack.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.ui.theme.getTitleTextColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun StackScreen(
    context: Context,
    state: StackContract.State,
    effectFlow: Flow<StackContract.Effect>?,
    onEventSent: (event: StackContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: StackContract.Effect.Navigation) -> Unit
) {
    Box {
        val scaffoldState: ScaffoldState = rememberScaffoldState()

        // Listen for side effects from the VM
        LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
            effectFlow?.onEach { effect ->
                when (effect) {
                    is StackContract.Effect.GetWordsError ->
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = context.getString(effect.errorRes),
                            duration = SnackbarDuration.Short
                        )
                    is StackContract.Effect.Navigation.BackToSentenceList -> onNavigationRequested(
                        effect
                    )
                }
            }?.collect()
        }

        Scaffold(
            scaffoldState = scaffoldState,
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
    state: StackContract.State,
    onEventSent: (event: StackContract.Event) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(state.words) { item ->
            when (item) {
                is StackItemUI -> WordItemRow(item = item)
            }
        }
    }
}

@Composable
private fun WordItemRow(
    item: StackItemUI
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Row {
            WordItem(
                item = item,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    )
                    .fillMaxWidth(0.80f)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun WordItem(
    item: StackItemUI,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = item.word,
            textAlign = TextAlign.Left,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.subtitle1,
            color = getTitleTextColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}
