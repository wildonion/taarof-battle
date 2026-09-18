package com.wildonion.taarofbattle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Cream = Color(0xFFFFF8ED)
private val DeepTeal = Color(0xFF0F3D3E)
private val WarmOrange = Color(0xFFE76F51)
private val MintGreen = Color(0xFF2A9D8F)
private val SoftYellow = Color(0xFFE9C46A)
private val RoseRed = Color(0xFFE63946)

private val LightColors = lightColorScheme(
    primary = DeepTeal,
    onPrimary = Color.White,
    secondary = WarmOrange,
    onSecondary = Color.White,
    tertiary = MintGreen,
    background = Cream,
    onBackground = DeepTeal,
    surface = Color.White,
    onSurface = DeepTeal,
    error = RoseRed
)

private val DarkColors = darkColorScheme(
    primary = SoftYellow,
    onPrimary = DeepTeal,
    secondary = WarmOrange,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

@Composable
fun TaarofTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
