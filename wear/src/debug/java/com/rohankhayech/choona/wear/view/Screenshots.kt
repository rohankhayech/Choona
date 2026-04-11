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

package com.rohankhayech.choona.wear.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.timeTextCurvedText
import com.rohankhayech.android.util.ui.preview.wear.WearLargePreview
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.controller.tunings.TuningList
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.model.preferences.TuningDisplayType
import com.rohankhayech.choona.lib.model.tuning.ChromaticTuning
import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.InstrumentTuning
import com.rohankhayech.choona.lib.model.tuning.Notes
import com.rohankhayech.choona.lib.model.tuning.Tuning.Category
import com.rohankhayech.choona.lib.model.tuning.Tunings
import com.rohankhayech.choona.wear.view.screens.ConfigureTuningScreen
import com.rohankhayech.choona.wear.view.screens.EditTuningScreen
import com.rohankhayech.choona.wear.view.screens.SettingsScreen
import com.rohankhayech.choona.wear.view.screens.TunerScreen
import com.rohankhayech.choona.wear.view.screens.TuningSelectionScreen
import com.rohankhayech.choona.wear.view.theme.AppTheme

/** @file Previews for generating wear screenshots. */

@WearLargePreview
@Composable
private fun TunerScreenshot() {
    AppTheme {
        AppScaffold(timeText = {}) {
            TunerScreen(
                granted = true,
                tuning = Tunings.STANDARD,
                prefs = TunerPreferences(),
                noteOffset = remember { mutableStateOf<Double?>(0.3) },
                selectedString = 3,
                selectedNote = -29,
                autoDetect = true,
                chromatic = false,
                tuned = BooleanArray(6) { false },
                noteTuned = false,
                canRequest = true,
                error = null,
                onSelectString = {},
                onSelectNote = {},
                onAutoChanged = {},
                getCanonicalName = { it.toString() },
                onTuned = {},
                onOpenConfigurePanel = {},
                onRequestPermission = {},
                onOpenPermissionSettings = {}
            )
        }
    }
}

@WearLargePreview
@Composable
private fun InTuneScreenshot() {
    AppTheme {
        AppScaffold(timeText = {}) {
            TunerScreen(
                granted = true,
                tuning = Tunings.DROP_D,
                prefs = TunerPreferences(),
                noteOffset = remember { mutableStateOf<Double?>(0.01) },
                selectedString = 5,
                selectedNote = 0,
                autoDetect = true,
                chromatic = false,
                tuned = BooleanArray(6) { it == 5 },
                noteTuned = true,
                canRequest = true,
                error = null,
                onSelectString = {},
                onSelectNote = {},
                onAutoChanged = {},
                getCanonicalName = { it.toString() },
                onTuned = {},
                onOpenConfigurePanel = {},
                onRequestPermission = {},
                onOpenPermissionSettings = {}
            )
        }
    }
}

@WearLargePreview
@Composable
private fun SelectionScreenshot() {
    val customTuning = InstrumentTuning.fromString("Example", Instrument.GUITAR, null, "F4 C4 G#3 D#3 A#2 F2")

    AppTheme {
        AppScaffold(
            timeText = {
                val appName = stringResource(R.string.app_name)
                TimeText { time -> timeTextCurvedText("$appName ‧ $time") }
            }
        ) {
            TuningSelectionScreen(
                current = null,
                currentSaved = true,
                tunings = TuningList.GROUPED_TUNINGS,
                pinned = Tunings.STANDARD,
                pinnedInitial = true,
                favourites = setOf(Tunings.WHOLE_STEP_DOWN),
                custom = setOf(customTuning),
                instrumentFilter = null,
                categoryFilter = null,
                instrumentFilters = remember { mutableStateOf(Instrument.entries.associateWith { true }) },
                categoryFilters = remember { mutableStateOf(Category.entries.associateWith { true }) },
                isFavourite = { this == Tunings.WHOLE_STEP_DOWN },
                onSelectInstrument = {},
                onSelectCategory = {},
                onSave = { _, _ -> },
                onFavouriteSet = { _, _ -> },
                onSelect = {},
                onDelete = {},
                onOpenTuningEditor = { _, _ -> },
                onDismiss = {},
                onPin = {},
                onUnpin = {}
            )
        }
    }
}

