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

package com.rohankhayech.choona.wear.view.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.AlertDialog
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import com.rohankhayech.android.util.ui.preview.wear.WearSizePreview
import com.rohankhayech.android.util.ui.wear.input.wearTextInput
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.controller.tuner.Tuner
import com.rohankhayech.choona.lib.controller.tunings.MAX_STRINGS
import com.rohankhayech.choona.lib.controller.tunings.MIN_STRINGS
import com.rohankhayech.choona.lib.model.error.ExistingTuningException
import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.model.tuning.Tunings
import com.rohankhayech.choona.lib.view.util.getLocalisedName
import com.rohankhayech.choona.wear.view.components.NoteSelector
import com.rohankhayech.choona.wear.view.components.SectionLabel
import com.rohankhayech.choona.wear.view.components.StringControls
import com.rohankhayech.choona.wear.view.theme.AppTheme
import com.rohankhayech.choona.wear.R as WearR

/**
 * UI screen used to edit or create a custom tuning.
 *
 * @param name The current name of the tuning.
 * @param new Whether the tuning is a new custom tuning.
 * @param tuning The guitar tuning being edited.
 * @param hasChanges Whether the tuning has been modified.
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
@Composable
fun EditTuningScreen(
    name: String,
    new: Boolean,
    tuning: Tuning,
    hasChanges: Boolean = false,
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
    onDelete: () -> Unit,
) {
    val listState = rememberScalingLazyListState()
    var stringToEdit by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showInstrumentDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Handle discard confirmation on back.
    val handleCancel = {
        if (hasChanges) {
            showDiscardDialog = true
        } else {
            onCancel()
        }
    }
    BackHandler(enabled = true, onBack = handleCancel)
    
    // Note selection dialog.
    stringToEdit?.let { str ->
        NoteSelectionDialog(
            initialNoteIndex = tuning.getString(str).rootNoteIndex,
            onConfirm = { noteIndex ->
                onSetString(str, noteIndex)
                stringToEdit = null
            },
            onPressNote = { onPressNote(it, tuning.instrument) },
            onDismiss = { stringToEdit = null }
        )
    }

    // Delete dialog.
    AlertDialog(
        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ), onClick = {
                showDeleteDialog = false
                onDelete() }
            ) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = { showDeleteDialog = false }) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        onDismissRequest = { showDeleteDialog = false },
        visible = showDeleteDialog,
        title = { Text(
            "${stringResource(R.string.delete)} ${name.ifBlank { tuning.toString() }}?"
        )},
        icon = {
            Icon(
                Icons.Default.Delete,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = null
            )
        }
    )

    // Discard dialog
    AlertDialog(
        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ), onClick = {
                showDiscardDialog = false
                onCancel() }
            ) {
                Text(text = stringResource(R.string.discard))
            }
        },
        dismissButton = {
            TextButton(onClick = { showDiscardDialog = false }) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        onDismissRequest = { showDiscardDialog = false },
        visible = showDiscardDialog,
        title = { Text(stringResource(R.string.discard_changes)) },
        icon = {
            Icon(
                Icons.Default.Close,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = null
            )
        }
    ) {
        item {
            Text(
                stringResource(R.string.discard_changes_confirmation),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    // Instrument dialog
    AlertDialog(
        onDismissRequest = { showInstrumentDialog = false },
        title = { Text(stringResource(R.string.instrument)) },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = { showInstrumentDialog = false }) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        visible = showInstrumentDialog
    ) {
        items(Instrument.entries.filter { it != Instrument.OTHER }) { instr ->
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onInstrumentChange(instr)
                    showInstrumentDialog = false
                }
            ) {
                Text(instr.getLocalisedName(), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
    }

    // Error dialog
    AlertDialog(
        onDismissRequest = { errorMessage = null },
        title = { Text(stringResource(WearR.string.err_msg_existing_tuning_title)) },
        dismissButton = {},
        confirmButton = {
            Button(onClick = { errorMessage = null }) {
                Icon(Icons.Default.Close, stringResource(R.string.dismiss))
            }
        },
        visible = errorMessage != null
    ) {
        item {
            Text(
                errorMessage?: "",
                textAlign = TextAlign.Center
            )
        }
    }

    val errMsg = stringResource(R.string.err_msg_existing_tuning)
    val builtInStr = stringResource(R.string.err_msg_existing_tuning_built_in)
    val customStr = stringResource(R.string.err_msg_existing_tuning_custom)

    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(onClick = {
                try {
                    onSave()
                } catch (e: ExistingTuningException) {
                    errorMessage = errMsg.format(
                        when (e.builtIn) {
                            true -> builtInStr
                            false -> customStr
                        }, e.existingName
                    )
                }
            }) {
                Text(stringResource(R.string.save))
            }
        }
    ) { padding ->
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = padding,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ListHeader {
                    Text(
                        stringResource(if (new) R.string.add_tuning else R.string.edit_tuning),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Name edit
            item {
                Button(
                    onClick = wearTextInput(
                        stringResource(R.string.name),
                        "tuning-name",
                        onInput = onNameChange
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            stringResource(R.string.name),
                             style = MaterialTheme.typography.bodySmall,
                             maxLines = 1,
                             overflow = TextOverflow.Ellipsis,
                             textAlign = TextAlign.Center
                        )
                        Text(
                            name.ifBlank { tuning.toString() },
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Instrument edit
            item {
                Button(
                    onClick = { showInstrumentDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(R.drawable.guitar_electric), null, modifier = Modifier.padding(end = 4.dp))
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                stringResource(R.string.instrument),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                tuning.instrument.getLocalisedName(),
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            item {
                SectionLabel(stringResource(R.string.strings))
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onTuneDown,
                        enabled = remember(tuning) { derivedStateOf { tuning.min().rootNoteIndex > Tuner.LOWEST_NOTE } }.value
                    ) {
                        Icon(Icons.Default.Remove, stringResource(R.string.tune_down))
                    }
                    Text(
                        stringResource(WearR.string.tune),
                        style = MaterialTheme.typography.labelMedium
                    )
                    IconButton(
                        onClick = onTuneUp,
                        enabled = remember(tuning) { derivedStateOf { tuning.max().rootNoteIndex < Tuner.HIGHEST_NOTE } }.value
                    ) {
                        Icon(Icons.Default.Add, stringResource(R.string.tune_up))
                    }
                }
            }

            item {
                AddRemoveRow(
                    addEnabled = tuning.numStrings() < MAX_STRINGS,
                    removeEnabled = tuning.numStrings() > MIN_STRINGS,
                    onAddString = onAddHighString,
                    onRemoveString = onRemoveHighString
                )
            }

            item {
                StringControls(
                    tuning = tuning,
                    selectedString = null,
                    tuned = null,
                    onSelect = {
                        stringToEdit = it
                        onPressNote(tuning.getString(it).rootNoteIndex, tuning.instrument)
                    },
                    onTuneDown = onTuneStringDown,
                    onTuneUp = onTuneStringUp
                )
            }

            item {
                AddRemoveRow(
                    addEnabled = tuning.numStrings() < MAX_STRINGS,
                    removeEnabled = tuning.numStrings() > MIN_STRINGS,
                    onAddString = onAddLowString,
                    onRemoveString = onRemoveLowString
                )
            }

            item {
                TextButton(
                    onClick = handleCancel
                ) {
                    Text(stringResource(android.R.string.cancel))
                }
            }

            if (!new) {
                item {
                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Delete, null)
                            Text(stringResource(R.string.delete), modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            modifier = Modifier.size(32.dp),
            enabled = removeEnabled,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
            onClick = onRemoveString,
        ) {
            Icon(Icons.Default.Delete, stringResource(R.string.remove_string))
        }
        IconButton(
            modifier = Modifier.size(32.dp),
            enabled = addEnabled,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            onClick = onAddString
        ) {
            Icon(Icons.Default.Add, stringResource(R.string.add_string))
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
        confirmButton = {
            Button(onClick = { onConfirm(selectedNoteIndex) }) {
                Icon(Icons.Default.Done, stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        visible = true
    ) {
        item {
            NoteSelector(
                selectedNoteIndex = selectedNoteIndex,
                tuned = false,
                onSelect = {
                    selectedNoteIndex = it
                    onPressNote(it)
                }
            )
        }
    }
}

@WearSizePreview
@Preview
@Composable
private fun Preview() {
    AppTheme {
        AppScaffold {
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
                onPressNote = { _, _ -> },
                onCancel = {},
                onSave = {},
                onSetString = { _, _ -> },
                onNameChange = {},
                onInstrumentChange = {},
                onDelete = {}
            )
        }
    }
}
