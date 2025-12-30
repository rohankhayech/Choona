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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.model.tuning.Tunings
import com.rohankhayech.choona.wear.view.components.NoteSelector
import com.rohankhayech.choona.wear.view.components.StringControls
import com.rohankhayech.choona.wear.view.components.VerticalTuningItem
import com.rohankhayech.choona.wear.view.theme.AppTheme

/**
 * UI screen used to tune individual strings and the tuning
 * itself up and down, as well as select from favourite tunings.
 *
 * @param tuning Guitar tuning used for comparison.
 * @param chromatic Whether the chromatic tuning mode is enabled.
 * @param selectedNote The selected note in chromatic mode.
 * @param favTunings Set of tunings marked as favourite by the user.
 * @param getCanonicalName Gets the name of the tuning if it is saved as a custom tuning.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 * @param onSelectNote Called when a note is selected in chromatic mode.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 * @param onDismiss Called when the screen is dismissed.
 * @param onSettingsPressed Called when the settings button is pressed.
 *
 * @author Rohan Khayech
 */
@Composable
fun ConfigureTuningScreen(
    tuning: TuningEntry,
    chromatic: Boolean,
    selectedNote: Int,
    favTunings: State<Set<TuningEntry>>,
    getCanonicalName: (TuningEntry.InstrumentTuning) -> String,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onSelectNote: (Int) -> Unit,
    onOpenTuningSelector: () -> Unit,
    onDismiss: () -> Unit,
    onSettingsPressed: () -> Unit,
) {
    val listState = rememberScalingLazyListState()
    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(onClick = onSettingsPressed) {
                Text(stringResource(R.string.tuner_settings))
            }
        }
    ) { padding ->
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = padding,
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ListHeader {
                    Text(
                        stringResource(R.string.configure_tuning),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            item {
                Row {
                    Button(onClick = onOpenTuningSelector) {
                        VerticalTuningItem(
                            tuning = tuning,
                            getCanonicalName = getCanonicalName
                        )
                    }
                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(Icons.Default.Done, null)
                    }
                }
            }
            item {
                if (chromatic) {
                    NoteSelector(
                        selectedNoteIndex = selectedNote,
                        tuned = false,
                        onSelect = onSelectNote
                    )
                } else {
                    StringControls(
                        tuning = tuning.tuning!!,
                        selectedString = null,
                        tuned = null,
                        onSelect = {},
                        onTuneDown = onTuneDownString,
                        onTuneUp = onTuneUpString,
                    )
                }
            }
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
            ConfigureTuningScreen(
                tuning = TuningEntry.InstrumentTuning(Tunings.HALF_STEP_DOWN),
                chromatic = false,
                selectedNote = -29,
                favTunings = remember { mutableStateOf(emptySet()) },
                getCanonicalName = { it.toString() },
                onTuneUpString = {},
                onTuneDownString = {},
                onTuneUpTuning = {},
                onTuneDownTuning = {},
                onOpenTuningSelector = {},
                onDismiss = {},
                onSelectNote = {}
            ) {}
        }
    }
}
