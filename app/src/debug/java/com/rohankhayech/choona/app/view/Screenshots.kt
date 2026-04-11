/*
 * Choona - Guitar Tuner
 * Copyright (C) 2026 Rohan Khayech
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

package com.rohankhayech.choona.app.view

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
import com.rohankhayech.android.util.ui.preview.CompactOrientationPreview
import com.rohankhayech.android.util.ui.preview.DarkPreview
import com.rohankhayech.android.util.ui.preview.TabletThemePreview
import com.rohankhayech.choona.app.view.screens.EditTuningScreen
import com.rohankhayech.choona.app.view.screens.MainLayout
import com.rohankhayech.choona.app.view.screens.SettingsScreen
import com.rohankhayech.choona.app.view.screens.TunerScreen
import com.rohankhayech.choona.app.view.screens.TuningSelectionScreen
import com.rohankhayech.choona.app.view.theme.AppTheme
import com.rohankhayech.choona.lib.controller.tunings.TuningList
import com.rohankhayech.choona.lib.model.preferences.StringLayout
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.model.preferences.TuningDisplayType
import com.rohankhayech.choona.lib.model.tuning.ChromaticTuning
import com.rohankhayech.choona.lib.model.tuning.InstrumentTuning
import com.rohankhayech.choona.lib.model.tuning.Notes
import com.rohankhayech.choona.lib.model.tuning.Tunings
import com.rohankhayech.choona.lib.view.viewmodel.TunerViewModel.Screen

/** @file Previews for generating screenshots. */

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DarkPreview
@Composable
private fun TunerScreenshot() {
    AppTheme {
        TunerScreen(
            granted = true,
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = Tunings.STANDARD,
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
            canRequest = true,
            error = null,
            onSelectString = {},
            onSelectTuning = {},
            onSelectChromatic = {},
            onSelectNote = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onAutoChanged = {},
            onTuned = {},
            onOpenTuningSelector = {},
            onSettingsPressed = {},
            onConfigurePressed = {},
            editModeEnabled = true,
            onEditModeChanged = {},
            onRequestPermission = {},
            onOpenPermissionSettings = {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DarkPreview
@Composable
private fun InTuneScreenshot() {
    AppTheme {
        TunerScreen(
            granted = true,
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = Tunings.DROP_D,
            noteOffset = remember { mutableDoubleStateOf(0.01) },
            selectedString = 5,
            selectedNote = 0,
            tuned = BooleanArray(6) { it == 5 },
            noteTuned = true,
            autoDetect = true,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { it.toString() },
            prefs = TunerPreferences(),
            canRequest = true,
            error = null,
            onSelectString = {},
            onSelectTuning = {},
            onSelectChromatic = {},
            onSelectNote = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onAutoChanged = {},
            onTuned = {},
            onOpenTuningSelector = {},
            onSettingsPressed = {},
            onConfigurePressed = {},
            editModeEnabled = false,
            onEditModeChanged = {},
            onRequestPermission = {},
            onOpenPermissionSettings = {}
        )
    }
}

@DarkPreview
@Composable
private fun SelectionScreenshot() {
    val tunings = TuningList(Tunings.WHOLE_STEP_DOWN).apply {
        setFavourited(Tunings.DROP_D, true)
        addCustom("Example", InstrumentTuning.fromString("F4 C4 G#3 D#3 A#2 F2"))
    }

    AppTheme {
        TuningSelectionScreen(
            tuningList = tunings,
            pinnedInitial = true,
            backIcon = Icons.Default.Close,
            onSelect = {},
            onSelectChromatic = {},
            onOpenTuningEditor = {_,_->},
            onDismiss = {}
        )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@DarkPreview
@Composable
private fun CustomScreenshot() {
    val custom = InstrumentTuning.fromString("F4 C4 G#3 D#3 A#2 F2")

    AppTheme {
        EditTuningScreen(
            name = "",
            new = true,
            tuning = custom,
            onNameChange = {},
            onInstrumentChange = {},
            onSetString = { _, _ -> },
            onAddLowString = {},
            onAddHighString = {},
            onRemoveLowString = {},
            onRemoveHighString = {},
            onTuneStringUp = {},
            onTuneStringDown = {},
            onTuneUp = {},
            onTuneDown = {},
            onPressNote = { _, _ -> },
            onCancel = {},
            onSave = {},
            onDelete = {}
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
            tuning = ChromaticTuning,
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
            onSelectString = {},
            onSelectTuning = {},
            onSelectChromatic = {},
            onSelectNote = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onAutoChanged = {},
            onTuned = {},
            onOpenTuningSelector = {},
            onSettingsPressed = {},
            onConfigurePressed = {},
            editModeEnabled = false,
            onEditModeChanged = {},
            canRequest = true,
            onRequestPermission = {},
            onOpenPermissionSettings = {},
            error = null,
            granted = true,
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun SemitonesScreenshot() {
    AppTheme {
        TunerScreen(
            granted = true,
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = Tunings.STANDARD,
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
            canRequest = true,
            error = null,
            onSelectString = {},
            onSelectTuning = {},
            onSelectChromatic = {},
            onSelectNote = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onAutoChanged = {},
            onTuned = {},
            onOpenTuningSelector = {},
            onSettingsPressed = {},
            onConfigurePressed = {},
            editModeEnabled = false,
            onEditModeChanged = {},
            onRequestPermission = {},
            onOpenPermissionSettings = {}
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
            onSelectStringLayout = {},
            onSelectDisplayType = {},
            onEnableStringSelectSound = {},
            onEnableInTuneSound = {},
            onSetUseBlackTheme = {},
            onSetUseDynamicColor = {},
            onToggleEditModeDefault = {},
            onSelectInitialTuning = {},
            onAboutPressed = {},
            onBackPressed = {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DarkPreview
@Composable
private fun BlackThemeScreenshot() {
    AppTheme(fullBlack = true) {
        TunerScreen(
            granted = true,
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = Tunings.DROP_D,
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
            onSelectString = {},
            onSelectTuning = {},
            onSelectChromatic = {},
            onSelectNote = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onAutoChanged = {},
            onTuned = {},
            onOpenTuningSelector = {},
            onSettingsPressed = {},
            onConfigurePressed = {},
            editModeEnabled = true,
            onEditModeChanged = {},
            canRequest = true,
            onRequestPermission = {},
            onOpenPermissionSettings = {},
            error = null
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@CompactOrientationPreview
@Composable
private fun SplitScreenScreenshot() {
    AppTheme(darkTheme = true) {
        TunerScreen(
            granted = true,
            compact = true,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = Tunings.STANDARD,
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
            canRequest = true,
            error = null,
            onSelectString = {},
            onSelectTuning = {},
            onSelectChromatic = {},
            onSelectNote = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onAutoChanged = {},
            onTuned = {},
            onOpenTuningSelector = {},
            onSettingsPressed = {},
            onConfigurePressed = {},
            editModeEnabled = true,
            onEditModeChanged = {},
            onRequestPermission = {},
            onOpenPermissionSettings = {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@TabletThemePreview
@Composable
private fun TabletScreenshot() {
    val tunings = TuningList(Tunings.WHOLE_STEP_DOWN).apply {
        setFavourited(Tunings.DROP_D, true)
        addCustom("Example", InstrumentTuning.fromString("F4 C4 G#3 D#3 A#2 F2"))
    }

    AppTheme {
        MainLayout(
            backStack = listOf(Screen.Tuner, Screen.TuningSelection),
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(891.dp, 891.dp)),
            compact = false,
            expanded = true,
            tuning = Tunings.HALF_STEP_DOWN,
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
            editModeEnabled = true,
            onEditModeChanged = {},
            onSelectString = {},
            onSelectTuning = {},
            onSelectChromatic = {},
            onSelectNote = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onAutoChanged = {},
            onTuned = {},
            onOpenTuningSelector = {},
            onSettingsPressed = {},
            onConfigurePressed = {},
            onSelectTuningFromList = {},
            onSelectChromaticFromList = {},
            onOpenTuningEditor = { _, _ -> },
            onBack = {},
            canRequest = true,
            onRequestPermission = {},
            onOpenPermissionSettings = {},
            error = null,
            granted = true,
            onPressNote = {_, _ ->},
            onSaveTuningFromEditor = { _, _ -> },
            onDeleteTuningFromEditor = { _ -> }
        )
    }
}
