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

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.rohankhayech.android.util.ui.layout.ItemScrollPosition
import com.rohankhayech.android.util.ui.layout.LazyListAutoScroll
import com.rohankhayech.android.util.ui.preview.ThemePreview
import com.rohankhayech.android.util.ui.theme.m3.harmonised
import com.armarizki.chromatic.controller.tuner.Tuner.Companion.HIGHEST_NOTE
import com.armarizki.chromatic.controller.tuner.Tuner.Companion.LOWEST_NOTE
import com.armarizki.chromatic.view.theme.PreviewWrapper
import com.armarizki.chromatic.view.theme.extColors
import com.armarizki.music.Notes

 
private val LOWEST_OCTAVE = Notes.getOctave(Notes.getSymbol(LOWEST_NOTE))

 
private val HIGHEST_OCTAVE = Notes.getOctave(Notes.getSymbol(HIGHEST_NOTE))

 
private val NUM_OCTAVES = HIGHEST_OCTAVE - LOWEST_OCTAVE + 1

 
private val LOWEST_ROOT_NOTE_INDEX = Notes.NOTE_SYMBOLS.indexOf(Notes.getRootNote(Notes.getSymbol(LOWEST_NOTE)))

 
private val HIGHEST_ROOT_NOTE_INDEX = Notes.NOTE_SYMBOLS.indexOf(Notes.getRootNote(Notes.getSymbol(HIGHEST_NOTE)))

 
@Composable
fun NoteSelector(
    modifier: Modifier = Modifier,
    selectedNoteIndex: Int,
    tuned: Boolean,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val selectedSymbol = remember (selectedNoteIndex) { Notes.getSymbol(selectedNoteIndex) }

        val selectedNote = remember (selectedSymbol) { Notes.NOTE_SYMBOLS.indexOf(Notes.getRootNote(selectedSymbol)) }
        val selectedOctave = remember (selectedSymbol) { Notes.getOctave(selectedSymbol) }

        ScrollableButtonRow(
            items = Notes.NOTE_SYMBOLS.mapIndexed { i, s -> Pair(i, s) },
            selectedIndex = selectedNote,
            activatedButtons = remember (tuned, selectedNote) { BooleanArray(Notes.NOTE_SYMBOLS.size) { i -> tuned && i == selectedNote }},
            disabledButtons = when(selectedOctave) {
                LOWEST_OCTAVE ->
                    BooleanArray(Notes.NOTE_SYMBOLS.size) { i -> i < LOWEST_ROOT_NOTE_INDEX }
                HIGHEST_OCTAVE ->
                    BooleanArray(Notes.NOTE_SYMBOLS.size) { i -> i > HIGHEST_ROOT_NOTE_INDEX }
                else -> BooleanArray(Notes.NOTE_SYMBOLS.size) { false }
            },
            onSelect = remember (selectedOctave, onSelect) {{ index ->
                onSelect(Notes.getIndex("${Notes.NOTE_SYMBOLS[index]}$selectedOctave"))
            }}
        )

        ScrollableButtonRow(
            items = List(NUM_OCTAVES) { i ->
                "${LOWEST_OCTAVE+i}"
            }.mapIndexed { i, s -> Pair(i, s) },
            selectedIndex = selectedOctave - LOWEST_OCTAVE,
            activatedButtons = BooleanArray(NUM_OCTAVES) { i -> tuned && i == selectedOctave - LOWEST_OCTAVE },
            disabledButtons = remember (selectedNote) { when {
                selectedNote < LOWEST_ROOT_NOTE_INDEX ->
                    BooleanArray(NUM_OCTAVES) { i -> i <= LOWEST_OCTAVE - 1 }
                selectedNote > HIGHEST_ROOT_NOTE_INDEX ->
                    BooleanArray(NUM_OCTAVES) { i -> i >= HIGHEST_OCTAVE - 1}
                else -> BooleanArray(HIGHEST_OCTAVE-LOWEST_OCTAVE + 1) { false }
            }},
            onSelect = remember (selectedNote, onSelect) {{ index ->
                onSelect(Notes.getIndex("${Notes.NOTE_SYMBOLS[selectedNote]}${index + 1}"))
            }}
        )
    }
}

 
@Composable
fun CompactNoteSelector(
    modifier: Modifier = Modifier,
    selectedNoteIndex: Int,
    tuned: Boolean,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val notes = List(HIGHEST_NOTE - LOWEST_NOTE + 1) { i ->
            Pair(i, Notes.getSymbol(i + LOWEST_NOTE))
        }

        ScrollableButtonRow(
            items = notes,
            selectedIndex = selectedNoteIndex - LOWEST_NOTE,
            activatedButtons = remember (tuned, selectedNoteIndex) { BooleanArray(notes.size) { i -> tuned && i == selectedNoteIndex - LOWEST_NOTE }},
            onSelect = remember (onSelect) {{ index ->
                onSelect(index + LOWEST_NOTE)
            }}
        )
    }
}

 
@Composable
fun ScrollableButtonRow(
    modifier: Modifier = Modifier,
    items: List<Pair<Int, String>>,
    selectedIndex: Int,
    activatedButtons: BooleanArray,
    disabledButtons: BooleanArray = BooleanArray(items.size) { false },
    reversed: Boolean = false,
    onSelect: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    // Center the selected button.
    LazyListAutoScroll(listState, selectedIndex, ItemScrollPosition.Center)

    LazyRow(
        modifier,
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 8.dp),
        reverseLayout = reversed
    ) {
        items(items, {(index) -> index}) { (index, label) ->
            NoteSelectionButton(
                index = index,
                label = label,
                selected = selectedIndex == index,
                tuned = activatedButtons[index],
                disabled = disabledButtons[index],
                onSelect = onSelect
            )
        }
    }
}

 
@Composable
fun NoteSelectionButton(
    index: Int,
    label: String,
    tuned: Boolean,
    selected: Boolean,
    disabled: Boolean = false,
    onSelect: (Int) -> Unit,
) {
    // Animate content color by selected and tuned state.
    val contentColor by animateColorAsState(
        if (selected) {
            if (tuned) MaterialTheme.extColors.blue.onContainer.harmonised()
            else MaterialTheme.colorScheme.onTertiaryContainer
        }
        else if (tuned) MaterialTheme.extColors.blue.color.harmonised()
        else LocalContentColor.current,
        label = "String Button Content Color"
    )

    // Animate background color by selected state.
    val backgroundColor by animateColorAsState(
        if (selected) {
            if (tuned) MaterialTheme.extColors.blue.container.harmonised()
            else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.60f)
        }
        else MaterialTheme.colorScheme.background,
        label = "String Button Background Color"
    )

    // Selection Button
    OutlinedButton(
        modifier = Modifier.defaultMinSize(84.dp, 48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = ButtonDefaults.outlinedButtonBorder()
            .copy(brush = SolidColor(
                if (selected) contentColor.copy(alpha = 0.38f)
                else MaterialTheme.colorScheme.outlineVariant
            )),
        enabled = !disabled,
        onClick = remember(onSelect, index) { { onSelect(index) } }
    ) {
        Text(label, modifier = Modifier.padding(4.dp))
    }
}

// Previews

@ThemePreview
@Composable
private fun Preview() {
    var noteIndex by remember { mutableIntStateOf(-29) }

    PreviewWrapper {
        NoteSelector(
            modifier = Modifier.padding(vertical = 8.dp),
            selectedNoteIndex = noteIndex,
            tuned = false,
            onSelect = { noteIndex = it }
        )
    }
}
