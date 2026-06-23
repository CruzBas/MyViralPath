package com.example.myviralpath.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NaranjaPrimario,
    secondary = BordeTxt,
    tertiary = TextoSecundario,
    background = BackgroundOscuro,
    surface = BackgroundOscuro,
    onPrimary = TextoPrimario,
    onBackground = TextoPrimario,
    onSurface = TextoPrimario
)

private val LightColorScheme = lightColorScheme(
    primary = NaranjaPrimario,
    secondary = BordeTxt,
    tertiary = TextoSecundario,
    background = BackgroundOscuro,
    surface = BackgroundOscuro,
    onPrimary = TextoPrimario,
    onBackground = TextoPrimario,
    onSurface = TextoPrimario
)

@Composable
fun MyViralPathTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+

    //Se cambio a falsepar que Android no intente pintar la app con los colores de fondo del celular del usuario.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}