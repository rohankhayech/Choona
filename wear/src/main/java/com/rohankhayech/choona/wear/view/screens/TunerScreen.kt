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

package com.rohankhayech.choona.wear.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HdrAuto
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconToggleButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.tooling.preview.devices.WearDevices
import com.rohankhayech.choona.lib.model.preferences.TunerPreferences
import com.rohankhayech.choona.lib.model.tuning.Notes
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.model.tuning.Tunings
import com.rohankhayech.choona.wear.view.components.CompactNoteSelector
import com.rohankhayech.choona.wear.view.components.CompactStringSelector
import com.rohankhayech.choona.wear.view.components.CurvedTuningItem
import com.rohankhayech.choona.wear.view.components.TuningDisplay
import com.rohankhayech.choona.wear.view.theme.AppTheme

@Composable
fun TunerScreen(
    tuning: TuningEntry,
    prefs: TunerPreferences,
    noteIndex: Int,
    noteOffset: State<Double?>,
    selectedString: Int,
    selectedNote: Int,
    autoDetect: Boolean,
    chromatic: Boolean,
    tuned: BooleanArray,
    noteTuned: Boolean,
    onSelectString: (Int) -> Unit,
    onSelectNote: (Int) -> Unit,
    onAutoChanged: (Boolean) -> Unit,
    getCanonicalName: (TuningEntry.InstrumentTuning) -> String,
    onTuned: () -> Unit,
    onOpenConfigurePanel: () -> Unit,
) {
    ScreenScaffold(timeText = {}) {
        val round = LocalConfiguration.current.isScreenRound

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TuningDisplay(
                noteIndex = noteIndex,
                noteOffset = noteOffset,
                displayType = prefs.displayType,
                showNote = chromatic && autoDetect,
                onTuned = onTuned
            )
            Row(
                Modifier.padding(end = if (round) 48.dp else 8.dp)
            ) {
                if (chromatic) {
                    CompactNoteSelector(
                        modifier = Modifier.weight(1f),
                        selectedNoteIndex = selectedNote,
                        contentPadding = PaddingValues(if (round) 48.dp else 8.dp, end = 8.dp),
                        tuned = noteTuned,
                        onSelect = onSelectNote
                    )
                } else {
                    CompactStringSelector(
                        modifier = Modifier.weight(1f),
                        tuning = tuning.tuning!!,
                        selectedString = selectedString,
                        contentPadding = PaddingValues(if (round) 48.dp else 8.dp, end = 8.dp),
                        tuned = tuned,
                        onSelect = onSelectString
                    )
                }
                Box(
                    Modifier
                        .height(32.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
                Box(Modifier.padding(start = 8.dp)) {
                    IconToggleButton(
                        modifier = Modifier.size(32.dp),
                        checked = autoDetect,
                        onCheckedChange = onAutoChanged
                    ) {
                        Icon(Icons.Default.HdrAuto, null)
                    }
                }
            }
            if (!round) {
                CurvedTuningItem(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp).clickable(onClick = onOpenConfigurePanel),
                    tuning = tuning,
                    getCanonicalName = getCanonicalName,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Spacer(Modifier.fillMaxWidth().height(30.dp).clickable(onClick = onOpenConfigurePanel))
            }
        }
        if (round) {
            CurvedTuningItem(
                modifier = Modifier.fillMaxSize().padding(4.dp),
                tuning = tuning,
                getCanonicalName = getCanonicalName,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Preview(device = WearDevices.SQUARE, showSystemUi = true)
@Composable
private fun Preview() {
    AppTheme {
        AppScaffold {
            TunerScreen(
                tuning = TuningEntry.InstrumentTuning(Tunings.HALF_STEP_DOWN),
                prefs = TunerPreferences(),
                noteIndex = Notes.getIndex("A4"),
                noteOffset = remember { mutableDoubleStateOf(2.0) },
                selectedString = 0,
                selectedNote = 0,
                autoDetect = true,
                chromatic = false,
                tuned = booleanArrayOf(false, true, false, false, false, true),
                noteTuned = false,
                onTuned = {},
                onSelectString = {},
                onSelectNote = {},
                onAutoChanged = {},
                getCanonicalName = { "" },
                onOpenConfigurePanel = {}
            )
        }
    }
}