/*
 * Choona - Guitar Tuner
 * Copyright (C) 2025 Rohan Khayech
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

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.rohankhayech.android.util.ui.preview.m3.ColorSwatch
import com.rohankhayech.android.util.ui.theme.m3.AdaptableMaterialTheme
import com.rohankhayech.android.util.ui.theme.m3.dynamicTrueDarkColorScheme
import com.rohankhayech.android.util.ui.theme.m3.trueDark

/**
 * Theme for the app.
 *
 * @param darkTheme Whether to use the dark variant of this theme.
 * @param fullBlack Whether to use full black colors when [darkTheme] is enabled.
 * @param dynamicColor Whether to use dynamic color.
 * @param content Content to display with this theme.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fullBlack: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AdaptableMaterialTheme(
        lightColorScheme = LightColorScheme,
        darkColorScheme = DarkColorScheme,
        trueDarkColorScheme = TrueDarkColorScheme,
        dynamicTrueDarkColorScheme = { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicTrueDarkColorScheme(LocalContext.current).copy(surface = Color.Black) else DarkColorScheme.trueDark().copy(surface = Color.Black) },
        darkTheme = darkTheme,
        trueDark = fullBlack,
        dynamicColor = dynamicColor,
        content = content
    )
}

@Composable
fun PreviewWrapper(
    fullBlack: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AppTheme(fullBlack = fullBlack, dynamicColor = dynamicColor) {
        Surface(content = content)
    }
}

@PreviewLightDark
@Composable
fun AppThemePreview() {
    PreviewWrapper { ColorSwatch() }
}