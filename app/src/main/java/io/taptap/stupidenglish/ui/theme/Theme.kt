package io.taptap.stupidenglish.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColorScheme(
    surface = Esspresso,
    onSurface = White,
    primary = White,
    onPrimary = Black,
    background = Black,
    secondary = Grey,
    tertiary = Yellow,
    error = Red
)

private val LightColorPalette = lightColorScheme(
    surface = White,
    onSurface = Black,
    primary = Black,
    onPrimary = White,
    background = White,
    secondary = Grey,
    tertiary = Yellow,
    error = Red
)

@Composable
fun StupidEnglishTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()

//    systemUiController.setSystemBarsColor(
//        color = Color.Transparent,
//        darkIcons = !darkTheme
//    )

    systemUiController.setNavigationBarColor(
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.77f),
        darkIcons = !darkTheme,
        navigationBarContrastEnforced = false
    )

    systemUiController.setStatusBarColor(
        color = Color.Transparent,
        darkIcons = !darkTheme
    )

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = StupidEnglishTypography,
        content = content
    )
}

@Composable
fun StupidLanguageBackgroundBox(
    modifier: Modifier = Modifier.fillMaxSize(),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(brush = getStupidLanguageBackgroundBox())
    ) {
        Box(modifier = modifier.systemBarsPadding()) {
            content()
        }
    }
}

@Composable
fun getStupidLanguageBackgroundRow(
    darkTheme: Boolean = isSystemInDarkTheme()
): Brush {
    return if (darkTheme) {
        Brush.horizontalGradient(
            colors = listOf(
                Esspresso,
                Esspresso
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                WarmWhite2,
                WarmWhite1
            )
        )
    }
}

@Composable
fun getStupidLanguageBackgroundBox(
    darkTheme: Boolean = isSystemInDarkTheme()
): Brush {
    return if (darkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Black,
                Black
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                WarmWhite2,
                White
            )
        )
    }
}
