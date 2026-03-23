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

package com.rohankhayech.choona.app.view.screens

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
import androidx.compose.material3.ExposedDropdownMenuAnchorType
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rohankhayech.android.util.ui.preview.DarkPreview
import com.rohankhayech.android.util.ui.preview.ThemePreview
import com.rohankhayech.choona.app.view.components.InlineStringControls
import com.rohankhayech.choona.app.view.components.NoteSelector
import com.rohankhayech.choona.app.view.theme.AppTheme
import com.rohankhayech.choona.app.view.theme.PreviewWrapper
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.controller.tuner.Tuner
import com.rohankhayech.choona.lib.controller.tunings.MAX_STRINGS
import com.rohankhayech.choona.lib.model.error.ExistingTuningException
import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.model.tuning.Tunings
import com.rohankhayech.choona.lib.view.util.getLocalisedName
import kotlinx.coroutines.launch

/**
 * UI screen used to tune individual strings and the tuning
 * itself up and down, as well as select from favourite tunings.
 *
 * @param name The current name of the tuning.
 * @param new Whether the tuning is a new custom tuning.
 * @param tuning The guitar tuning being edited.
 * @param onNameChange Called when the name is changed.
 * @param onInstrumentChange Called when the instrument is changed.
 * @param onSetString Called when a string is set.
 * @param onAddLowString Called when a low string is added.
 * @param onAddHighString Called when a high string is added.
 * @param onRemoveLowString Called when a low string is removed.
 * @param onRemoveHighString Called when a high string is removed.
 * @param onTuneStringUp Called when a string is tuned up.
 * @param onTuneStringDown Called when a string is tuned down.
 * @param onTuneUp Called when the tuning is tuned up.
 * @param onTuneDown Called when the tuning is tuned down.
 * @param onPressNote Called when a note selection button is pressed.
 * @param onCancel Called when the user cancels the edit.
 * @param onSave Called when the user saves the tuning.
 * @param onDelete Called when the user deletes the tuning.
 *
 * @author Rohan Khayech
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTuningScreen(
    name: String,
    new: Boolean,
    tuning: Tuning,
    onNameChange: (String) -> Unit,
    onInstrumentChange: (Instrument) -> Unit,
    onSetString: (n: Int, noteIndex: Int) -> Unit,
    onAddLowString: () -> Unit,
    onAddHighString: () -> Unit,
    onRemoveLowString: () -> Unit,
    onRemoveHighString: () -> Unit,
    onTuneStringUp: (Int) -> Unit,
    onTuneStringDown: (Int) -> Unit,
    onTuneUp: () -> Unit,
    onTuneDown: () -> Unit,
    onPressNote: (Int, Instrument) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val snackbarHost = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold (
        Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(if (new) "Add custom tuning" else "Edit Tuning")
                },
                actions = {
                    // Save button.
                    Button(modifier = Modifier.padding(horizontal = 16.dp), onClick = {
                        try {
                            onSave()
                        } catch (e: ExistingTuningException) {
                            coroutineScope.launch {
                                snackbarHost.showSnackbar(
                                    message = "A ${
                                        when (e.builtIn) {
                                            true -> "built-in"
                                            else -> "custom"
                                        }
                                    } tuning already exists as ${e.existingName}.",
                                )
                            }
                        }
                    }) {
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
            EditTuningForm(
                new = new,
                name = name,
                instrument = tuning.instrument,
                tuning = tuning,
                onAddLowString = onAddLowString,
                onAddHighString = onAddHighString,
                onRemoveLowString = onRemoveLowString,
                onRemoveHighString = onRemoveHighString,
                onTuneUpString = onTuneStringUp,
                onTuneDownString = onTuneStringDown,
                onTuneUpTuning = onTuneUp,
                onTuneDownTuning = onTuneDown,
                onNameChange = onNameChange,
                onInstrumentChange = onInstrumentChange,
                onSetString = onSetString,
                onPressNote = { n ->
                    onPressNote(n, tuning.instrument)
                },
                onDelete = onDelete
            )
        }
    }
}

/**
 * Form for editing a tuning.
 *
 * @param new Whether the tuning is a new custom tuning.
 * @param name The current name of the tuning.
 * @param instrument The current instrument of the tuning.
 * @param tuning The guitar tuning being edited.
 * @param onAddLowString Called when a low string is added.
 * @param onAddHighString Called when a high string is added.
 * @param onRemoveLowString Called when a low string is removed.
 * @param onRemoveHighString Called when a high string is removed.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 * @param onNameChange Called when the name is changed.
 * @param onInstrumentChange Called when the instrument is changed.
 * @param onSetString Called when a string is set.
 * @param onPressNote Called when a note selection button is pressed.
 * @param onDelete Called when the user deletes the tuning.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTuningForm(
    new: Boolean,
    name: String,
    instrument: Instrument,
    tuning: Tuning,
    onAddLowString: () -> Unit,
    onAddHighString: () -> Unit,
    onRemoveLowString: () -> Unit,
    onRemoveHighString: () -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onNameChange: (String) -> Unit,
    onInstrumentChange: (Instrument) -> Unit,
    onSetString: (Int, Int) -> Unit,
    onPressNote: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var stringToEdit by remember { mutableStateOf<Int?>(null) }

    if (stringToEdit != null) {
        val index = stringToEdit!!
        NoteSelectionDialog(
            initialNoteIndex = tuning.getString(index).rootNoteIndex,
            onConfirm = { noteIndex ->
                onSetString(index, noteIndex)
                stringToEdit = null
            },
            onPressNote = onPressNote,
            onDismiss = { stringToEdit = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            label = { Text("Name") },
            placeholder = { Text(tuning.toString()) },
            onValueChange = onNameChange
        )

        var instrExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = instrExpanded, onExpandedChange = { instrExpanded = it }) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
                leadingIcon = { Icon(painterResource(R.drawable.guitar_electric), null) },
                value = instrument.getLocalisedName(),
                label = { Text("Instrument") },
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = instrExpanded
                    )
                }
            )
            ExposedDropdownMenu(
                expanded = instrExpanded,
                onDismissRequest = { instrExpanded = false }
            ) {
                Instrument.entries.take(Instrument.entries.size - 1).forEach { instr ->
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
                    addEnabled = tuning.numStrings() < MAX_STRINGS,
                    removeEnabled = tuning.numStrings() > 1,
                    onAddString = onAddHighString,
                    onRemoveString = onRemoveHighString
                )
                InlineStringControls(
                    tuning = tuning,
                    selectedString = null,
                    tuned = null,
                    onSelect = {
                        stringToEdit = it
                        onPressNote(tuning.getString(it).rootNoteIndex)
                    },
                    onTuneDown = onTuneDownString,
                    onTuneUp = onTuneUpString,
                    editModeEnabled = true
                )
                AddRemoveRow(
                    addEnabled = tuning.numStrings() < MAX_STRINGS,
                    removeEnabled = tuning.numStrings() > 1,
                    onAddString = onAddLowString,
                    onRemoveString = onRemoveLowString
                )
                Row {
                        TextButton(
                            onClick = onTuneDownTuning,
                            enabled = remember(tuning) { derivedStateOf { tuning.min().rootNoteIndex > Tuner.LOWEST_NOTE } }.value,
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
                            enabled = remember(tuning) { derivedStateOf { tuning.max().rootNoteIndex < Tuner.HIGHEST_NOTE } }.value,
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
        // Delete button.
        if (!new) {
            TextButton(
                onClick = onDelete,
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

/**
 * Row with add and remove buttons for strings.
 *
 * @param addEnabled Whether the add button is enabled.
 * @param removeEnabled Whether the remove button is enabled.
 * @param onAddString Called when the add button is pressed.
 * @param onRemoveString Called when the remove button is pressed.
 */
