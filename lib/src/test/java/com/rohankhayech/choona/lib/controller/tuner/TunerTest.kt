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

package com.rohankhayech.choona.lib.controller.tuner

import be.tarsos.dsp.pitch.PitchDetectionResult
import com.rohankhayech.choona.lib.model.tuning.GuitarString
import com.rohankhayech.choona.lib.model.tuning.Notes
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.model.tuning.Tunings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class TunerTest {

    private lateinit var tuner: Tuner

    @Before
    fun setUp() {
        tuner = Tuner()
    }

    @Test
    fun testSelectString() {
        tuner.selectString(5)
        assertEquals(5, tuner.selectedString.value)
        assertFalse(tuner.autoDetect.value)

        // Test invalid
        assertThrows(IllegalArgumentException::class.java) { tuner.selectString(-1) }
        assertThrows(IllegalArgumentException::class.java) { tuner.selectString(6) }
    }

    @Test
    fun testSetTuning() {
        for (i in 0 until tuner.tuning.value.numStrings()) {
            tuner.setTuned(i)
        }

        tuner.setTuning(Tuning.DROP_D)
        assertSame(Tuning.DROP_D, tuner.tuning.value)

        // Check reset tuned status on different string.
        assertFalse(tuner.tuned.value[5])
        for (i in 0..4) {
            try {
                assertTrue(tuner.tuned.value[i])
            } catch (_: AssertionError) {
                fail("String $i should be tuned")
            }
        }
        assertFalse(tuner.chromatic.value)

        tuner.setChromatic()
        tuner.setTuning(Tunings.WHOLE_STEP_DOWN)

        assertSame(Tunings.WHOLE_STEP_DOWN, tuner.tuning.value)
        assertFalse(tuner.chromatic.value)
    }

    @Test
    fun testTuneUp() {
        for (i in 0 until tuner.tuning.value.numStrings()) {
            tuner.setTuned(i)
        }

        tuner.tuneUp()
        assertEquals(Tuning.STANDARD.higherTuning(), tuner.tuning.value)
        for (i in 0..5) {
            assertFalse(tuner.tuned.value[i])
        }

        // Test highest.
        tuner.setTuning(
            Tuning(
                GuitarString.A2,
                GuitarString.fromRootNote(
                    Notes.getSymbol(Tuner.HIGHEST_NOTE - 1)
                )
            )
        )
        assertTrue(tuner.tuneUp())
        assertFalse(tuner.tuneUp())

    }

    @Test
    fun testTuneDown() {
        for (i in 0 until tuner.tuning.value.numStrings()) {
            tuner.setTuned(i)
        }

        tuner.tuneDown()
        assertEquals(Tuning.STANDARD.lowerTuning(), tuner.tuning.value)
        for (i in 0..5) {
            assertFalse(tuner.tuned.value[i])
        }

        // Test lowest.
        tuner.setTuning(
            Tuning(
                GuitarString.A2,
                GuitarString.fromRootNote(
                    Notes.getSymbol(Tuner.LOWEST_NOTE + 1)
                )
            )
        )
        assertTrue(tuner.tuneDown())
        assertFalse(tuner.tuneDown())
    }

    @Test
    fun testTuneStringUp() {
        tuner.setTuned(0)

        tuner.tuneStringUp(0)
        assertEquals(GuitarString.E4.higherString(), tuner.tuning.value.getString(0))
        assertFalse(tuner.tuned.value[0])

        // Test invalid
        assertThrows(IllegalArgumentException::class.java) { tuner.tuneStringUp(-1) }
        assertThrows(IllegalArgumentException::class.java) { tuner.tuneStringUp(6) }

        // Test highest.
        tuner.setTuning(Tuning(GuitarString.fromRootNote(Notes.getSymbol(Tuner.HIGHEST_NOTE - 1))))
        assertTrue(tuner.tuneStringUp(0))
        assertFalse(tuner.tuneStringUp(0))
    }

    @Test
    fun testTuneStringDown() {
        tuner.setTuned(0)

        tuner.tuneStringDown(0)
        assertEquals(GuitarString.E4.lowerString(), tuner.tuning.value.getString(0))
        assertFalse(tuner.tuned.value[0])

        // Test invalid
        assertThrows(IllegalArgumentException::class.java) { tuner.tuneStringDown(-1) }
        assertThrows(IllegalArgumentException::class.java) { tuner.tuneStringDown(6) }

        // Test lowest.
        tuner.setTuning(Tuning(GuitarString.fromRootNote(Notes.getSymbol(Tuner.LOWEST_NOTE + 1))))
        assertTrue(tuner.tuneStringDown(0))
        assertFalse(tuner.tuneStringDown(0))
    }

    @Test
    fun testSelectNote() {
        tuner.setChromatic()
        tuner.setTuned()

        tuner.selectNote(Tuner.LOWEST_NOTE)
        assertEquals(Tuner.LOWEST_NOTE, tuner.selectedNote.value)
        assertFalse(tuner.noteTuned.value)

        tuner.selectNote(Tuner.HIGHEST_NOTE)
        assertEquals(Tuner.HIGHEST_NOTE, tuner.selectedNote.value)

        tuner.setTuned()
        tuner.selectNote(Tuner.HIGHEST_NOTE)
        assertTrue(tuner.noteTuned.value)

        assertThrows(IllegalArgumentException::class.java) { tuner.selectNote(Tuner.LOWEST_NOTE-1) }
        assertThrows(IllegalArgumentException::class.java) { tuner.selectNote(Tuner.HIGHEST_NOTE+1) }
    }

    @Test
    fun testSetTuned() {
        tuner.setTuned()
        assertTrue(tuner.tuned.value[0])
        tuner.setTuned(1)
        assertTrue(tuner.tuned.value[1])
        tuner.setTuned(1, false)
        assertFalse(tuner.tuned.value[1])

        // Test tuned.
        assertThrows(IllegalArgumentException::class.java) { tuner.setTuned(-1) }
        assertThrows(IllegalArgumentException::class.java) { tuner.setTuned(6) }
    }

    @Test
    fun testSetAutoDetect() {
        tuner.setAutoDetect(false)
        assertFalse(tuner.autoDetect.value)
        tuner.setAutoDetect(false)
        assertFalse(tuner.autoDetect.value)
    }

    @Test
    fun testProcessPitch() {
        // Test with auto detect.
        tuner.selectString(5)
        tuner.setAutoDetect(true)
        tuner.processPitch(PitchDetectionResult().apply { pitch = 440f; isPitched = true })
        assertEquals(0, tuner.selectedString.value) // Test auto-detect.
        assertEquals(5.0, tuner.noteOffset.value!!, 0.001) // Test note offset.

        // Test with auto detect off.
        tuner.setAutoDetect(false)
        tuner.processPitch(PitchDetectionResult().apply { pitch = 246.94f; isPitched = true })
        assertEquals(0, tuner.selectedString.value) // Test no auto-detect.
        assertEquals(-5.0, tuner.noteOffset.value!!, 0.001) // Test note offset.

        // Test un-pitched.
        tuner.setAutoDetect(true)
        tuner.processPitch(PitchDetectionResult().apply { pitch = -1f; isPitched = false })
        assertEquals(0, tuner.selectedString.value)
        assertNull(tuner.noteOffset.value) // Test not offset.

        // Test chromatic with auto detect.
        tuner.setChromatic()
        tuner.setTuned()
        tuner.setAutoDetect(true)
        tuner.processPitch(PitchDetectionResult().apply { pitch = 452f; isPitched = true })
        assertEquals(Notes.getIndex("A4"), tuner.selectedNote.value) // Test auto-detect.
        assertEquals(0.466, tuner.noteOffset.value!!, 0.001) // Test note offset.
        assertFalse(tuner.noteTuned.value)

        // Test with auto detect off.
        tuner.setAutoDetect(false)
        tuner.processPitch(PitchDetectionResult().apply { pitch = 246.94f; isPitched = true })
        assertEquals(Notes.getIndex("A4"), tuner.selectedNote.value) // Test no auto-detect.
        assertEquals(-10.0, tuner.noteOffset.value!!, 0.001) // Test note offset.

        // Test un-pitched.
        tuner.setAutoDetect(true)
        tuner.processPitch(PitchDetectionResult().apply { pitch = -1f; isPitched = false })
        assertEquals(Notes.getIndex("A4"), tuner.selectedNote.value) // Test no change.
        assertNull(tuner.noteOffset.value) // Test not offset.
    }
}