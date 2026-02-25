/*
 * ChromaticTuner - Arma Rizki
 *
 * Includes modified source code from Choona Guitar Tuner
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

package com.armarizki.chromatic.view.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rohankhayech.android.util.ui.preview.ThemePreview
import com.armarizki.chromatic.R
import com.armarizki.chromatic.controller.tuner.Tuner
import com.armarizki.chromatic.model.tuning.TuningEntry
import com.armarizki.chromatic.view.theme.PreviewWrapper
import com.armarizki.music.Tuning

 
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TuningSelector(
    modifier: Modifier = Modifier,
    tuning: TuningEntry,
    favTunings: State<Set<TuningEntry>>,
    getCanonicalName: TuningEntry.InstrumentTuning.() -> String,
    enabled: Boolean = true,
    openDirect: Boolean,
    compact: Boolean,
    onSelect: (TuningEntry) -> Unit,
    onTuneDown: () -> Unit,
    onTuneUp: () -> Unit,
    onOpenTuningSelector: () -> Unit,
    editModeEnabled: Boolean
) {
    LookaheadScope {
        Row(
            modifier = modifier
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (editModeEnabled && tuning is TuningEntry.InstrumentTuning) {
                // Tune Down Button
                IconButton(
                    onClick = onTuneDown,
                    enabled = remember(tuning) { derivedStateOf { tuning.tuning.min().rootNoteIndex > Tuner.LOWEST_NOTE } }.value
                ) {
                    Icon(Icons.Default.Remove, stringResource(R.string.tune_down))
                }
            }

            // Tuning Display and Selection
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = if (!editModeEnabled || tuning is TuningEntry.InstrumentTuning) 16.dp else 0.dp),
                expanded = expanded && enabled,
                onExpandedChange = {
                    if (openDirect) onOpenTuningSelector()
                    else expanded = it
                }
            ) {
                // Current Tuning
                CurrentTuningField(
                    modifier = Modifier.animateBounds(lookaheadScope = this@LookaheadScope).menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled),
                    tuning = tuning,
                    getCanonicalName,
                    expanded = expanded,
                    showExpanded = enabled,
                    compact
                )

                // Dropdown Menu
                ExposedDropdownMenu(
                    expanded = expanded && enabled,
                    onDismissRequest = { expanded = false }
                ) {
                    for (tuningOption in favTunings.value) {
                        DropdownMenuItem(
                            text = {
                                TuningItem(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    tuning = tuningOption,
                                    fontWeight = FontWeight.Normal,
                                    getCanonicalName = getCanonicalName
                                )
                            },
                            onClick = {
                                onSelect(tuningOption)
                                expanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.open_tuning_selector))
                        },
                        onClick = {
                            onOpenTuningSelector()
                            expanded = false
                        })
                }
            }

            if (editModeEnabled && tuning is TuningEntry.InstrumentTuning) {
                // Tune Up Button
                IconButton(
                    onClick = onTuneUp,
                    enabled = remember(tuning) { derivedStateOf { tuning.tuning.max().rootNoteIndex < Tuner.HIGHEST_NOTE } }.value
                ) {
                    Icon(Icons.Default.Add, stringResource(R.string.tune_up))
                }
            }
        }
    }
}

 
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrentTuningField(
    modifier: Modifier = Modifier,
    tuning: TuningEntry,
    getCanonicalName: TuningEntry.InstrumentTuning.() -> String,
    expanded: Boolean,
    showExpanded: Boolean,
    compact: Boolean
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = OutlinedTextFieldDefaults.shape,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(
            width = if (expanded && showExpanded) OutlinedTextFieldDefaults.FocusedBorderThickness
            else OutlinedTextFieldDefaults.UnfocusedBorderThickness,
            color = if (expanded && showExpanded) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant
        ),
    ) {
        Row(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TuningItem(
                modifier = Modifier.weight(1f),
                compact = compact,
                tuning = tuning,
                getCanonicalName = getCanonicalName,
                fontWeight = FontWeight.Bold
            )
            if (showExpanded) ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }
    }
}

 
@Composable
fun TuningItem(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    tuning: TuningEntry,
    fontWeight: FontWeight,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    getCanonicalName: TuningEntry.InstrumentTuning.() -> String,
) {
    val tuningName = when (tuning) {
        is TuningEntry.ChromaticTuning -> stringResource(R.string.chromatic)
        is TuningEntry.InstrumentTuning ->
            if (tuning.tuning.hasName()) {
                tuning.tuning.name
            } else {
                tuning.getCanonicalName()
            }
    }

    val strings = remember(tuning) {
        tuning.tuning?.strings
            ?.reversed()
            ?.joinToString(
                separator = if (!compact) ", " else "",
            ) { if (compact) it.toString() else it.toFullString() }
    } ?: ""

    val desc = when (tuning) {
        is TuningEntry.ChromaticTuning -> stringResource(R.string.chromatic_desc)
        is TuningEntry.InstrumentTuning -> strings
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            tuningName,
            style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
            fontWeight = fontWeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            desc,
            style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Previews
@ThemePreview
@Composable
private fun Preview() {
    PreviewWrapper {
        TuningSelector(
            Modifier.padding(8.dp),
            tuning = TuningEntry.InstrumentTuning(Tuning.STANDARD),
            favTunings = remember { mutableStateOf(setOf(TuningEntry.InstrumentTuning(Tuning.STANDARD), TuningEntry.InstrumentTuning(Tuning.DROP_D))) },
            openDirect = false,
            onSelect = {},
            onTuneDown = {},
            onTuneUp = {},
            onOpenTuningSelector = {},
            editModeEnabled = true,
            compact = false,
            getCanonicalName = { this.tuning.toString() }
        )
    }
}

@ThemePreview
@Composable
private fun EditOffPreview() {
    PreviewWrapper {
        TuningSelector(
            Modifier.padding(8.dp),
            tuning = TuningEntry.InstrumentTuning(Tuning.STANDARD),
            favTunings = remember { mutableStateOf(setOf(TuningEntry.InstrumentTuning(Tuning.STANDARD), TuningEntry.InstrumentTuning(Tuning.DROP_D))) },
            getCanonicalName = { this.tuning.toString() },
            openDirect = false,
            onSelect = {},
            onTuneDown = {},
            onTuneUp = {},
            onOpenTuningSelector = {},
            editModeEnabled = false,
            compact = false
        )
    }
}

// Previews
@ThemePreview
@Composable
private fun TuningItemPreview() {
    PreviewWrapper {
        TuningItem(
            Modifier.padding(8.dp),
            tuning = TuningEntry.InstrumentTuning(Tuning.STANDARD),
            getCanonicalName = { this.tuning.toString() },
            fontWeight = FontWeight.Bold
        )
    }
}