/*
 * ChromaticTuner - Arma Rizki
 *
 * Includes modified source code from Choona Guitar Tuner
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

package com.armarizki.chromatic.view.theme

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
val Blue500 = Color(0xFF03A0D3)
val Blue700 = Color(0xFF1976D2)
val Red500 = Color(0xFFF44336)
val Red700 = Color(0xFFD32F2F)
val Yellow500 = Color(0xFFD9BF00)

val BluePrimary = Color(0xFF03A0D3)
val BluePrimaryDark = Color(0xFF0284B3)
val BluePrimaryLight = Color(0xFFB8E9F9)

val BlueSecondary = Color(0xFF1E6F91)
val BlueSecondaryLight = Color(0xFFCFEAF4)

val BlueTertiary = Color(0xFF355F8C)
val BlueTertiaryLight = Color(0xFFD6E4F2)


private val primaryLight = BluePrimary
private val onPrimaryLight = Color.White
private val primaryContainerLight = BluePrimaryLight
private val onPrimaryContainerLight = Color(0xFF003544)

private val secondaryLight = BlueSecondary
private val onSecondaryLight = Color.White
private val secondaryContainerLight = BlueSecondaryLight
private val onSecondaryContainerLight = Color(0xFF002F3A)

private val tertiaryLight = BlueTertiary
private val onTertiaryLight = Color.White
private val tertiaryContainerLight = BlueTertiaryLight
private val onTertiaryContainerLight = Color(0xFF102A44)

private val backgroundLight = Color(0xFFF6FAFC)
private val onBackgroundLight = Color(0xFF0E1D23)

private val surfaceLight = backgroundLight
private val onSurfaceLight = onBackgroundLight

private val surfaceVariantLight = Color(0xFFDDE7EC)
private val onSurfaceVariantLight = Color(0xFF3F4F55)

private val outlineLight = Color(0xFF6E7F86)
private val outlineVariantLight = Color(0xFFC0CCD1)
private val scrimLight = Color(0xFF000000)
private val inverseSurfaceLight = Color(0xFF2D322C)
private val inverseOnSurfaceLight = Color(0xFFEFF2E9)
private val inversePrimaryLight = Color(0xFF8ED0E6)
private val surfaceDimLight = Color(0xFFD8DBD2)
private val surfaceBrightLight = Color(0xFFF7FBF1)
private val surfaceContainerLowestLight = Color(0xFFFFFFFF)
private val surfaceContainerLowLight = Color(0xFFF0F7FA)
private val surfaceContainerLight = Color(0xFFE8F3F8)
private val surfaceContainerHighLight = Color(0xFFDDEEF5)
private val surfaceContainerHighestLight = Color(0xFFD3E9F3)


private val primaryDark = BluePrimary
private val onPrimaryDark = Color(0xFF001E2A)
private val primaryContainerDark = Color(0xFF004E66)
private val onPrimaryContainerDark = Color(0xFFB8E9F9)

private val secondaryDark = Color(0xFF9ED5E6)
private val onSecondaryDark = Color(0xFF003543)
private val secondaryContainerDark = Color(0xFF1E6F91)
private val onSecondaryContainerDark = Color(0xFFCFEAF4)

private val tertiaryDark = Color(0xFFB5CFEA)
private val onTertiaryDark = Color(0xFF1A3048)
private val tertiaryContainerDark = Color(0xFF355F8C)
private val onTertiaryContainerDark = Color(0xFFD6E4F2)

private val backgroundDark = Color(0xFF0B1A20)
private val onBackgroundDark = Color(0xFFDDE7EC)

private val surfaceDark = backgroundDark
private val onSurfaceDark = onBackgroundDark

private val surfaceVariantDark = Color(0xFF3F4F55)
private val onSurfaceVariantDark = Color(0xFFC0CCD1)

private val outlineDark = Color(0xFF8A9BA2)
private val outlineVariantDark = Color(0xFF3F4F55)

private val scrimDark = Color(0xFF000000)
private val inverseSurfaceDark = Color(0xFFE0E4DB)
private val inverseOnSurfaceDark = Color(0xFF2D322C)
private val inversePrimaryDark = Color(0xFF1E6F91)
private val surfaceDimDark = Color(0xFF10140F)
private val surfaceBrightDark = Color(0xFF363A34)
private val surfaceContainerLowestDark = Color(0xFF0B0F0A)
private val surfaceContainerLowDark = Color(0xFF11262E)
private val surfaceContainerDark = Color(0xFF162E37)
private val surfaceContainerHighDark = Color(0xFF1B3640)
private val surfaceContainerHighestDark = Color(0xFF21404B)

// ---- Error colors (Material 3 compliant) ----
private val errorLight = Color(0xFFBA1A1A)
private val onErrorLight = Color(0xFFFFFFFF)
private val errorContainerLight = Color(0xFFFFDAD6)
private val onErrorContainerLight = Color(0xFF410002)

private val errorDark = Color(0xFFFFB4AB)
private val onErrorDark = Color(0xFF690005)
private val errorContainerDark = Color(0xFF93000A)
private val onErrorContainerDark = Color(0xFFFFDAD6)



 
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

 
val TrueDarkColorScheme = DarkColorScheme.trueDark().copy(
    surface = Color.Black,
    background = Color.Black,
    primary = BluePrimary,
    secondary = Color(0xFF8ED0E6),
    tertiary = Color(0xFF9DBBE3)
)


  
val MaterialTheme.extColors: ExtendedColorScheme
    @Composable
    get() = if (isLight) extendedLight else extendedDark

private val extendedLight = ExtendedColorScheme(
    blue = ColorFamily(
        color = primaryLight,
        onColor = onPrimaryLight,
        container = primaryContainerLight,
        onContainer = onPrimaryContainerLight,
    )
)

private val extendedDark = ExtendedColorScheme(
    blue = ColorFamily(
        color = primaryDark,
        onColor = onPrimaryDark,
        container = primaryContainerDark,
        onContainer = onPrimaryContainerDark,
    )
)


@Immutable
data class ExtendedColorScheme(
    val blue: ColorFamily
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val container: Color,
    val onContainer: Color
)