@WearLargePreview
@Composable
private fun CustomScreenshot() {
    val custom = InstrumentTuning.fromString("F4 C4 G#3 D#3 A#2 F2")

    AppTheme {
        AppScaffold(
            timeText = {
                val appName = stringResource(R.string.app_name)
                TimeText { time -> timeTextCurvedText("$appName ‧ $time") }
            }
        ) {
            EditTuningScreen(
                name = custom.name,
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
}

@WearLargePreview
@Composable
private fun ChromaticScreenshot() {
    AppTheme {
        AppScaffold(timeText = {}) {
            TunerScreen(
                granted = true,
                tuning = ChromaticTuning,
                prefs = TunerPreferences(),
                noteOffset = remember { mutableStateOf<Double?>(-0.4) },
                selectedString = 3,
                selectedNote = Notes.getIndex("D3"),
                autoDetect = true,
                chromatic = true,
                tuned = BooleanArray(6) { false },
                noteTuned = false,
                canRequest = true,
                error = null,
                onSelectString = {},
                onSelectNote = {},
                onAutoChanged = {},
                getCanonicalName = { it.toString() },
                onTuned = {},
                onOpenConfigurePanel = {},
                onRequestPermission = {},
                onOpenPermissionSettings = {}
            )
        }
    }
}


@WearLargePreview
@Composable
private fun ConfigureScreenshot() {
    AppTheme(
        dynamicColor = true
    ) {
        AppScaffold(timeText = {}) {
            ConfigureTuningScreen(
                tuning = Tunings.DROP_D,
                selectedNote = -29,
                chromatic = false,
                onSelectNote = {},
                getCanonicalName = { it.toString() },
                onTuneUpString = {},
                onTuneDownString = {},
                onTuneUpTuning = {},
                onTuneDownTuning = {},
                onOpenTuningSelector = {},
                onDismiss = { },
                onSettingsPressed = {},
            )
        }
    }
}


@WearLargePreview
@Composable
private fun SemitonesScreenshot() {
    AppTheme {
        AppScaffold(timeText = {}) {
            TunerScreen(
                granted = true,
                tuning = Tunings.STANDARD,
                prefs = TunerPreferences(
                    displayType = TuningDisplayType.SEMITONES
                ),
                noteOffset = remember { mutableStateOf<Double?>(-3.6) },
                selectedString = 3,
                selectedNote = -29,
                autoDetect = false,
                chromatic = false,
                tuned = BooleanArray(6) { false },
                noteTuned = false,
                canRequest = true,
                error = null,
                onSelectString = {},
                onSelectNote = {},
                onAutoChanged = {},
                getCanonicalName = { it.toString() },
                onTuned = {},
                onOpenConfigurePanel = {},
                onRequestPermission = {},
                onOpenPermissionSettings = {}
            )
        }
    }
}

@WearLargePreview
@Composable
private fun SettingsScreenshot() {
    AppTheme {
        AppScaffold(
            timeText = {
                val appName = stringResource(R.string.app_name)
                TimeText { time -> timeTextCurvedText("$appName ‧ $time") }
            }
        ) {
            SettingsScreen(
                prefs = TunerPreferences(
                    enableInTuneSound = false
                ),
                pinnedTuning = "Standard",
                onSelectDisplayType = {},
                onEnableStringSelectSound = {},
                onEnableInTuneSound = {},
                onSetUseDynamicColor = {},
                onSelectInitialTuning = {},
                onAboutPressed = {}
            )
        }
    }
}