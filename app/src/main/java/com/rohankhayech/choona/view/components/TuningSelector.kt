/*
 * Choona - Guitar Tuner
 * Copyright (C) 2023 Rohan Khayech
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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rohankhayech.choona.R
import com.rohankhayech.choona.controller.tuner.Tuner
import com.rohankhayech.choona.model.tuning.Tunings
import com.rohankhayech.choona.view.LightDarkPreview
import com.rohankhayech.choona.view.PreviewWrapper
import com.rohankhayech.music.Tuning

/**
 * Row UI component displaying and allowing selection and retuning of the current tuning.
 *
 * @param tuning The current guitar tuning.
 * @param favTunings Set of tunings marked as favourite by the user.
 * @param customTunings Set of custom tunings added by the user.
 * @param enabled Whether the selector is enabled. Defaults to true.
 * @param onSelect Called when a tuning is selected.
 * @param onTuneDown Called when the tuning is tuned down.
 * @param onTuneUp Called when the tuning is tuned up.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 *
 * @author Rohan Khayech
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TuningSelector(
    modifier: Modifier = Modifier,
    tuning: Tuning,
    favTunings: State<Set<Tuning>>,
    customTunings: State<Set<Tuning>>,
    enabled: Boolean = true,
    onSelect: (Tuning) -> Unit,
    onTuneDown: () -> Unit,
    onTuneUp: () -> Unit,
    onOpenTuningSelector: () -> Unit,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Tune Down Button
        IconButton(
            onClick = onTuneDown,
            enabled = remember(tuning) { derivedStateOf { tuning.min().rootNoteIndex > Tuner.LOWEST_NOTE } }.value
        ) {
            Icon(Icons.Default.Remove, stringResource(R.string.tune_down))
        }

        // Tuning Display and Selection
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            modifier = Modifier.weight(1f),
            expanded = expanded && enabled,
            onExpandedChange = { expanded = !expanded }
        ) {

            // Current Tuning
            CurrentTuningField(
                tuning = tuning,
                customTunings = customTunings,
                expanded = expanded,
                showExpanded = enabled
            )

            // Dropdown Menu
            ExposedDropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { expanded = false }
            ) {
                for (tuningOption in favTunings.value) {
                    DropdownMenuItem(
                        onClick = {
                            onSelect(tuningOption)
                            expanded = false
                        }
                    ) {
                        TuningItem(modifier = Modifier.padding(vertical = 8.dp), tuning = tuningOption, fontWeight = FontWeight.Normal, customTunings = customTunings)
                    }
                }
                DropdownMenuItem(onClick = {
                    onOpenTuningSelector()
                    expanded = false
                }) {
                    Text(stringResource(R.string.open_tuning_selector))
                }
            }
        }

        // Tune Up Button
        IconButton(
            onClick = onTuneUp,
            enabled = remember(tuning) { derivedStateOf { tuning.max().rootNoteIndex < Tuner.HIGHEST_NOTE } }.value
        ) {
            Icon(Icons.Default.Add, stringResource(R.string.tune_up))
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
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CurrentTuningField(
    tuning: Tuning,
    customTunings: State<Set<Tuning>>,
    expanded: Boolean,
    showExpanded: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = TextFieldDefaults.OutlinedTextFieldShape,
        color = MaterialTheme.colors.background,
        border = BorderStroke(
            width = if (expanded && showExpanded) TextFieldDefaults.FocusedBorderThickness
            else TextFieldDefaults.UnfocusedBorderThickness,
            color = if (expanded && showExpanded) MaterialTheme.colors.primary
            else MaterialTheme.colors.onBackground.copy(alpha = TextFieldDefaults.UnfocusedIndicatorLineOpacity)
        ),
    ) {
        Row(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TuningItem(modifier = Modifier.weight(1f), tuning = tuning, customTunings = customTunings, fontWeight = FontWeight.Bold)
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
 *
 * @author Rohan Khayech
 */
@Composable
fun TuningItem(
    modifier: Modifier = Modifier,
    tuning: Tuning,
    fontWeight: FontWeight,
    customTunings: State<Set<Tuning>>,
) {
    val tuningName = remember(tuning, customTunings) {
        if (tuning.hasName()) {
            tuning.name
        } else {
            tuning.findEquivalentIn(customTunings.value + Tunings.COMMON)?.name
                ?: tuning.toString()
        }
    }

    val strings = remember(tuning) {
        tuning.strings
            .reversed()
            .joinToString(
                separator = ", ",
            ) { it.toFullString() }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            tuningName,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = fontWeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            strings,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Previews
@LightDarkPreview
@Composable
private fun Preview() {
    PreviewWrapper {
        TuningSelector(
            Modifier.padding(8.dp),
            tuning = Tuning.STANDARD,
            favTunings = remember { mutableStateOf(setOf(Tuning.STANDARD, Tuning.DROP_D)) },
            customTunings = remember { mutableStateOf(emptySet()) },
            onSelect = {},
            onTuneDown = {},
            onTuneUp = {},
            onOpenTuningSelector = {}
        )
    }
}

// Previews
@LightDarkPreview
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