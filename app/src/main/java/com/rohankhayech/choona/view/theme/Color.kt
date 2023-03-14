/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.view.theme

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

val Green500 = Color(0xFF4CAF50)
val Green700 = Color(0xFF388E3C)
val Blue500 = Color(0xFF2196F3)
val Blue700 = Color(0xFF1976D2)
val Red500 = Color(0xFFF44336)
val Red700 = Color(0xFFD32F2F)
val Yellow500 = Color(0xFFD9BF00)

val LightColors = lightColors(
    primary = Green500,
    primaryVariant = Green700,
    secondary = Blue500,
    secondaryVariant = Blue700,
    onSecondary = Color.White,
    error = Red700,
)

val DarkColors = darkColors(
    primary = Green500,
    primaryVariant = Green700,
    secondary = Blue500,
    secondaryVariant = Blue700,
    onSecondary = Color.White,
    error = Red500,
)

val BlackColors = DarkColors.copy(
    primary = Green700,
    secondary = Blue700,
    background = Color.Black,
    onBackground = Color.LightGray,
    onSecondary = Color.LightGray,
    error = Red500,
)

// Custom Button Colors

/**
 * Button colors for buttons performing a secondary action.
 * Button background will be the surface color, instead of the primary color.
 */
@Composable
fun secondaryButtonColors() : ButtonColors {
    return ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.onSurface
            .copy(alpha = 0.11f)
            .compositeOver(MaterialTheme.colors.surface)
    )
}

/**
 * Button colors for a secondary-action text button.
 * Content color will be the content color for the current background, instead of the primary color.
 */
@Composable
fun secondaryTextButtonColors() : ButtonColors {
    return ButtonDefaults.textButtonColors(
        contentColor = LocalContentColor.current
    )
}

