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

package com.rohankhayech.choona.wear.view.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.rohankhayech.choona.view.theme.ColorFamily
import com.rohankhayech.choona.view.theme.ExtendedColorScheme
import com.rohankhayech.choona.view.theme.errorContainerDark
import com.rohankhayech.choona.view.theme.errorDark
import com.rohankhayech.choona.view.theme.onErrorContainerDark
import com.rohankhayech.choona.view.theme.onErrorDark
import com.rohankhayech.choona.view.theme.onPrimaryContainerDark
import com.rohankhayech.choona.view.theme.onPrimaryDark
import com.rohankhayech.choona.view.theme.onSecondaryContainerDark
import com.rohankhayech.choona.view.theme.onSecondaryDark
import com.rohankhayech.choona.view.theme.onSurfaceDark
import com.rohankhayech.choona.view.theme.onSurfaceVariantDark
import com.rohankhayech.choona.view.theme.onTertiaryContainerDark
import com.rohankhayech.choona.view.theme.onTertiaryDark
import com.rohankhayech.choona.view.theme.outlineDark
import com.rohankhayech.choona.view.theme.outlineVariantDark
import com.rohankhayech.choona.view.theme.primaryContainerDark
import com.rohankhayech.choona.view.theme.primaryDark
import com.rohankhayech.choona.view.theme.secondaryContainerDark
import com.rohankhayech.choona.view.theme.secondaryDark
import com.rohankhayech.choona.view.theme.surfaceContainerDark
import com.rohankhayech.choona.view.theme.surfaceContainerHighDark
import com.rohankhayech.choona.view.theme.surfaceContainerLowDark
import com.rohankhayech.choona.view.theme.tertiaryContainerDark
import com.rohankhayech.choona.view.theme.tertiaryDark

/** Dark M3 Color Scheme for the application. */
val DarkColorScheme = ColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    onSurface = onSurfaceDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark
)
/** Extra colors for the app. */
val MaterialTheme.extColors: ExtendedColorScheme
    @Composable
    get() = extendedDark

/** Extra dark theme colors. */
private val extendedDark = ExtendedColorScheme(
    green = ColorFamily(
        primaryDark,
        onPrimaryDark,
        primaryContainerDark,
        onPrimaryContainerDark,
    )
)