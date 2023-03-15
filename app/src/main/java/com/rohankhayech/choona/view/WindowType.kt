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

package com.rohankhayech.choona.view

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

enum class WindowType {

    /**
     * Represents most phones in portrait orientation.
     * Compact width and medium height.
     */
    PHONE_PORTRAIT,

    /**
     * Represents most phones in landscape orientation.
     * Medium width and compact height.
     */
    PHONE_LANDSCAPE,

    /**
     * Represents most tablets in portrait orientation.
     * Medium width and expanded height.
     */
    TABLET_PORTRAIT,

    /**
     * Represents most phones in portrait orientation.
     * Expanded width and medium height.
     */
    TABLET_LANDSCAPE,

    /**
     * Compact width and height.
     * Eg. Multi-window on phones.
     */
    COMPACT;

    companion object {
        fun calculateWindowType(windowSizeClass: WindowSizeClass): WindowType {
            return when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    when (windowSizeClass.heightSizeClass) {
                        WindowHeightSizeClass.Compact -> COMPACT
                        else -> PHONE_PORTRAIT
                    }
                }
                WindowWidthSizeClass.Medium -> {
                    when (windowSizeClass.heightSizeClass) {
                        WindowHeightSizeClass.Compact -> PHONE_LANDSCAPE
                        else -> TABLET_PORTRAIT
                    }
                }
                WindowWidthSizeClass.Expanded -> {
                    when (windowSizeClass.heightSizeClass) {
                        WindowHeightSizeClass.Compact -> PHONE_LANDSCAPE
                        else -> TABLET_LANDSCAPE
                    }
                }
                else -> throw IllegalArgumentException("Invalid window size class.")
            }
        }
    }
}