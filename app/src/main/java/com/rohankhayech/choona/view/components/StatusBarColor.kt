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

package com.rohankhayech.choona.view.components

import android.os.Build
import android.view.WindowInsetsController
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import com.rohankhayech.choona.view.components.StatusBarIconColor.DARK
import com.rohankhayech.choona.view.components.StatusBarIconColor.LIGHT

/**
 * UI component used to control the color of the status bar icons.
 * @param iconColor: Whether to use light or dark status bar icons.
 * @author Rohan Khayech
 */
@Composable
fun StatusBarColor(
    iconColor: StatusBarIconColor
) {
    val activity = LocalActivity.current
    LaunchedEffect(activity, iconColor) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            activity?.window?.insetsController?.setSystemBarsAppearance(
                when (iconColor) {
                    LIGHT -> 0
                    DARK -> WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                },
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }
}

/**
 * Enum class representing the color of the status bar icons.
 * @property LIGHT Light status bar icons for use over dark app bars.
 * @property DARK Dark status bar icons for use over light app bars.
 * @author Rohan Khayech
 */
@Immutable
enum class StatusBarIconColor {
    /** Light status bar icons for use over dark app bars. */
    LIGHT,
    /** Dark status bar icons for use over light app bars. */
    DARK
}
