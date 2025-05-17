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

package com.rohankhayech.choona.view.components

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
import com.rohankhayech.choona.R
import com.rohankhayech.choona.controller.tuner.Tuner
import com.rohankhayech.choona.model.tuning.Tunings
import com.rohankhayech.choona.view.theme.PreviewWrapper
import com.rohankhayech.music.Tuning

/**
 * Row UI component displaying and allowing selection and retuning of the current tuning.
 *
 * @param tuning The current guitar tuning.
 * @param favTunings Set of tunings marked as favourite by the user.
 * @param customTunings Set of custom tunings added by the user.
 * @param enabled Whether the selector is enabled. Defaults to true.
 * @param openDirect Whether to open the tuning selection screen directly instead of the favourites dropdown.
 * @param onSelect Called when a tuning is selected.
 * @param onTuneDown Called when the tuning is tuned down.
 * @param onTuneUp Called when the tuning is tuned up.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 * @param editModeEnabled
 *
 * @author Rohan Khayech
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TuningSelector(
    modifier: Modifier = Modifier,
    tuning: Tuning,
    favTunings: State<Set<Tuning>>,
    customTunings: State<Set<Tuning>>,
    enabled: Boolean = true,
    openDirect: Boolean,
    compact: Boolean,
    onSelect: (Tuning) -> Unit,
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
            if (editModeEnabled) {
                // Tune Down Button
                IconButton(
                    onClick = onTuneDown,
                    enabled = remember(tuning) { derivedStateOf { tuning.min().rootNoteIndex > Tuner.LOWEST_NOTE } }.value
                ) {
                    Icon(Icons.Default.Remove, stringResource(R.string.tune_down))
                }
            }

            // Tuning Display and Selection
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = if (!editModeEnabled) 16.dp else 0.dp),
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
                    customTunings = customTunings,
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
                                    customTunings = customTunings
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

            if (editModeEnabled) {
                // Tune Up Button
                IconButton(
                    onClick = onTuneUp,
                    enabled = remember(tuning) { derivedStateOf { tuning.max().rootNoteIndex < Tuner.HIGHEST_NOTE } }.value
                ) {
                    Icon(Icons.Default.Add, stringResource(R.string.tune_up))
                }
            }
        }
    }
}

/**
 * Outlined dropdown box field showing the current tuning.
 *
 * @param tuning The current guitar tuning.
 * @param customTunings Set of custom tunings added by the user.
 * @param expanded Whether the dropdown box is expanded.
 * @param showExpanded Whether to show the expanded state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrentTuningField(
    modifier: Modifier = Modifier,
    tuning: Tuning,
    customTunings: State<Set<Tuning>>,
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
            TuningItem(modifier = Modifier.weight(1f), compact = compact, tuning = tuning, customTunings = customTunings, fontWeight = FontWeight.Bold)
            if (showExpanded) ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }
    }
}

/**
 * UI component displaying the name and strings of the specified tuning.
 *
 * @param modifier The modifier to apply to this layout.
 * @param tuning The tuning to display.
 * @param fontWeight The font weight of the tuning name text.
 * @param customTunings Set of custom tunings added by the user.
 * @param horizontalAlignment The horizontal alignment of the text.
 * @param compact Whether to show the compact version of the tuning.
 *
 * @author Rohan Khayech
 */
@Composable
fun TuningItem(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    tuning: Tuning,
    fontWeight: FontWeight,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    customTunings: State<Set<Tuning>>,
) {
    val tuningName = remember(tuning, customTunings) {
        if (tuning.hasName()) {
            tuning.name
        } else {
            tuning.findEquivalentIn(customTunings.value + Tunings.TUNINGS)?.name
                ?: tuning.toString()
        }
    }

    val strings = remember(tuning) {
        tuning.strings
            .reversed()
            .joinToString(
                separator = ", ",
            ) { it.toFullString() }
            ) { if (compact) it.toString() else it.toFullString() }
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
            strings,
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
            tuning = Tuning.STANDARD,
            favTunings = remember { mutableStateOf(setOf(Tuning.STANDARD, Tuning.DROP_D)) },
            customTunings = remember { mutableStateOf(emptySet()) },
            openDirect = false,
            onSelect = {},
            onTuneDown = {},
            onTuneUp = {},
            onOpenTuningSelector = {},
            editModeEnabled = true,
            compact = false
        )
    }
}

@ThemePreview
@Composable
private fun EditOffPreview() {
    PreviewWrapper {
        TuningSelector(
            Modifier.padding(8.dp),
            tuning = Tuning.STANDARD,
            favTunings = remember { mutableStateOf(setOf(Tuning.STANDARD, Tuning.DROP_D)) },
            customTunings = remember { mutableStateOf(emptySet()) },
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
            tuning = Tuning.STANDARD,
            customTunings = remember { mutableStateOf(emptySet()) },
            fontWeight = FontWeight.Bold
        )
    }
}