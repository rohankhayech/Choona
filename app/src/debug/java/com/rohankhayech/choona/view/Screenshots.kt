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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohankhayech.android.util.ui.preview.CompactOrientationPreview
import com.rohankhayech.android.util.ui.preview.DarkPreview
import com.rohankhayech.android.util.ui.preview.TabletPreview
import com.rohankhayech.choona.model.preferences.StringLayout
import com.rohankhayech.choona.model.preferences.TunerPreferences
import com.rohankhayech.choona.model.preferences.TuningDisplayType
import com.rohankhayech.choona.model.tuning.TuningList
import com.rohankhayech.choona.model.tuning.Tunings
import com.rohankhayech.choona.view.screens.MainLayout
import com.rohankhayech.choona.view.screens.SaveTuningDialog
import com.rohankhayech.choona.view.screens.SettingsScreen
import com.rohankhayech.choona.view.screens.TunerScreen
import com.rohankhayech.choona.view.screens.TuningSelectionScreen
import com.rohankhayech.choona.view.theme.AppTheme
import com.rohankhayech.music.Tuning

// Previews for generating screenshots.

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun TunerScreenshot() {
    AppTheme {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = Tunings.STANDARD,
            noteOffset = remember { mutableStateOf(0.3)},
            selectedString = 3,
            tuned = BooleanArray(6) { false },
            autoDetect = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs = TunerPreferences(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, true, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun InTuneScreenshot() {
    AppTheme {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = Tunings.DROP_D,
            noteOffset = remember { mutableStateOf(0.01)},
            selectedString = 5,
            tuned = BooleanArray(6) { it == 5 },
            autoDetect = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs = TunerPreferences(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, false, {}
        )
    }
}

@Preview
@Composable
private fun SelectionScreenshot() {
    val tunings = TuningList(Tunings.WHOLE_STEP_DOWN).apply {
        setFavourited(Tunings.DROP_D, true)
        addCustom("Example", Tuning.fromString("F4 C4 G#3 D#3 A#2 F2"))
    }

    AppTheme {
        TuningSelectionScreen(
            tuningList = tunings,
            pinnedInitial = true,
            backIcon = Icons.Default.Close,
            onSelect = {},
            onDismiss = {}
        )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Preview
@Composable
private fun CustomScreenshot() {
    val current = Tuning.fromString("F4 C4 G#3 D#3 A#2 F2")
    val tunings = TuningList(current).apply {
        setFavourited(Tunings.DROP_D, true)
    }

    AppTheme {
        TuningSelectionScreen(
            current = tunings.current.value,
            tunings = tunings.filteredTunings.value,
            favourites = tunings.favourites.value,
            custom = emptySet(),
            pinned = Tuning.STANDARD,
            pinnedInitial = true,
            instrumentFilter = null,
            categoryFilter = null,
            instrumentFilters = tunings.instrumentFilters.collectAsStateWithLifecycle(),
            categoryFilters = tunings.categoryFilters.collectAsStateWithLifecycle(),
            backIcon = Icons.Default.Close,
            deletedTuning = tunings.deletedTuning,
            {}, {}, {_,_->}, {_,_->}, {}, {}, {}, {}, {}
        )

        SaveTuningDialog(
            tuning = current,
            onSave = { _, _ -> },
            onDismiss = {}
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
            tuning = Tunings.STANDARD,
            noteOffset = remember { mutableStateOf(-3.6)},
            selectedString = 3,
            tuned = BooleanArray(6) { false },
            autoDetect = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs = TunerPreferences(
                stringLayout = StringLayout.SIDE_BY_SIDE,
                displayType = TuningDisplayType.SEMITONES
            ),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, false, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun CentsScreenshot() {
    AppTheme {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = Tunings.STANDARD,
            noteOffset = remember { mutableStateOf(-0.42)},
            selectedString = 3,
            tuned = BooleanArray(6) { false },
            autoDetect = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs = TunerPreferences(
                displayType = TuningDisplayType.CENTS
            ),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, true, {}
        )
    }
}

@Composable
@Preview
private fun SettingsScreenshot() {
    AppTheme {
        SettingsScreen(
            prefs = TunerPreferences(
                enableInTuneSound = false
            ),
            pinnedTuning = "Standard",
            {},{},{},{},{},{},{}, {}, {}, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DarkPreview
@Composable
private fun DarkThemeScreenshot() {
    AppTheme {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = Tunings.DROP_D,
            noteOffset = remember { mutableStateOf(-0.2)},
            selectedString = 3,
            tuned = BooleanArray(6) { it==5 || it==4 },
            autoDetect = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs = TunerPreferences(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, true, {}
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
            tuning = Tunings.DROP_D,
            noteOffset = remember { mutableStateOf(-0.2)},
            selectedString = 3,
            tuned = BooleanArray(6) { it==5 || it==4 },
            autoDetect = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs = TunerPreferences(
                useBlackTheme = true
            ),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, true, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@CompactOrientationPreview
@Composable
private fun SplitScreenScreenshot() {
    AppTheme {
        TunerScreen(
            compact = true,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = Tunings.STANDARD,
            noteOffset = remember { mutableStateOf(0.3)},
            selectedString = 3,
            tuned = BooleanArray(6) { false },
            autoDetect = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs = TunerPreferences(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, true, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@TabletPreview
@Composable
private fun TabletScreenshot() {
    val tunings = TuningList(Tunings.WHOLE_STEP_DOWN).apply {
        setFavourited(Tunings.DROP_D, true)
        addCustom("Example", Tuning.fromString("F4 C4 G#3 D#3 A#2 F2"))
    }

    AppTheme {
        MainLayout(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(891.dp, 891.dp)),
            compact = false,
            expanded = true,
            tuning = Tunings.WHOLE_STEP_DOWN,
            noteOffset = remember { mutableStateOf(0.3)},
            selectedString = 3,
            tuned = BooleanArray(6) { false },
            autoDetect = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            customTunings = remember { mutableStateOf(emptySet()) },
            prefs = TunerPreferences(),
            tuningList = tunings,
            tuningSelectorOpen = false,
            configurePanelOpen = false,
            true,
            {}, {},{},{},{},{},{},{},{},{},{},{},{},{},{}
        )
    }
}