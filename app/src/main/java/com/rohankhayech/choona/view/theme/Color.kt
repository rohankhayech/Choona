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

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.rohankhayech.android.util.ui.theme.m3.isLight
import com.rohankhayech.android.util.ui.theme.m3.trueDark

val Green500 = Color(0xFF4CAF50)
val Green700 = Color(0xFF388E3C)
val Blue500 = Color(0xFF2196F3)
val Blue700 = Color(0xFF1976D2)
val Red500 = Color(0xFFF44336)
val Red700 = Color(0xFFD32F2F)
val Yellow500 = Color(0xFFD9BF00)

private val primaryLight = Green500
private val onPrimaryLight = Color(0xFFFFFFFF)
private val primaryContainerLight = Color(0xFFBCF0B4)
private val onPrimaryContainerLight = Color(0xFF235024)
private val secondaryLight = Color(0xFF3B6939)
private val onSecondaryLight = Color(0xFFFFFFFF)
private val secondaryContainerLight = Color(0xFFBCF0B4)
private val onSecondaryContainerLight = Color(0xFF245024)
private val tertiaryLight = Color(0xFF36618E)
private val onTertiaryLight = Color(0xFFFFFFFF)
private val tertiaryContainerLight = Color(0xFFD1E4FF)
private val onTertiaryContainerLight = Color(0xFF194975)
private val errorLight = Color(0xFFBA1A1A)
private val onErrorLight = Color(0xFFFFFFFF)
private val errorContainerLight = Color(0xFFFFDAD6)
private val onErrorContainerLight = Color(0xFF93000A)
private val backgroundLight = Color(0xFFF7FBF1)
private val onBackgroundLight = Color(0xFF191D17)
private val surfaceLight = Color(0xFFF7FBF1)
private val onSurfaceLight = Color(0xFF191D17)
private val surfaceVariantLight = Color(0xFFDEE5D8)
private val onSurfaceVariantLight = Color(0xFF424940)
private val outlineLight = Color(0xFF72796F)
private val outlineVariantLight = Color(0xFFC2C9BD)
private val scrimLight = Color(0xFF000000)
private val inverseSurfaceLight = Color(0xFF2D322C)
private val inverseOnSurfaceLight = Color(0xFFEFF2E9)
private val inversePrimaryLight = Color(0xFFA1D39A)
private val surfaceDimLight = Color(0xFFD8DBD2)
private val surfaceBrightLight = Color(0xFFF7FBF1)
private val surfaceContainerLowestLight = Color(0xFFFFFFFF)
private val surfaceContainerLowLight = Color(0xFFF1F5EC)
private val surfaceContainerLight = Color(0xFFECEFE6)
private val surfaceContainerHighLight = Color(0xFFE6E9E0)
private val surfaceContainerHighestLight = Color(0xFFE0E4DB)

private val primaryDark = Green500
private val onPrimaryDark = Color(0xFF0A390F)
private val primaryContainerDark = Color(0xFF235024)
private val onPrimaryContainerDark = Color(0xFFBCF0B4)
private val secondaryDark = Color(0xFFA1D39A)
private val onSecondaryDark = Color(0xFF0A390F)
private val secondaryContainerDark = Color(0xFF245024)
private val onSecondaryContainerDark = Color(0xFFBCF0B4)
private val tertiaryDark = Blue500
private val onTertiaryDark = Color(0xFF003258)
private val tertiaryContainerDark = Color(0xFF194975)
private val onTertiaryContainerDark = Color(0xFFD1E4FF)
private val errorDark = Color(0xFFFFB4AB)
private val onErrorDark = Color(0xFF690005)
private val errorContainerDark = Color(0xFF93000A)
private val onErrorContainerDark = Color(0xFFFFDAD6)
private val backgroundDark = Color(0xFF10140F)
private val onBackgroundDark = Color(0xFFE0E4DB)
private val surfaceDark = Color(0xFF10140F)
private val onSurfaceDark = Color(0xFFE0E4DB)
private val surfaceVariantDark = Color(0xFF424940)
private val onSurfaceVariantDark = Color(0xFFC2C9BD)
private val outlineDark = Color(0xFF8C9388)
private val outlineVariantDark = Color(0xFF424940)
private val scrimDark = Color(0xFF000000)
private val inverseSurfaceDark = Color(0xFFE0E4DB)
private val inverseOnSurfaceDark = Color(0xFF2D322C)
private val inversePrimaryDark = Color(0xFF3B6939)
private val surfaceDimDark = Color(0xFF10140F)
private val surfaceBrightDark = Color(0xFF363A34)
private val surfaceContainerLowestDark = Color(0xFF0B0F0A)
private val surfaceContainerLowDark = Color(0xFF191D17)
private val surfaceContainerDark = Color(0xFF1D211B)
private val surfaceContainerHighDark = Color(0xFF272B25)
private val surfaceContainerHighestDark = Color(0xFF323630)

/** Light M3 Color Scheme for the application. */
val LightColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

/** Dark M3 Color Scheme for the application. */
val DarkColorScheme = darkColorScheme(
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
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

/** True dark M3 color scheme for the application. */
val TrueDarkColorScheme = DarkColorScheme.trueDark().copy(
    surface = Color.Black,
    primary = Green700,
    tertiary = Blue700
)

/** Extra colors for the app. */
val MaterialTheme.extColors: ExtendedColorScheme
    @Composable
    get() = if (isLight) extendedLight else extendedDark

/** Extra light theme colors. */
private val extendedLight = ExtendedColorScheme(
    green = ColorFamily(
        primaryLight,
        onPrimaryLight,
        primaryContainerLight,
        onPrimaryContainerLight,
    ),
)

/** Extra dark theme colors. */
private val extendedDark = ExtendedColorScheme(
    green = ColorFamily(
        primaryDark,
        onPrimaryDark,
        primaryContainerDark,
        primaryContainerLight,
    )
)

/** Extra colors for the app. */
@Immutable
data class ExtendedColorScheme(
    val green: ColorFamily
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val container: Color,
    val onContainer: Color
)
