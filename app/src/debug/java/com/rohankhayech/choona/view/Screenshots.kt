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

package com.rohankhayech.choona.view

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohankhayech.android.util.ui.preview.CompactOrientationPreview
import com.rohankhayech.android.util.ui.preview.DarkPreview
import com.rohankhayech.android.util.ui.preview.TabletThemePreview
import com.rohankhayech.choona.lib.model.preferences.StringLayout
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.model.preferences.TuningDisplayType
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.model.tuning.TuningList
import com.rohankhayech.choona.lib.model.tuning.Tunings
import com.rohankhayech.choona.view.screens.MainLayout
import com.rohankhayech.choona.view.screens.SaveTuningDialog
import com.rohankhayech.choona.view.screens.SettingsScreen
import com.rohankhayech.choona.view.screens.TunerScreen
import com.rohankhayech.choona.view.screens.TuningSelectionScreen
import com.rohankhayech.choona.view.theme.AppTheme
import com.rohankhayech.music.Notes
import com.rohankhayech.music.Tuning

// Previews for generating screenshots.

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DarkPreview
@Composable
private fun TunerScreenshot() {
    AppTheme {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = TuningEntry.InstrumentTuning(Tunings.STANDARD),
            noteOffset = remember { mutableDoubleStateOf(0.3) },
            selectedString = 3,
            selectedNote = -29,
            tuned = BooleanArray(6) { false },
            noteTuned = false,
            autoDetect = true,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { it.toString() },
            prefs = TunerPreferences(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, true, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DarkPreview
@Composable
private fun InTuneScreenshot() {
    AppTheme {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = TuningEntry.InstrumentTuning(Tunings.DROP_D),
            noteOffset = remember { mutableDoubleStateOf(0.01) },
            selectedString = 5,
            selectedNote = 0, // Assuming a valid note for Drop D, string 5
            tuned = BooleanArray(6) { it == 5 },
            noteTuned = true, // Implied by "InTune"
            autoDetect = true,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet<TuningEntry>()) },
            getCanonicalName = { it.toString() },
            prefs = TunerPreferences(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, false, {}
        )
    }
}

@DarkPreview
@Composable
private fun SelectionScreenshot() {
    val tunings = TuningList(Tunings.WHOLE_STEP_DOWN).apply {
        setFavourited(TuningEntry.InstrumentTuning(Tunings.DROP_D), true)
        addCustom("Example", Tuning.fromString("F4 C4 G#3 D#3 A#2 F2"))
    }

    AppTheme {
        TuningSelectionScreen(
            tuningList = tunings,
            pinnedInitial = true,
            backIcon = Icons.Default.Close,
            onSelect = {},
            onSelectChromatic = {},
            onDismiss = {}
        )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@DarkPreview
@Composable
private fun CustomScreenshot() {
    val current = TuningEntry.InstrumentTuning(Tuning.fromString("F4 C4 G#3 D#3 A#2 F2"))
    val tunings = TuningList(current.tuning).apply {
        setFavourited(TuningEntry.InstrumentTuning(Tunings.DROP_D), true)
    }

    AppTheme {
        TuningSelectionScreen (
            current = tunings.current.value,
            tunings = tunings.filteredTunings.value,
            favourites = tunings.favourites.value,
            custom = tunings.custom.value,
            pinned = TuningEntry.InstrumentTuning(Tuning.STANDARD),
            pinnedInitial = true,
            instrumentFilter = null,
            categoryFilter = null,
            instrumentFilters = tunings.instrumentFilters.collectAsStateWithLifecycle(),
            categoryFilters = tunings.categoryFilters.collectAsStateWithLifecycle(),
            backIcon = Icons.Default.Close,
            deletedTuning = tunings.deletedTuning,
            onSelectInstrument = {},
            onSelectCategory = {},
            onSave = {_, _ ->},
            onFavouriteSet = {_, _ ->},
            onSelect = {},
            onDelete = {},
            onDismiss = {},
            onPin = {},
            onUnpin = {},
            isFavourite = { tunings.run {
                this@TuningSelectionScreen.isFavourite()
            }},
            currentSaved = false
        )

        SaveTuningDialog(
            tuning = current.tuning,
            onSave = { _, _ -> },
            onDismiss = {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DarkPreview
@Composable
private fun ChromaticScreenshot() {
    AppTheme {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = TuningEntry.ChromaticTuning,
            noteOffset = remember { mutableDoubleStateOf(-0.4) },
            selectedString = 3,
            selectedNote = Notes.getIndex("D3"),
            tuned = BooleanArray(6) { false },
            noteTuned = false,
            autoDetect = true,
            chromatic = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { it.toString() },
            prefs = TunerPreferences(
                stringLayout = StringLayout.SIDE_BY_SIDE
            ),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, false, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun SemitonesScreenshot() {
    AppTheme {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = TuningEntry.InstrumentTuning(Tunings.STANDARD),
            noteOffset = remember { mutableDoubleStateOf(-3.6) },
            selectedString = 3,
            selectedNote = -29,
            tuned = BooleanArray(6) { false },
            noteTuned = false,
            autoDetect = false,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { it.toString() },
            prefs = TunerPreferences(
                displayType = TuningDisplayType.SEMITONES,
                stringLayout = StringLayout.SIDE_BY_SIDE
            ),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, false, {}
        )
    }
}

@Composable
@DarkPreview
private fun SettingsScreenshot() {
    AppTheme {
        SettingsScreen(
            prefs = TunerPreferences(
                enableInTuneSound = false
            ),
            pinnedTuning = "Standard",
            {},{}, {},{},{},{},{}, {}, {}, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DarkPreview
@Composable
private fun BlackThemeScreenshot() {
    AppTheme(fullBlack = true) {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = TuningEntry.InstrumentTuning(Tunings.DROP_D),
            noteOffset = remember { mutableDoubleStateOf(-0.42) },
            selectedString = 3,
            selectedNote = -29,
            tuned = BooleanArray(6) { it==5 || it==4 },
            noteTuned = false,
            autoDetect = true,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { it.toString() },
            prefs = TunerPreferences(
                useBlackTheme = true,
                displayType = TuningDisplayType.CENTS
            ),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, true, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@CompactOrientationPreview
@Composable
private fun SplitScreenScreenshot() {
    AppTheme(darkTheme = true) {
        TunerScreen(
            compact = true,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = TuningEntry.InstrumentTuning(Tunings.STANDARD),
            noteOffset = remember { mutableDoubleStateOf(0.3) },
            selectedString = 3,
            selectedNote = -29,
            tuned = BooleanArray(6) { false },
            noteTuned = false,
            autoDetect = true,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { it.toString() },
            prefs = TunerPreferences(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, true, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@TabletThemePreview
@Composable
private fun TabletScreenshot() {
    val tunings = TuningList(Tunings.WHOLE_STEP_DOWN).apply {
        setFavourited(TuningEntry.InstrumentTuning(Tunings.DROP_D), true)
        addCustom("Example", Tuning.fromString("F4 C4 G#3 D#3 A#2 F2"))
    }

    AppTheme {
        MainLayout(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(891.dp, 891.dp)),
            compact = false,
            expanded = true,
            tuning = TuningEntry.InstrumentTuning(Tunings.HALF_STEP_DOWN),
            noteOffset = remember { mutableDoubleStateOf(0.3) },
            selectedString = 3,
            selectedNote = -28,
            tuned = BooleanArray(6) { false },
            noteTuned = false,
            autoDetect = true,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { it.toString() },
            prefs = TunerPreferences(),
            tuningList = tunings,
            tuningSelectorOpen = false,
            configurePanelOpen = false,
            true,
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}
        )
    }
}