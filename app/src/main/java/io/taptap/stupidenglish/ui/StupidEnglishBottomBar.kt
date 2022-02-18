package io.taptap.stupidenglish.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.taptap.stupidenglish.R
import io.taptap.stupidenglish.base.LAUNCH_LISTEN_FOR_EFFECTS
import io.taptap.stupidenglish.features.main.ui.MainContract
import io.taptap.stupidenglish.ui.theme.DeepBlue
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

    Column {
        Divider(color = Grey600, thickness = 1.dp, modifier = Modifier.weight(0.0f))

        tabs.forEach { item ->
            val selected = item == currentSection
            val tint by animateColorAsState(
                if (selected) {
                    DeepBlue
                } else {
                    Grey600
                }
            )

            StupidEnglishBottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_head_icon),
                        contentDescription = "words",
                        tint = DeepBlue
                    )
                },
                text = {
                    Text(
                        text = stringResource(item.title),
                        color = tint,
                        maxLines = 1
                    )
                },
                selected = selected,
                onSelected = {
                    onEventSent(MainContract.Event.OnTabSelected(item))
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

    /*NavigationBar {
        tabs.forEach { item ->
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                label = { Text(text = stringResource(id = item.title)) },
                selected = item == currentSection,
                onClick = {
                    onEventSent(MainContract.Event.OnTabSelected(item))
                }
            )
        }
    }*/
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
//        contentAlignment = Alignment.Center
    ) {
        icon()
        text()
    }
}
