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

package com.rohankhayech.choona.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.tuning.TuningList
import com.rohankhayech.music.Tuning

/**
 * Layout composable for the main tuning screen.
 * Handles layout of screens on different form factors.
 *
 * @param windowSizeClass Size class of the activity window.
 * @param compact Whether to use compact layout.
 * @param expanded Whether to use expanded layout.
 * @param tuning Guitar tuning used for comparison.
 * @param noteOffset The offset between the currently playing note and the selected string.
 * @param selectedString Index of the currently selected string within the tuning.
 * @param tuned Whether each string has been tuned.
 * @param autoDetect Whether the tuner will automatically detect the currently playing string.
 * @param favTunings Set of tunings marked as favourite by the user.
 * @param customTunings Set of custom tunings added by the user.
 * @param prefs User preferences for the tuner.
 * @param tuningList State holder for the tuning list.
 * @param tuningSelectorOpen Whether the tuning selection panel is open.
 * @param configurePanelOpen Whether the configure tuning panel is open.
 * @param onSelectString Called when a string is selected.
 * @param onSelectTuning Called when a tuning is selected.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 * @param onAutoChanged Called when the auto detect switch is toggled.
 * @param onTuned Called when the detected note is held in tune.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 * @param onSettingsPressed Called when the settings button is pressed.
 * @param onConfigurePressed Called when the configure tuning button is pressed.
 * @param onSelectTuningFromList Called when a tuning is selected from the selection panel.
 * @param onDismissTuningSelector Called when the tuning selection screen is dismissed.
 * @param onDismissConfigurePanel Called when the screen is dismissed.
 *
 * @author Rohan Khayech
 */
@Composable
fun MainLayout(
    windowSizeClass: WindowSizeClass,
    compact: Boolean,
    expanded: Boolean,
    tuning: Tuning,
    noteOffset: State<Double?>,
    selectedString: Int,
    tuned: BooleanArray,
    autoDetect: Boolean,
    favTunings: State<Set<Tuning>>,
    customTunings: State<Set<Tuning>>,
    prefs: TunerPreferences,
    tuningList: TuningList,
    tuningSelectorOpen: Boolean,
    configurePanelOpen: Boolean,
    onSelectString: (Int) -> Unit,
    onSelectTuning: (Tuning) -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onAutoChanged: (Boolean) -> Unit,
    onTuned: () -> Unit,
    onOpenTuningSelector: () -> Unit,
    onSettingsPressed: () -> Unit,
    onConfigurePressed: () -> Unit,
    onSelectTuningFromList: (Tuning) -> Unit,
    onDismissTuningSelector: () -> Unit,
    onDismissConfigurePanel: () -> Unit,
) {
    if (expanded) {
        Row(Modifier.fillMaxSize()) {
            Column(Modifier.weight(0.7f)) {
                TunerScreen(
                    compact = false,
                    windowSizeClass,
                    tuning,
                    noteOffset,
                    selectedString,
                    tuned,
                    autoDetect,
                    favTunings,
                    customTunings,
                    prefs,
                    onSelectString,
                    onSelectTuning,
                    onTuneUpString,
                    onTuneDownString,
                    onTuneUpTuning,
                    onTuneDownTuning,
                    onAutoChanged,
                    onTuned,
                    onOpenTuningSelector = {},
                    onSettingsPressed,
                    onConfigurePressed = {}
                )
            }
            Column(Modifier.weight(0.3f)) {
                Surface(elevation = 8.dp) {
                    TuningSelectionScreen(
                        tuningList = tuningList,
                        backIcon = null,
                        onSelect = onSelectTuningFromList,
                        onDismiss = {}
                    )
                }
            }
        }
    } else {
        AnimatedVisibility(
            visible = !tuningSelectorOpen && !configurePanelOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            TunerScreen(
                compact,
                windowSizeClass,
                tuning,
                noteOffset,
                selectedString,
                tuned,
                autoDetect,
                favTunings,
                customTunings,
                prefs,
                onSelectString,
                onSelectTuning,
                onTuneUpString,
                onTuneDownString,
                onTuneUpTuning,
                onTuneDownTuning,
                onAutoChanged,
                onTuned,
                onOpenTuningSelector,
                onSettingsPressed,
                onConfigurePressed
            )
        }
        AnimatedVisibility(
            visible = configurePanelOpen && !tuningSelectorOpen,
            enter = slideInVertically { -it/2 },
            exit = slideOutVertically { -it }
        ) {
            ConfigureTuningScreen(
                tuning = tuning,
                favTunings = favTunings,
                customTunings = customTunings,
                onSelectTuning = onSelectTuning,
                onTuneUpString = onTuneUpString,
                onTuneDownString = onTuneDownString,
                onTuneUpTuning = onTuneUpTuning,
                onTuneDownTuning = onTuneDownTuning,
                onOpenTuningSelector = onOpenTuningSelector,
                onDismiss = onDismissConfigurePanel
            )
        }
        AnimatedVisibility(
            visible = tuningSelectorOpen,
            enter = slideInVertically { it/2 },
            exit = slideOutVertically { it }
        ) {
            TuningSelectionScreen(
                tuningList = tuningList,
                backIcon = if (configurePanelOpen) Icons.Default.ArrowBack else Icons.Default.Close,
                onSelect = onSelectTuningFromList,
                onDismiss = onDismissTuningSelector,
            )
        }
    }
}