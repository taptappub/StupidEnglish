package io.taptap.stupidenglish.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    surface = Grey600,
    primary = White100,
    background = Black200,
    secondary = Blue100,
    error = Red100,

    onBackground = White100,
    onPrimary = Black200,
    onSecondary = White100,
    onError = White100,
    onSurface = White100
)

private val LightColorPalette = lightColors(
    surface = White100,
    primary = Black200,
    background = Grey200,
    secondary = Blue100,
    error = Red100,

    onBackground = Black200,
    onPrimary = White100,
    onSecondary = Black200,
    onError = White100,
    onSurface = Black200
)

@Composable
fun StupidEnglishTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )

        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )

        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
    }

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}

@Composable
fun getTitleTextColor(
    darkTheme: Boolean = isSystemInDarkTheme()
): Color {
    return if (darkTheme) {
        White100
    } else {
        Black200
    }
}

@Composable
fun getContentTextColor(
    darkTheme: Boolean = isSystemInDarkTheme()
): Color {
    return if (darkTheme) {
        White100
    } else {
        Grey600
    }
}

@Composable
fun getFABColor(
    darkTheme: Boolean = isSystemInDarkTheme()
): Color {
    return if (darkTheme) {
        Blue100
    } else {
        Black200
    }
}

@Composable
fun getFABTextColor(
    darkTheme: Boolean = isSystemInDarkTheme()
): Color {
    return if (darkTheme) {
        Black200
    } else {
        White100
    }
}

@Composable
fun getSecondaryButtonBackgroundColor(
    darkTheme: Boolean = isSystemInDarkTheme()
): Color {
    return if (darkTheme) {
        Grey200
    } else {
        Grey200
    }
}

@Composable
fun getPrimaryButtonBackgroundColor(
    darkTheme: Boolean = isSystemInDarkTheme()
): Color {
    return if (darkTheme) {
        Grey600
    } else {
        Black200
    }
}
