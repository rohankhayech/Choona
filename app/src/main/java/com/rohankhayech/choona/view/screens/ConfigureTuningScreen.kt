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

package com.rohankhayech.choona.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rohankhayech.android.util.ui.preview.CompactOrientationThemePreview
import com.rohankhayech.choona.R
import com.rohankhayech.choona.model.tuning.TuningEntry
import com.rohankhayech.choona.model.tuning.Tunings
import com.rohankhayech.choona.view.components.NoteSelector
import com.rohankhayech.choona.view.components.StringControls
import com.rohankhayech.choona.view.components.TuningSelector
import com.rohankhayech.choona.view.theme.AppTheme

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureTuningScreen(
    tuning: TuningEntry,
    chromatic: Boolean,
    selectedNote: Int,
    favTunings: State<Set<TuningEntry>>,
    getCanonicalName: TuningEntry.InstrumentTuning.() -> String,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onSelectNote: (Int) -> Unit,
    onOpenTuningSelector: () -> Unit,
    onDismiss: () -> Unit,
    onSettingsPressed: () -> Unit,
) {
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold (
        Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.configure_tuning), style = MaterialTheme.typography.titleMedium)
                },
                actions = {
                    // Settings button
                    IconButton(onClick = onSettingsPressed) {
                        Icon(Icons.Default.Settings, stringResource(R.string.tuner_settings))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, stringResource(R.string.dismiss))
                    }
                },
                scrollBehavior = scrollBehaviour,
            )
        },
        bottomBar = {
            Column(Modifier.fillMaxWidth()) {
                HorizontalDivider(thickness = Dp.Hairline)
                BottomAppBar(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    TuningSelector(
                        tuning = tuning,
                        favTunings = favTunings,
                        getCanonicalName = getCanonicalName,
                        openDirect = true,
                        onSelect = {},
                        onTuneDown = onTuneDownTuning,
                        onTuneUp = onTuneUpTuning,
                        onOpenTuningSelector = onOpenTuningSelector,
                        editModeEnabled = true,
                        compact = true
                    )

                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (chromatic) {
                NoteSelector(
                    Modifier.padding(vertical = 8.dp),
                    selectedNoteIndex = selectedNote,
                    tuned = false,
                    onSelect = onSelectNote
                )
            } else {
                StringControls(
                    Modifier.padding(vertical = 8.dp),
                    inline = true,
                    tuning = tuning.tuning!!,
                    selectedString = null,
                    tuned = null,
                    onSelect = {},
                    onTuneDown = onTuneDownString,
                    onTuneUp = onTuneUpString,
                    editModeEnabled = true,
                )
            }
        }
    }
}

@CompactOrientationThemePreview
@Composable
private fun Preview() {
    AppTheme {
        ConfigureTuningScreen(
            tuning = TuningEntry.InstrumentTuning(Tunings.HALF_STEP_DOWN),
            chromatic = false,
            selectedNote = -29,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { this.tuning.toString() },
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