package io.taptap.stupidenglish.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

@Composable
fun StupidEnglishScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    topBar: @Composable (() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    floatingActionButton: @Composable (() -> Unit) = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    isFloatingActionButtonDocked: Boolean = false,
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = Color.Transparent,
//    drawerBackgroundColor: Color = JetsnackTheme.colors.uiBackground,
//    drawerContentColor: Color = JetsnackTheme.colors.textSecondary,
//    drawerScrimColor: Color = JetsnackTheme.colors.uiBorder,
//    backgroundColor: Color = JetsnackTheme.colors.uiBackground,
//    contentColor: Color = JetsnackTheme.colors.textSecondary,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        isFloatingActionButtonDocked = isFloatingActionButtonDocked,
        drawerContent = drawerContent,
        drawerShape = drawerShape,
        drawerElevation = drawerElevation,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
//        drawerBackgroundColor = drawerBackgroundColor,
//        drawerContentColor = drawerContentColor,
//        drawerScrimColor = drawerScrimColor,
//        backgroundColor = backgroundColor,
//        contentColor = contentColor,
        content = content
    )
}
