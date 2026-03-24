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

package com.rohankhayech.choona.lib.controller.tunings

import com.rohankhayech.choona.lib.controller.tuner.Tuner
import com.rohankhayech.choona.lib.model.tuning.GuitarString
import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.Tuning
import kotlinx.coroutines.flow.update

/** Minimum number of strings allowed in a tuning. */
const val MIN_STRINGS = 2

/** Maximum number of strings allowed in a tuning. */
const val MAX_STRINGS = 12

/**
 * An editor for creating or modifying custom tunings.
 *
 * Extends [TuningEditor] to provide additional functionality for modifying
 * the tuning structure, such as adding/removing strings and changing the instrument.
 *
 * @param tuning The initial tuning to edit.
 *
 * @author Rohan Khayech
 */
class CustomTuningEditor(
    tuning: Tuning
): TuningEditor(tuning) {

    /**
     * Sets the instrument for the tuning.
     * @param instrument The new instrument.
     */
    fun setInstrument(instrument: Instrument) {
        _tuning.update {
            Tuning(it.name, instrument, null, it.strings)
        }
    }

    /**
     * Sets the note for a specific string.
     * @param n The index of the string to set (0 is the lowest string).
     * @param noteIndex The index of the note to set.
     * @throws IllegalArgumentException if the string or note index is out of range.
     */
    fun setString(n: Int, noteIndex: Int) {
        require(n in 0 until tuning.value.numStrings())
        requireValidNoteIndex(noteIndex)
        _tuning.update { it.withString(n, GuitarString.fromRootNoteIndex(noteIndex)) }
    }

    /**
     * Adds a new lowest string to the tuning, duplicating the current lowest string.
     * @throws IllegalArgumentException if the number of strings is already at [MAX_STRINGS].
     */
    fun addLowString() {
        require(tuning.value.numStrings() < MAX_STRINGS)
        _tuning.update {
            Tuning(
                it.name,
                it.instrument,
                null,
                it.strings.plusElement(it.strings.last())
            )
        }
    }

    /**
     * Adds a new highest string to the tuning, duplicating the current highest string.
     * @throws IllegalArgumentException if the number of strings is already at [MAX_STRINGS].
     */
    fun addHighString() {
        require(tuning.value.numStrings() < MAX_STRINGS)
        _tuning.update {
            Tuning(
                it.name,
                it.instrument,
                null,
                listOf(it.strings.first()).plus(it.strings)
            )
        }
    }

    /**
     * Removes the lowest string from the tuning.
     * @throws IllegalArgumentException if the tuning only has [MIN_STRINGS] remaining.
     */
    fun removeLowString() {
        require(tuning.value.numStrings() > MIN_STRINGS)
        _tuning.update {
            Tuning(it.name, it.instrument, null, it.strings.take(it.numStrings() - 1))
        }
    }

    /**
     * Removes the highest string from the tuning.
     * @throws IllegalArgumentException if the tuning only has [MIN_STRINGS] remaining.
     */
    fun removeHighString() {
        require(tuning.value.numStrings() > MIN_STRINGS)
        _tuning.update {
            Tuning(it.name, it.instrument, null, it.strings.takeLast(it.numStrings() - 1))
        }
    }

    /**
     * Validates that a note index is within the supported range of the tuner.
     * @param noteIndex The note index to validate.
     * @throws IllegalArgumentException if the note index is out of range.
     */
    fun requireValidNoteIndex(noteIndex: Int) {
        require(noteIndex in Tuner.Companion.LOWEST_NOTE..Tuner.Companion.HIGHEST_NOTE)
    }
}