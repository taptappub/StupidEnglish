package io.taptap.stupidenglish.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = White100,
    primaryVariant = Blue100,
    secondary = Grey600,
    background = Black200
)

private val LightColorPalette = lightColors(
    surface = Red100,
    primary = White100,
    background = Grey200,
    secondary = Blue100,
//    primaryVariant = Red100,
//    secondaryVariant = Red100,
    error = Red100,

    onBackground = Black200,
    onPrimary = Black200,
    onSecondary = Black200,
    onError = White100,
    onSurface = White100

    /* Other default colors to override
background = Color.White,
surface = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/
)

@Composable
fun StupidEnglishTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Transparent,
        darkIcons = darkTheme
    )

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
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
