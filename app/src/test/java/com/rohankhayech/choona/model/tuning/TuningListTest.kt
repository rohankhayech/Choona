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

package com.rohankhayech.choona.model.tuning

import com.rohankhayech.music.Instrument
import com.rohankhayech.music.Tuning
import com.rohankhayech.music.Tuning.Category
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

/**
 * Test harness for the tuning list class.
 *
 * @author Rohan Khayech
 */
class TuningListTest {

    private lateinit var tuningList: TuningList

    private val testScope = TestScope()

    @Before
    fun setUp() {
        tuningList = TuningList(coroutineScope = testScope)
    }

    @Test
    fun testConstructor() {
        val tl = TuningList(Tuning.STANDARD, TestScope())
        assertSame(Tuning.STANDARD, tl.current.value)
    }

    @Test
    fun testCurrent() {
        // Test default value.
        assertNull(tuningList.current.value)

        // Test not in list.
        var new = Tuning.fromString("E2")
        tuningList.setCurrent(new)
        assertSame(new, tuningList.current.value)

        // Test equiv in list.
        new = Tuning.fromString("E4 B3 G3 D3 A2 E2")
        tuningList.setCurrent(new)
        assertSame(Tuning.STANDARD, tuningList.current.value)
    }

    @Test
    fun testFavourites() {
        // Test default value.
        assertEquals(setOf(Tuning.STANDARD), tuningList.favourites.value)

        // Test set fav
        tuningList.setFavourited(Tuning.DROP_D, true)
        assertEquals(setOf(Tuning.STANDARD, Tuning.DROP_D), tuningList.favourites.value)

        // Test set unfav
        tuningList.setFavourited(Tuning.DROP_D, false)
        assertEquals(setOf(Tuning.STANDARD), tuningList.favourites.value)
    }

    @Test
    fun testCustom() {
        // Test default value.
        assertEquals(emptySet<Tuning>(), tuningList.custom.value)

        // Setup
        tuningList.setCurrent(Tuning.STANDARD)

        // Test add custom.
        val new = Tuning.fromString("E2")
        val named = Tuning.fromString("New", Tuning.DEFAULT_INSTRUMENT, null, "E2")
        tuningList.addCustom("New", new)
        assertEquals(setOf(named), tuningList.custom.value)
        assertEquals(Tuning.STANDARD, tuningList.current.value)
        tuningList.removeCustom(named)

        // Test add custom with equiv current
        tuningList.setCurrent(new)
        tuningList.addCustom("New", new)
        assertEquals(named, tuningList.current.value)

        // Test remove custom
        tuningList.setFavourited(named, true)
        tuningList.removeCustom(named)
        assertEquals(emptySet<Tuning>(), tuningList.custom.value)
        assertEquals(setOf(Tuning.STANDARD), tuningList.favourites.value)
        assertEquals(new, tuningList.current.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFilteredTunings() {
        // Starts the cold flow
        testScope.backgroundScope.launch {
            tuningList.filteredTunings.collect {}
        }

        // Test default value
        val expectedDef = Tunings.COMMON.groupAndSort()
        assertEquals(expectedDef, tuningList.filteredTunings.value)

        // Test filtered by instrument
        tuningList.filterBy(instrument = Instrument.BASS)
        testScope.advanceUntilIdle()
        var expectedInstr = mapOf(
            Pair(Instrument.BASS, Category.COMMON) to listOf(
                Tunings.BASS_STANDARD,
                Tunings.BASS_DROP_D,
                Tunings.BASS_E_FLAT
            )
        )
        assertEquals(expectedInstr, tuningList.filteredTunings.value)

        // Test filtered by category
        tuningList.filterBy(instrument = null, category = Category.MISC)
        testScope.advanceUntilIdle()
        expectedInstr = mapOf(
            Pair(Instrument.GUITAR, Category.MISC) to listOf(
                Tunings.G_MODAL,
                Tunings.ALL_4TH,
                Tunings.NST
            )
        )
        assertEquals(expectedInstr, tuningList.filteredTunings.value)

        // Test filtered by both
        tuningList.filterBy(instrument = Instrument.BASS, category = Category.MISC)
        testScope.advanceUntilIdle()
        expectedInstr = emptyMap()
        assertEquals(expectedInstr, tuningList.filteredTunings.value)
    }

    @Test
    fun testEquals() {
        val newList = TuningList(coroutineScope = testScope)
        assertEquals(tuningList, newList)

        newList.setCurrent(Tuning.STANDARD)
        assertNotEquals(tuningList, newList)
    }

    @Test
    fun testHashCode() {
        val equal = TuningList(coroutineScope = testScope)
        assertEquals(equal.hashCode(), tuningList.hashCode())
    }

    @Test
    fun testGroupAndSort() {
        val guitarCommon = Tuning.fromString("", Instrument.GUITAR, Category.COMMON, "E2")
        val guitarCommon2 = Tuning.fromString("", Instrument.GUITAR, Category.COMMON, "D2")
        val guitarOpen = Tuning.fromString("", Instrument.GUITAR, Category.OPEN, "E2")
        val bassCommon = Tuning.fromString("", Instrument.BASS, Category.COMMON, "E2")
        val bassPower = Tuning.fromString("", Instrument.BASS, Category.POWER, "E2")

        val tunings = listOf(bassPower, guitarCommon, guitarOpen, guitarCommon2, bassCommon)

        val expectedGroups = mapOf<Pair<Instrument, Category?>, List<Tuning>>(
            (Instrument.GUITAR to Category.COMMON) to listOf(guitarCommon, guitarCommon2),
            (Instrument.GUITAR to Category.OPEN) to listOf(guitarOpen),
            (Instrument.BASS to Category.COMMON) to listOf(bassCommon),
            (Instrument.BASS to Category.POWER) to listOf(bassPower)
        )

        val grouped = tunings.groupAndSort()

        assertEquals(expectedGroups, grouped)
    }
}