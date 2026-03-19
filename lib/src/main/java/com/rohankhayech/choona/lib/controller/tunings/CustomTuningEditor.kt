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

const val MAX_STRINGS = 12

class CustomTuningEditor(
    tuning: Tuning
): TuningEditor(tuning) {
    fun setInstrument(instrument: Instrument) {
        _tuning.update {
            Tuning(it.name, instrument, null, it.strings)
        }
    }

    fun setString(n: Int, noteIndex: Int) {
        requireValidNoteIndex(noteIndex)
        _tuning.update { it.withString(n, GuitarString.fromRootNoteIndex(noteIndex)) }
    }

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

    fun removeLowString() {
        require(tuning.value.numStrings() > 1)
        _tuning.update {
            Tuning(it.name, it.instrument, null, it.strings.take(it.numStrings() - 1))
        }
    }

    fun removeHighString() {
        require(tuning.value.numStrings() > 1)
        _tuning.update {
            Tuning(it.name, it.instrument, null, it.strings.takeLast(it.numStrings() - 1))
        }
    }

    fun requireValidNoteIndex(noteIndex: Int) {
        require(noteIndex in Tuner.Companion.LOWEST_NOTE..Tuner.Companion.HIGHEST_NOTE)
    }
}