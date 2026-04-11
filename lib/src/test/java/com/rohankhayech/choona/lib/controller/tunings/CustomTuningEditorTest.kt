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
import com.rohankhayech.choona.lib.model.tuning.InstrumentTuning
import com.rohankhayech.choona.lib.model.tuning.Notes
import com.rohankhayech.choona.lib.model.tuning.Tunings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * Unit tests for [CustomTuningEditor].
 *
 * @author Rohan Khayech
 */
class CustomTuningEditorTest {

    /**
     * Verifies that [CustomTuningEditor.setInstrument] correctly updates the instrument of the tuning being edited.
     */
    @Test
    fun setInstrument() {
        val editor = CustomTuningEditor(Tunings.STANDARD)
        editor.setInstrument(Instrument.BASS)
        assertEquals(Instrument.BASS, editor.tuning.value.instrument)
    }

    /**
     * Verifies that [CustomTuningEditor.setString] correctly updates the note of a specific string.
     */
    @Test
    fun setString() {
        val editor = CustomTuningEditor(Tunings.STANDARD)
        val newNoteIndex = Notes.getIndex("A4")
        editor.setString(0, newNoteIndex)
        assertEquals(GuitarString.fromRootNoteIndex(newNoteIndex), editor.tuning.value.getString(0))
    }

    /**
     * Verifies that [CustomTuningEditor.setString] throws an [IllegalArgumentException] when an invalid note index is provided.
     */
    @Test
    fun setString_invalidIndex_throwsException() {
        val editor = CustomTuningEditor(Tunings.STANDARD)
        assertThrows(IllegalArgumentException::class.java) {
            editor.setString(0, Tuner.LOWEST_NOTE - 1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            editor.setString(0, Tuner.HIGHEST_NOTE + 1)
        }
    }

    /**
     * Verifies that [CustomTuningEditor.addLowString] correctly adds a new string to the bottom of the tuning.
     */
    @Test
    fun addLowString() {
        val initialTuning = Tunings.STANDARD
        val editor = CustomTuningEditor(initialTuning)
        editor.addLowString()
        assertEquals(initialTuning.numStrings() + 1, editor.tuning.value.numStrings())
        assertEquals(initialTuning.strings.last(), editor.tuning.value.strings.last())
    }

    /**
     * Verifies that [CustomTuningEditor.addLowString] throws an [IllegalArgumentException] when the maximum number of strings is reached.
     */
    @Test
    fun addLowString_maxStrings_throwsException() {
        val editor = CustomTuningEditor(InstrumentTuning("", Instrument.GUITAR, null, List(MAX_STRINGS) { GuitarString.E2 }))
        assertThrows(IllegalArgumentException::class.java) {
            editor.addLowString()
        }
    }

    /**
     * Verifies that [CustomTuningEditor.addHighString] correctly adds a new string to the top of the tuning.
     */
    @Test
    fun addHighString() {
        val initialTuning = Tunings.STANDARD
        val editor = CustomTuningEditor(initialTuning)
        editor.addHighString()
        assertEquals(initialTuning.numStrings() + 1, editor.tuning.value.numStrings())
        assertEquals(initialTuning.strings.first(), editor.tuning.value.strings.first())
    }

    /**
     * Verifies that [CustomTuningEditor.addHighString] throws an [IllegalArgumentException] when the maximum number of strings is reached.
     */
    @Test
    fun addHighString_maxStrings_throwsException() {
        val editor = CustomTuningEditor(InstrumentTuning("", Instrument.GUITAR, null, List(MAX_STRINGS) { GuitarString.E2 }))
        assertThrows(IllegalArgumentException::class.java) {
            editor.addHighString()
        }
    }

    /**
     * Verifies that [CustomTuningEditor.removeLowString] correctly removes the string from the bottom of the tuning.
     */
    @Test
    fun removeLowString() {
        val initialTuning = Tunings.STANDARD
        val editor = CustomTuningEditor(initialTuning)
        editor.removeLowString()
        assertEquals(initialTuning.numStrings() - 1, editor.tuning.value.numStrings())
        assertEquals(initialTuning.strings.dropLast(1), editor.tuning.value.strings)
    }

    /**
     * Verifies that [CustomTuningEditor.removeLowString] throws an [IllegalArgumentException] when only [MIN_STRINGS] remains.
     */
    @Test
    fun removeLowString_minStrings_throwsException() {
        val editor = CustomTuningEditor(InstrumentTuning("", Instrument.GUITAR, null, List(MIN_STRINGS) { GuitarString.E2 }))
        assertThrows(IllegalArgumentException::class.java) {
            editor.removeLowString()
        }
    }

    /**
     * Verifies that [CustomTuningEditor.removeHighString] correctly removes the string from the top of the tuning.
     */
    @Test
    fun removeHighString() {
        val initialTuning = Tunings.STANDARD
        val editor = CustomTuningEditor(initialTuning)
        editor.removeHighString()
        assertEquals(initialTuning.numStrings() - 1, editor.tuning.value.numStrings())
        assertEquals(initialTuning.strings.drop(1), editor.tuning.value.strings)
    }

    /**
     * Verifies that [CustomTuningEditor.removeHighString] throws an [IllegalArgumentException] when only [MIN_STRINGS] remains.
     */
    @Test
    fun removeHighString_minStrings_throwsException() {
        val editor = CustomTuningEditor(InstrumentTuning("", Instrument.GUITAR, null, List(MIN_STRINGS) { GuitarString.E2 }))
        assertThrows(IllegalArgumentException::class.java) {
            editor.removeHighString()
        }
    }
}