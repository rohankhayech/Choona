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

package com.rohankhayech.choona.lib.view.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val Green500 = Color(0xFF4CAF50)
val Green700 = Color(0xFF388E3C)
val Blue500 = Color(0xFF2196F3)
val Blue700 = Color(0xFF1976D2)
val Red500 = Color(0xFFF44336)
val Red700 = Color(0xFFD32F2F)
val Yellow500 = Color(0xFFD9BF00)

val primaryDark = Green500
val onPrimaryDark = Color(0xFF0A390F)
val primaryContainerDark = Color(0xFF235024)
val onPrimaryContainerDark = Color(0xFFBCF0B4)
val secondaryDark = Color(0xFFA1D39A)
val onSecondaryDark = Color(0xFF0A390F)
val secondaryContainerDark = Color(0xFF245024)
val onSecondaryContainerDark = Color(0xFFBCF0B4)
val tertiaryDark = Blue500
val onTertiaryDark = Color(0xFF003258)
val tertiaryContainerDark = Color(0xFF194975)
val onTertiaryContainerDark = Color(0xFFD1E4FF)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF10140F)
val onBackgroundDark = Color(0xFFE0E4DB)
val onSurfaceDark = Color(0xFFE0E4DB)
val onSurfaceVariantDark = Color(0xFFC2C9BD)
val outlineDark = Color(0xFF8C9388)
val outlineVariantDark = Color(0xFF424940)
val surfaceContainerLowDark = Color(0xFF191D17)
val surfaceContainerDark = Color(0xFF1D211B)
val surfaceContainerHighDark = Color(0xFF272B25)

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
