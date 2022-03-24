package io.taptap.stupidenglish.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColorScheme(
    surface = Grey600,
    primary = White100,
    background = Esspresso,
    secondary = DeepBlue,
    tertiary = Yellow,
    error = Red
)

private val LightColorPalette = lightColorScheme(
    surface = WarmWhite1,
    primary = Black,
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
        color = Color.Transparent,
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
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable BoxScope.() -> Unit
) {
    val brush = if (darkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Esspresso,
                Esspresso
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
    ProvideWindowInsets {
        Box(
            modifier = modifier
                .background(brush = brush)
        ) {
            Box(modifier = Modifier.statusBarsPadding()) {
                content()
            }
        }
    }
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
