/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.view.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Theme for the app.
 *
 * @param darkTheme Whether to use the dark variant of this theme.
 * @param fullBlack Whether to use full black colors when [darkTheme] is enabled.
 * @param content Content to display with this theme.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fullBlack: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) {
            if (fullBlack) BlackColors else DarkColors
        } else LightColors,
        content = content
    )
}