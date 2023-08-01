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

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rohankhayech.choona.R
import com.rohankhayech.choona.controller.tuner.Tuner
import com.rohankhayech.music.GuitarString
import com.rohankhayech.music.Tuning

/**
 * Component displaying each string in the current [tuning] and allowing selection of a string for tuning.
 * @param inline Whether to display the string controls inline or side-by-side.
 * @param tuning Current guitar tuning used for comparison.
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 * @param onTuneDown Called when a string is tuned down.
 * @param onTuneUp Called when a string is tuned up.
 *
 * @author Rohan Khayech
 */
@Composable
fun StringControls(
    inline: Boolean,
    tuning: Tuning,
    selectedString: Int?,
    tuned: BooleanArray?,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit
) {
    Box(Modifier.padding(8.dp)) {
        if (inline) {
            InlineStringControls(
                tuning = tuning,
                selectedString = selectedString,
                tuned = tuned,
                onSelect = onSelect,
                onTuneDown = onTuneDown,
                onTuneUp = onTuneUp
            )
        } else {
            SideBySideStringControls(
                tuning = tuning,
                selectedString = selectedString,
                tuned = tuned,
                onSelect = onSelect,
                onTuneDown = onTuneDown,
                onTuneUp = onTuneUp
            )
        }
    }
}

/**
 * Component displaying each string in the current [tuning] side-by-side and allowing selection of a string for tuning.
 * @param tuning Current guitar tuning used for comparison.
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 * @param onTuneDown Called when a string is tuned down.
 * @param onTuneUp Called when a string is tuned up.
 */
@Composable
private fun SideBySideStringControls(
    tuning: Tuning,
    selectedString: Int?,
    tuned: BooleanArray?,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.spacedBy(8.dp)
    ) {
        val splitTuning = remember(tuning) {
            tuning.mapIndexed { n, gs -> Pair(n, gs) }
                .reversed()
                .chunked(tuning.numStrings()/2)
        }

        InlineStringControls(
            tuning = tuning,
            strings = remember(tuning) { splitTuning[0].reversed() },
            selectedString = selectedString,
            tuned = tuned,
            onSelect = onSelect,
            onTuneDown = onTuneDown,
            onTuneUp = onTuneUp
        )
        InlineStringControls(
            tuning = tuning,
            strings = splitTuning[1],
            selectedString = selectedString,
            tuned = tuned,
            onSelect = onSelect,
            onTuneDown = onTuneDown,
            onTuneUp = onTuneUp
        )
    }
}

/**
 * Component displaying the specified [strings] inline and allowing selection of a string for tuning.
 * @param tuning Current guitar tuning used for comparison.
 * @param strings Strings to display in this selector and their indexes within the tuning. Defaults to [tuning].
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 * @param onTuneDown Called when a string is tuned down.
 * @param onTuneUp Called when a string is tuned up.
 */
@Composable
private fun InlineStringControls(
    tuning: Tuning,
    strings: List<Pair<Int, GuitarString>> = remember(tuning) { tuning.mapIndexed { n, gs -> Pair(n, gs) } },
    selectedString: Int?,
    tuned: BooleanArray?,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit
) {
    Column(
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
 * Component displaying the specified [strings] inline horizontally and allowing selection of a string for tuning.
 * @param tuning Current guitar tuning used for comparison.
 * @param strings Strings to display in this selector and their indexes within the tuning. Defaults to [tuning].
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
    strings: List<Pair<Int, GuitarString>> = remember(tuning) { tuning.mapIndexed { n, gs -> Pair(n, gs) }.reversed() },
    selectedString: Int,
    tuned: BooleanArray,
    onSelect: (Int) -> Unit,
) {
    val scrollState = rememberScrollState()

    val selectedStringButtonPosition = with(LocalDensity.current) {
        remember(tuning, selectedString) {
            (72.dp * (strings.size -1 - selectedString)).toPx()
        }
    }
    LaunchedEffect(key1 = selectedString) {
        scrollState.animateScrollTo(selectedStringButtonPosition.toInt())
    }

    Row(
        modifier = modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))
        strings.forEach {
            val (index, string) = it
            StringSelectionButton(
                index = index,
                string = string,
                selected = selectedString == index,
                tuned = tuned[index],
                onSelect = onSelect
            )
        }
        Spacer(Modifier.width(8.dp))
    }
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
            onClick = remember(onTuneDown, index) { { onTuneDown(index) } },
            enabled = remember(string) { derivedStateOf { string.rootNoteIndex > Tuner.LOWEST_NOTE } }.value
        ) {
            Icon(Icons.Default.Remove, stringResource(R.string.tune_down))
        }

        StringSelectionButton(tuned, selected, onSelect, index, string)

        // Tune Up Button
        IconButton(
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
 * @param selected Whether the string is currently selected for tuning.
 * @param onSelect Called when the string is selected.
 */
@Composable
private fun StringSelectionButton(
    tuned: Boolean,
    selected: Boolean,
    onSelect: (Int) -> Unit,
    index: Int,
    string: GuitarString
) {
    // Animate content color by selected and tuned state.
    val contentColor by animateColorAsState(
        if (tuned) MaterialTheme.colors.primary
        else if (selected) MaterialTheme.colors.secondaryVariant
        else LocalContentColor.current,
        label = "String Button Content Color"
    )

    // Animate background color by selected state.
    val backgroundColor by animateColorAsState(
        if (selected) {
            contentColor.copy(alpha = 0.12f)
                .compositeOver(MaterialTheme.colors.background)
        } else MaterialTheme.colors.background,
        label = "String Button Background Color"
    )

    // Selection Button
    OutlinedButton(
        modifier = Modifier.size(72.dp, 48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
        ),
        shape = RoundedCornerShape(100),
        onClick = remember(onSelect, index) { { onSelect(index) } }
    ) {
        Text(string.toFullString(), modifier = Modifier.padding(4.dp))
    }
}