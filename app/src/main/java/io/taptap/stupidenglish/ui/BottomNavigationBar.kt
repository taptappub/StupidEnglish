package io.taptap.stupidenglish.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.BottomNavigation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.taptap.stupidenglish.NavigationKeys

@Composable
fun BottomBar(navController: NavController) {
    val items = remember {
        listOf(
            NavigationKeys.BottomNavigationScreen.SE_WORDS,
            NavigationKeys.BottomNavigationScreen.SE_SENTENCES
        )
    }

    BottomNavigation(
        elevation = 5.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.map {
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = it.icon),
                        contentDescription = it.title
                    )
                },
                label = {
                    Text(
                        text = it.title
                    )
                },
                selected = currentRoute == it.route,
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(alpha = 0.4f),
                onClick = {
                    navController.navigate(it.route)
                }
            )
        }
    }
}
