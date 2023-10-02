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

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import com.rohankhayech.android.util.ui.theme.trueDarkColors

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

val BlackColors = trueDarkColors(
    primary = Green700,
    primaryVariant = Green700,
    secondary = Blue700,
    secondaryVariant = Blue700,
    onSecondary = Color.White,
    error = Red500,
)

