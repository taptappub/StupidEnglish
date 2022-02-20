package io.taptap.stupidenglish.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import io.taptap.stupidenglish.NavigationKeys
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.features.main.ui.MainContract
import io.taptap.stupidenglish.ui.theme.Blue100
import io.taptap.stupidenglish.ui.theme.DeepBlue
import io.taptap.stupidenglish.ui.theme.Grey200
import io.taptap.stupidenglish.ui.theme.Grey600
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun StupidEnglishBottomBar(
    currentRoute: String,
    state: MainContract.State,
    effectFlow: Flow<MainContract.Effect>?,
    onEventSent: (event: MainContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit
) {
    val tabs = state.bottomBarTabs
    val currentSection = tabs.first { it.route == currentRoute }

    // Listen for side effects from the VM
    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is MainContract.Effect.Navigation.OnTabSelected -> {
                    onNavigationRequested(effect)
                }
            }
        }?.collect()
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .height(56.dp)
            .navigationBarsPadding(start = false, end = false)
    ) {
        Divider(color = Grey200, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {

            tabs.forEach { item ->
                val selected = item == currentSection
                val tint by animateColorAsState(
                    if (selected) {
                        Color(0xFF587EDE)
                    } else {
                        Color(0xFF6c6c6c)
                    }
                )
                StupidEnglishBottomNavigationItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = stringResource(id = item.title),
                            tint = tint,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(6.dp)
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(id = item.title),
                            color = tint,
                            maxLines = 1,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(6.dp)
                        )
                    },
                    selected = selected,
                    onSelected = {
                        onEventSent(MainContract.Event.OnTabSelected(item))
                    },
                    modifier = Modifier
                        .height(56.dp)
                        .weight(1.0f)
                )
            }
        }
    }
}

@Composable
fun StupidEnglishBottomNavigationItem(
    icon: @Composable RowScope.() -> Unit,
    text: @Composable RowScope.() -> Unit,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier.selectable(selected = selected, onClick = onSelected),
        horizontalArrangement = Arrangement.Center
    ) {
        icon()
        text()
    }
}
