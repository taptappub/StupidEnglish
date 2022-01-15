package io.taptap.stupidenglish.features.stack.ui

import android.content.Context
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackView
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.features.stack.ui.adapter.CardStackAdapter
import io.taptap.stupidenglish.ui.theme.getSuccessTextColor
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
    Column(modifier = Modifier.fillMaxWidth()) {

        AndroidView(
            factory = { context ->
                CardStackView(context).apply {
                    layoutManager = CardStackLayoutManager(context)
                }
            },
            update = { view ->
                view.adapter = CardStackAdapter(state.words)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 36.dp)
                .weight(1.0f, false)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, start = 48.dp, end = 48.dp, top = 24.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 100.dp,
                modifier = Modifier.clickable {

                }
            ) {
                Text(
                    text = stringResource(id = R.string.stck_button_no),
                    fontSize = 24.sp,
                    color = getTitleTextColor(),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 48.dp, end = 48.dp)
                )
            }
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 100.dp,
                modifier = Modifier.clickable {

                }
            ) {
                Text(
                    text = stringResource(id = R.string.stck_button_yes),
                    fontSize = 24.sp,
                    color = getSuccessTextColor(),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 48.dp, end = 48.dp)
                )
            }
        }
    }
}
