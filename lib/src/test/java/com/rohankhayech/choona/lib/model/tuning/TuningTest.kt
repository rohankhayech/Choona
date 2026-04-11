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
}