@Composable
private fun AddRemoveRow(
    addEnabled: Boolean,
    removeEnabled: Boolean,
    onAddString: () -> Unit,
    onRemoveString: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilledTonalIconButton(
            modifier = Modifier.size(32.dp),
            enabled = removeEnabled,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
            onClick = onRemoveString,
        ) {
            Icon(Icons.Default.Delete, "Remove String", modifier = Modifier.size(20.dp))
        }
        FilledTonalIconButton(
            modifier = Modifier.size(32.dp),
            enabled = addEnabled,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            onClick = { onAddString() }
        ) {
            Icon(Icons.Default.Add, "Add String", modifier = Modifier.size(20.dp))
        }

    }
}

/**
 * Dialog allowing the user to select a note and octave.
 *
 * @param initialNoteIndex Initial note index to display.
 * @param onConfirm Called when the OK button is pressed. Provides the selected note index.
 * @param onPressNote Called when a note selection button is pressed.
 * @param onDismiss Called when the dialog is dismissed or the Cancel button is pressed.
 *
 * @author Rohan Khayech
 */
@Composable
fun NoteSelectionDialog(
    initialNoteIndex: Int,
    onConfirm: (Int) -> Unit,
    onPressNote: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedNoteIndex by remember { mutableIntStateOf(initialNoteIndex) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_title_select_note)) },
        text = {
            NoteSelector(
                selectedNoteIndex = selectedNoteIndex,
                tuned = false,
                onSelect = {
                    selectedNoteIndex = it
                    onPressNote(it)
                }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedNoteIndex) }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}


@ThemePreview
@Composable
private fun Preview() {
    AppTheme {
        EditTuningScreen(
            name = Tunings.BASS_STANDARD.name,
            new = false,
            tuning = Tunings.BASS_STANDARD,
            onAddLowString = {},
            onAddHighString = {},
            onRemoveLowString = {},
            onRemoveHighString = {},
            onTuneStringUp = {},
            onTuneStringDown = {},
            onTuneUp = {},
            onTuneDown = {},
            onPressNote = {_, _ -> },
            onCancel = {},
            onSave = { },
            onSetString = {_,_->},
            onNameChange = {},
            onInstrumentChange = {},
            onDelete = {}
        )
    }
}

@DarkPreview
@Composable
private fun TrueDarkPreview() {
    AppTheme(fullBlack = true) {
        EditTuningScreen(
            name = Tunings.BASS_STANDARD.name,
            new = true,
            tuning = Tunings.BASS_STANDARD,
            onAddLowString = {},
            onAddHighString = {},
            onRemoveLowString = {},
            onRemoveHighString = {},
            onTuneStringUp = {},
            onTuneStringDown = {},
            onTuneUp = {},
            onTuneDown = {},
            onPressNote = {_, _ ->},
            onCancel = {},
            onSave = {},
            onSetString = {_,_->},
            onNameChange = {},
            onInstrumentChange = {},
            onDelete = {}
        )
    }
}

@ThemePreview
@Composable
private fun DialogPreview() {
    PreviewWrapper {
        NoteSelectionDialog(initialNoteIndex = -29, onConfirm = {}, onPressNote = {}, onDismiss = {})
    }
}