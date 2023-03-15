/*
 * Choona - Guitar Tuner
 * Copyright (C) 2023 Rohan Khayech
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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