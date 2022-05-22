package io.taptap.uikit.theme

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColorScheme(
    surface = Esspresso,
    onSurface = White,
    primary = White,
    onPrimary = Black,
    background = Black,
    secondary = Grey,
    secondaryContainer = WarmWhite1,
    onSecondaryContainer = Black,
    tertiary = Yellow,
    error = Red,
    errorContainer = FaintRed,
    surfaceVariant = HardGrey
)

private val LightColorPalette = lightColorScheme(
    surface = White,
    onSurface = Black,
    primary = Black,
    onPrimary = White,
    background = White,
    secondary = Grey,
    secondaryContainer = WarmWhite1,
    onSecondaryContainer = Black,
    tertiary = Yellow,
    error = Red,
    errorContainer = FaintRed,
    surfaceVariant = SoftGrey
)

@Composable
fun StupidEnglishTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()

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

    Log.d("Theme", "MaterialTheme.colorScheme.background = ${MaterialTheme.colorScheme.background}")

    systemUiController.setNavigationBarColor(
        color = if (darkTheme) { Black } else { White }
            .copy(alpha = 0.77f),
        darkIcons = !darkTheme,
        navigationBarContrastEnforced = false
    )

    systemUiController.setStatusBarColor(
        color = Color.Transparent,
        darkIcons = !darkTheme
    )
}

@Composable
fun StupidLanguageBackgroundBox(
    modifier: Modifier = Modifier.fillMaxSize(),
    topbar: (@Composable (() -> Unit))? = null,
    content: @Composable (BoxScope.() -> Unit)
) {
    Box(
        modifier = modifier
            .background(brush = getStupidLanguageBackgroundBox())
    ) {
        Column(modifier = modifier.systemBarsPadding()) {
            topbar?.invoke()
            Box(modifier = Modifier.fillMaxSize()) {
                content()
            }
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
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp

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
            ),
            startY = screenHeight * 0.4f
        )
    }
}
