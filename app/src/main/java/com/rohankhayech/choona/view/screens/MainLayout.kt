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

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.tuning.TuningList
import com.rohankhayech.music.Tuning

@Composable
fun MainLayout(
    windowSizeClass: WindowSizeClass,
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
    onSelectTuningFromList: (Tuning) -> Unit,
    onDeleteTuning: (Tuning) -> Unit = {},
    onDismissTuningSelector: () -> Unit
) {
    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
        Row(Modifier.fillMaxSize()) {
            Column(Modifier.weight(0.7f)) {
                TunerScreen(
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
                    onSettingsPressed
                )
            }
            Column(Modifier.weight(0.3f)) {
                TuningSelectionScreen(
                    tuningList = tuningList,
                    showBackButton = false,
                    onSelect = onSelectTuningFromList,
                    onDelete = onDeleteTuning,
                    onDismiss = {}
                )
            }
        }
    } else {
        AnimatedVisibility(!tuningSelectorOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            TunerScreen(
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
                onSettingsPressed
            )
        }
        AnimatedVisibility(tuningSelectorOpen,
            enter = slideInVertically { it/2 },
            exit = slideOutVertically { it }
        ) {
            TuningSelectionScreen(
                tuningList = tuningList,
                onSelect = onSelectTuningFromList,
                onDelete = onDeleteTuning,
                onDismiss = onDismissTuningSelector,
            )
        }
    }
}