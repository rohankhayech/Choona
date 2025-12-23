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

package com.rohankhayech.choona.wear.view.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import com.rohankhayech.choona.lib.R
import com.rohankhayech.choona.lib.controller.tuner.Tuner
import com.rohankhayech.choona.wear.view.theme.PreviewWrapper
import com.rohankhayech.music.GuitarString
import com.rohankhayech.music.Tuning

/**
 * Component displaying each string in the current [tuning] and allowing selection of a string for tuning.
 * @param tuning Current guitar tuning used for comparison.
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 * @param onTuneDown Called when a string is tuned down.
 * @param onTuneUp Called when a string is tuned up.
 * @param editModeEnabled Whether edit mode is enabled.
 *
 * @author Rohan Khayech
 */
@Composable
fun StringControls(
    modifier: Modifier = Modifier,
    tuning: Tuning,
    selectedString: Int?,
    tuned: BooleanArray?,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit,
) {
    val strings: List<Pair<Int, GuitarString>> = remember(tuning) { tuning.mapIndexed { n, gs -> Pair(n, gs) } }
    Column(
        modifier.horizontalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        strings.forEach {
            val (index, string) = it
            StringControl(
                index = index,
                string = string,
                selected = selectedString == index,
                tuned = tuned?.get(index) ?: false,
                onSelect = onSelect,
                onTuneDown = onTuneDown,
                onTuneUp = onTuneUp,
            )
        }
    }
}

/**
 * Component displaying the specified strings inline horizontally and allowing selection of a string for tuning.
 * @param tuning Current guitar tuning used for comparison.
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 *
 * @author Rohan Khayech
 */
@Composable
fun CompactStringSelector(
    modifier: Modifier = Modifier,
    tuning: Tuning,
    selectedString: Int,
    tuned: BooleanArray,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp),
    onSelect: (Int) -> Unit,
) {
    ScrollableButtonRow(
        modifier = modifier,
        contentPadding = contentPadding,
        items = remember(tuning) { tuning.mapIndexed { n, gs -> Pair(n, gs.toFullString()) } },
        selectedIndex = selectedString,
        activatedButtons = tuned,
        reversed = true,
        onSelect = onSelect
    )
}

/**
 * Row of buttons allowing selection and retuning of the specified string.
 *
 * @param index Index of the string within the tuning.
 * @param string The guitar string.
 * @param selected Whether the string is currently selected for tuning.
 * @param onSelect Called when the string is selected.
 * @param onTuneDown Called when the string is tuned down.
 * @param onTuneUp Called when the string is tuned up.
 * @param editModeEnabled Whether edit mode is enabled.
 */
@Composable
private fun StringControl(
    index: Int,
    string: GuitarString,
    selected: Boolean,
    tuned: Boolean,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
            // Tune Down Button
            IconButton(
                modifier = Modifier.height(32.dp),
                onClick = remember(onTuneDown, index) { { onTuneDown(index) } },
                enabled = remember(string) { derivedStateOf { string.rootNoteIndex > Tuner.LOWEST_NOTE } }.value
            ) {
                Icon(Icons.Default.Remove, stringResource(R.string.tune_down))
            }

        StringSelectionButton(index, string, tuned, selected, onSelect)

            // Tune Up Button
            IconButton(
                modifier = Modifier.height(32.dp),
                onClick = remember(onTuneUp, index) { { onTuneUp(index) } },
                enabled = remember(string) { derivedStateOf { string.rootNoteIndex < Tuner.HIGHEST_NOTE } }.value
            ) {
                Icon(Icons.Default.Add, stringResource(R.string.tune_up))
            }
    }
}

/**
 * Buttons displaying and allowing user selection of the specified string.
 *
 * @param index Index of the string within the tuning.
 * @param string The guitar string.
 * @param tuned Whether the string is tuned.
 * @param selected Whether the string is currently selected for tuning.
 * @param onSelect Called when the string is selected.
 */
@Composable
private fun StringSelectionButton(
    index: Int,
    string: GuitarString,
    tuned: Boolean,
    selected: Boolean,
    onSelect: (Int) -> Unit,
) {
    NoteSelectionButton(
        index = index,
        label = string.toFullString(),
        tuned = tuned,
        selected = selected,
        onSelect = onSelect
    )
}

// Previews

@Preview
@Composable
fun InlinePreview() {
    PreviewWrapper {
        StringControls(
            tuning = Tuning.STANDARD.withString(4, GuitarString.fromRootNote("D#3")),
            selectedString = 1,
            tuned = BooleanArray(6) { it == 4 },
            onSelect = {},
            onTuneDown = {},
            onTuneUp = {},
        )
    }
}

@Preview
@Composable
private fun CompactPreview() {
    PreviewWrapper {
        CompactStringSelector(
            tuning = Tuning.STANDARD,
            selectedString = 5,
            tuned = BooleanArray(6) { it == 4 },
            onSelect = {},
        )
    }
}

@Preview
@Composable
private fun StringControlPreview() {
    PreviewWrapper {
        StringControl(index = 0, string = GuitarString.E2, selected = false, tuned = false, onSelect = {}, onTuneDown = {}, onTuneUp = {})
    }
}

@Preview
@Composable
private fun ButtonStatesPreview() {
    PreviewWrapper {
        Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StringSelectionButton(tuned = false, selected = false, onSelect = {}, index = 0, string = GuitarString.E2)
            StringSelectionButton(tuned = false, selected = true, onSelect = {}, index = 0, string = GuitarString.E2)
            StringSelectionButton(tuned = true, selected = false, onSelect = {}, index = 0, string = GuitarString.E2)
            StringSelectionButton(tuned = true, selected = true, onSelect = {}, index = 0, string = GuitarString.E2)
        }
    }
}