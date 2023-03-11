/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.controller

import be.tarsos.dsp.pitch.PitchDetectionResult
import com.rohankhayech.music.GuitarString
import com.rohankhayech.music.Notes
import com.rohankhayech.music.Tuning
import org.junit.Assert.*
import org.junit.Test

class TunerTest {

    private val tuner = Tuner()

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
            assertTrue(tuner.tuned.value[i])
        }
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
        tuner.setTuning(Tuning(GuitarString.A2, GuitarString.fromRootNote(Notes.getSymbol(Tuner.HIGHEST_NOTE-1))))
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
        tuner.setTuning(Tuning(GuitarString.A2, GuitarString.fromRootNote(Notes.getSymbol(Tuner.LOWEST_NOTE+1))))
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
        tuner.setTuning(Tuning(GuitarString.fromRootNote(Notes.getSymbol(Tuner.HIGHEST_NOTE-1))))
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
        tuner.setTuning(Tuning(GuitarString.fromRootNote(Notes.getSymbol(Tuner.LOWEST_NOTE+1))))
        assertTrue(tuner.tuneStringDown(0))
        assertFalse(tuner.tuneStringDown(0))
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
    }
}