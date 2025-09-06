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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rohankhayech.android.util.ui.preview.DarkPreview
import com.rohankhayech.android.util.ui.preview.TabletThemePreview
import com.rohankhayech.android.util.ui.preview.ThemePreview
import com.rohankhayech.choona.R
import com.rohankhayech.choona.model.tuning.Tunings
import com.rohankhayech.choona.view.components.InlineStringControls
import com.rohankhayech.choona.view.theme.AppTheme
import com.rohankhayech.choona.view.util.getLocalisedName
import com.rohankhayech.music.Instrument
import com.rohankhayech.music.Tuning

/**
 * UI screen used to tune individual strings and the tuning
 * itself up and down, as well as select from favourite tunings.
 *
 * @param tuning Guitar tuning used for comparison.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 *
 * @author Rohan Khayech
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTuningScreen(
    new: Boolean,
    tuning: Tuning,
    existingTuningName: String?,
    onSetString: (n: Int, noteIndex: Int) -> Unit,
    onAddLowString: (noteIndex: Int) -> Unit,
    onAddHighString: (noteIndex: Int) -> Unit,
    onRemoveLowString: () -> Unit,
    onRemoveHighString: () -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold (
        Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(if (new) "Add custom tuning" else "Edit Tuning")
                },
                actions = {
                    // Save button.
                    Button(modifier = Modifier.padding(horizontal = 16.dp), onClick = onSave) {
                        Text("Save")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, stringResource(R.string.dismiss))
                    }
                },
                scrollBehavior = scrollBehaviour,
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var name by remember { mutableStateOf("") }
            var instrument by remember { mutableStateOf(Instrument.GUITAR) }

            EditTuningForm(
                new = new,
                name = name,
                instrument = instrument,
                tuning = tuning,
                onAddLowString = onAddLowString,
                onAddHighString = onAddHighString,
                onRemoveLowString = onRemoveLowString,
                onRemoveHighString = onRemoveHighString,
                onTuneUpString = onTuneUpString,
                onTuneDownString = onTuneDownString,
                onTuneUpTuning = onTuneUpTuning,
                onTuneDownTuning = onTuneDownTuning,
                onNameChange = { name = it },
                onInstrumentChange = { instrument = it }
            )

            // Delete button.
            if (!new) {
                TextButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = {},
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTuningDialog(
    new: Boolean,
    tuning: Tuning,
    onAddLowString: (Int) -> Unit,
    onAddHighString: (Int) -> Unit,
    onRemoveLowString: () -> Unit,
    onRemoveHighString: () -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(if (new) "Add custom tuning" else "Edit Tuning")
        },
        text = {
            Column(
                Modifier
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var name by remember { mutableStateOf("") }
                var instrument by remember { mutableStateOf(Instrument.GUITAR) }

                EditTuningForm(
                    new = new,
                    name = name,
                    instrument = instrument,
                    tuning = tuning,
                    onAddLowString = onAddLowString,
                    onAddHighString = onAddHighString,
                    onRemoveLowString = onRemoveLowString,
                    onRemoveHighString = onRemoveHighString,
                    onTuneUpString = onTuneUpString,
                    onTuneDownString = onTuneDownString,
                    onTuneUpTuning = onTuneUpTuning,
                    onTuneDownTuning = onTuneDownTuning,
                    onNameChange = { name = it },
                    onInstrumentChange = { instrument = it }
                )
            }
        },
        confirmButton = {
            // Save button.
            Button(modifier = Modifier.padding(horizontal = 16.dp), onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = if (!new) {{
            // Delete button.
            TextButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = {},
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Delete, null)
                Spacer(Modifier.width(8.dp))
                Text("Delete")
            }
        }} else null
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTuningForm(
    new: Boolean,
    name: String,
    instrument: Instrument,
    tuning: Tuning,
    onAddLowString: (Int) -> Unit,
    onAddHighString: (Int) -> Unit,
    onRemoveLowString: () -> Unit,
    onRemoveHighString: () -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onNameChange: (String) -> Unit,
    onInstrumentChange: (Instrument) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            label = { Text("Name") },
            placeholder = { Text(tuning.name) },
            onValueChange = onNameChange
        )

        var instrExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = instrExpanded, onExpandedChange = { instrExpanded = it }) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(painterResource(R.drawable.guitar_electric), null) },
                value = instrument.getLocalisedName(),
                label = { Text("Instrument") },
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = instrExpanded
                    )
                },
            )
            ExposedDropdownMenu(
                expanded = instrExpanded,
                onDismissRequest = { instrExpanded = false }
            ) {
                Instrument.entries.forEach { instr ->
                    DropdownMenuItem(
                        text = { Text(instr.getLocalisedName()) },
                        onClick = {
                            instrExpanded = false
                            onInstrumentChange(instr)
                        }
                    )
                }
            }
        }
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Strings",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                AddRemoveRow(
                    onAddString = onAddHighString,
                    onRemoveString = onRemoveHighString
                )
                InlineStringControls(
                    tuning = tuning,
                    selectedString = null,
                    tuned = null,
                    onSelect = {},
                    onTuneDown = onTuneDownString,
                    onTuneUp = onTuneUpString,
                    editModeEnabled = true
                )
                AddRemoveRow(
                    onAddString = onAddLowString,
                    onRemoveString = onRemoveLowString
                )
                Row {
                        TextButton(
                            onClick = onTuneDownTuning,
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Tune Down")
                        }
                        TextButton(
                            onClick = onTuneUpTuning,
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Tune Up")
                        }
                }
            }
        }
    }
}

@Composable
fun AddRemoveRow(
    onAddString: (Int) -> Unit,
    onRemoveString: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilledTonalIconButton(
            modifier = Modifier.size(32.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
            onClick = onRemoveString,
        ) {
            Icon(Icons.Default.Delete, "Remove String", modifier = Modifier.size(20.dp))
        }
        FilledTonalIconButton(
            modifier = Modifier.size(32.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            onClick = { onAddString(0) }
        ) {
            Icon(Icons.Default.Add, "Add String", modifier = Modifier.size(20.dp),)
        }

    }
}

@ThemePreview
@Composable
private fun Preview() {
    AppTheme {
        EditTuningScreen(
            new = false,
            tuning = Tunings.BASS_STANDARD,
            onAddLowString = {},
            onAddHighString = {},
            onRemoveLowString = {},
            onRemoveHighString = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onCancel = {},
            onSave = {},
            existingTuningName = "",
            onSetString = {_,_->}
        )
    }
}

@TabletThemePreview
@Composable
private fun DialogPreview() {
    AppTheme {
        EditTuningDialog(
            new = false,
            tuning = Tunings.BASS_STANDARD,
            onAddLowString = {},
            onAddHighString = {},
            onRemoveLowString = {},
            onRemoveHighString = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onCancel = {},
            onSave = {}
        )
    }
}

@DarkPreview
@Composable
private fun TrueDarkPreview() {
    AppTheme(fullBlack = true) {
        EditTuningScreen(
            new = true,
            tuning = Tunings.BASS_STANDARD,
            onAddLowString = {},
            onAddHighString = {},
            onRemoveLowString = {},
            onRemoveHighString = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onCancel = {},
            onSave = {},
            existingTuningName = null,
            onSetString = {_,_->}
        )
    }
}