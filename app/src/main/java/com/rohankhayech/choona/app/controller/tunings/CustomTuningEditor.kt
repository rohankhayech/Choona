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

package com.rohankhayech.choona.app.controller.tunings

import com.rohankhayech.choona.lib.controller.tuner.Tuner
import com.rohankhayech.choona.lib.controller.tunings.TuningEditor
import com.rohankhayech.choona.lib.model.tuning.GuitarString
import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CustomTuningEditor(tuning: TuningEntry.InstrumentTuning, val new: Boolean): TuningEditor(tuning.tuning) {
    private val _name = MutableStateFlow(tuning.name)

    val name = _name.asStateFlow()

    fun setName(name: String) {
        _name.update { name }
    }

    fun setInstrument(instrument: Instrument) {
        _tuning.update {
            Tuning(it.name, instrument, null, it.strings)
        }
    }

    fun setString(n: Int, noteIndex: Int) {
        requireValidNoteIndex(noteIndex)
        _tuning.update { it.withString(n, GuitarString.fromRootNoteIndex(noteIndex)) }
    }

    fun addLowString(noteIndex: Int) {
        requireValidNoteIndex(noteIndex)
        require(tuning.value.numStrings() < 12)
        _tuning.update {
            Tuning(
                it.name,
                it.instrument,
                null,
                it.strings.plusElement(GuitarString.fromRootNoteIndex(noteIndex))
            )
        }
    }

    fun addHighString(noteIndex: Int) {
        requireValidNoteIndex(noteIndex)
        require(tuning.value.numStrings() < 12)
        _tuning.update {
            Tuning(
                it.name,
                it.instrument,
                null,
                listOf(GuitarString.fromRootNoteIndex(noteIndex)).plus(it.strings)
            )
        }
    }

    fun removeLowString() {
        require(tuning.value.numStrings() > 1)
        _tuning.update {
            Tuning(it.name, it.instrument, null, it.strings.takeLast(it.numStrings() - 1))
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