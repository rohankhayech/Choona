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

package com.rohankhayech.choona.wear.view.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.rohankhayech.choona.lib.model.preferences.InitialTuningType
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.model.tuning.TuningList
import com.rohankhayech.music.Tuning

@Composable
fun MainLayout(
    tuning: TuningEntry,
    noteOffset: State<Double?>,
    selectedString: Int,
    selectedNote: Int,
    tuned: BooleanArray,
    noteTuned: Boolean,
    autoDetect: Boolean,
    chromatic: Boolean,
    favTunings: State<Set<TuningEntry>>,
    getCanonicalName: (TuningEntry.InstrumentTuning) -> String,
    prefs: TunerPreferences,
    tuningList: TuningList,
    tuningSelectorOpen: Boolean,
    configurePanelOpen: Boolean,
    onSelectString: (Int) -> Unit,
    onSelectTuning: (Tuning) -> Unit,
    onSelectChromatic: () -> Unit,
    onSelectNote: (Int) -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onAutoChanged: (Boolean) -> Unit,
    onTuned: () -> Unit,
    onOpenTuningSelector: () -> Unit,
    onOpenConfigurePanel: () -> Unit,
    onDismissTuningSelector: () -> Unit,
    onDismissConfigurePanel: () -> Unit,
) {
    AnimatedVisibility(
        visible = !tuningSelectorOpen && !configurePanelOpen,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        TunerScreen(
            tuning,
            prefs,
            selectedNote,
            noteOffset,
            selectedString,
            selectedNote,
            autoDetect,
            chromatic,
            tuned,
            noteTuned,
            onSelectString,
            onSelectNote,
            onAutoChanged,
            getCanonicalName,
            onTuned,
            onOpenConfigurePanel
        )
    }
    AnimatedVisibility(
        visible = configurePanelOpen && !tuningSelectorOpen,
        enter = slideInVertically { it/2 },
        exit = slideOutVertically { it }
    ) {
        ConfigureTuningScreen(
            tuning = tuning,
            chromatic,
            selectedNote = selectedNote,
            favTunings = favTunings,
            getCanonicalName = getCanonicalName,
            onTuneUpString = onTuneUpString,
            onTuneDownString = onTuneDownString,
            onTuneUpTuning = onTuneUpTuning,
            onTuneDownTuning = onTuneDownTuning,
            onSelectNote = onSelectNote,
            onOpenTuningSelector = onOpenTuningSelector,
            onDismiss = onDismissConfigurePanel,
            onSettingsPressed = {}
        )
    }
    AnimatedVisibility(
        visible = tuningSelectorOpen,
        enter = slideInVertically { it/2 },
        exit = slideOutVertically { it }
    ) {
        TuningListScreen(
            tuningList = tuningList,
            backIcon = if (configurePanelOpen) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Close,
            pinnedInitial = prefs.initialTuning == InitialTuningType.PINNED,
            onSelect = onSelectTuning,
            onSelectChromatic = onSelectChromatic,
            onDismiss = onDismissTuningSelector,
        )
    }
}