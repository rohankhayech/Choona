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

package com.rohankhayech.choona.lib.model.tuning

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TuningTest {
    @Test
    fun testChromatic() {
        val chromatic = ChromaticTuning
        assertEquals("Chromatic", chromatic.name)
        assertEquals("chromatic", chromatic.key)
        assertTrue(chromatic.hasName())
        assertEquals("Chromatic", chromatic.toString())
    }

    @Test
    fun testEqualsAndHashCode() {
        val entry1: Tuning = Tunings.OPEN_G
        val entry2: Tuning = Tunings.OPEN_G
        val entry3: Tuning = Tunings.OPEN_D
        val entry4: Tuning = ChromaticTuning
        assertEquals(entry1, entry2)
        assertEquals(entry1.hashCode(), entry2.hashCode())
        assertEquals(entry4, entry4)
        assertEquals(entry4.hashCode(), entry4.hashCode())
        assertNotEquals(entry1, entry4)
        assertNotEquals(entry1.hashCode(), entry4.hashCode())
        assertNotEquals(entry1, entry3)
        assertNotEquals(entry1.hashCode(), entry3.hashCode())
    }

    @Test
    fun testUnnamedInstrument() {
        val tuning = InstrumentTuning(GuitarString.E2)
        assertEquals(GuitarString.E2.toString(), tuning.name)
        assertNull(tuning.rawName)
        assertFalse(tuning.hasName())
    }

    @Test
    fun testEquivalentTo() {
        // Check same are equivalent
        assertTrue("Same objects are not equal.", Tunings.STANDARD.equivalentTo(Tunings.STANDARD))
        assertTrue(ChromaticTuning equivalentTo ChromaticTuning)

        // Check equivalence.
        val standard = InstrumentTuning(
            GuitarString.E4,
            GuitarString.B3,
            GuitarString.G3,
            GuitarString.D3,
            GuitarString.A2,
            GuitarString.E2
        )
        assertTrue("Tunings were not equivalent.", Tunings.STANDARD.equivalentTo(standard))
        assertFalse("Different tunings were equivalent.", Tunings.DROP_D.equivalentTo(Tunings.STANDARD))
        val bass = InstrumentTuning(
            Instrument.BASS,
            GuitarString.E4,
            GuitarString.B3,
            GuitarString.G3,
            GuitarString.D3,
            GuitarString.A2,
            GuitarString.E2
        )
        assertFalse("Different instrument tunings were equivalent.", bass.equivalentTo(Tunings.STANDARD))
        assertFalse("Instrument and chromatic tunings were equivalent.", Tunings.STANDARD equivalentTo ChromaticTuning)
        assertFalse("Instrument and chromatic tunings were equivalent.", ChromaticTuning equivalentTo Tunings.STANDARD)
    }

    @Test
    fun testHasEquivalentIn() {
        val standard = InstrumentTuning(
            GuitarString.E4,
            GuitarString.B3,
            GuitarString.G3,
            GuitarString.D3,
            GuitarString.A2,
            GuitarString.E2
        )

        // Check containing
        var list = listOf(Tunings.DROP_D, Tunings.STANDARD)
        assertTrue("Returned false for list containing equivalent.", standard.hasEquivalentIn(list))

        // Check not containing
        list = listOf(Tunings.DROP_D)
        assertFalse("Returned true for list not containing equivalent.", standard.hasEquivalentIn(list))
    }

    @Test
    fun testFindEquivalentIn() {
        val standard = InstrumentTuning(
            GuitarString.E4,
            GuitarString.B3,
            GuitarString.G3,
            GuitarString.D3,
            GuitarString.A2,
            GuitarString.E2
        )

        // Check containing
        var list = listOf(Tunings.DROP_D, Tunings.STANDARD)
        Assert.assertSame(
            "Did not return correct tuning for list containing equivalent.",
            Tunings.STANDARD,
            standard.findEquivalentIn(list)
        )

        // Check not containing
        list = listOf(Tunings.DROP_D)
        assertNull("Returned tuning for list containing equivalent.", standard.findEquivalentIn(list))
    }
}
