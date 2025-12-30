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

package com.rohankhayech.choona.lib.controller.tunings

import com.rohankhayech.choona.lib.model.tuning.Instrument
import com.rohankhayech.choona.lib.model.tuning.Tuning
import com.rohankhayech.choona.lib.model.tuning.Tuning.Category
import com.rohankhayech.choona.lib.model.tuning.TuningEntry
import com.rohankhayech.choona.lib.model.tuning.Tunings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
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
        val tl = TuningList(Tuning.STANDARD)
        assertEquals(TuningEntry.InstrumentTuning(Tuning.STANDARD), tl.current.value)
    }

    @Test
    fun testCurrent() {
        // Test default value.
        assertNull(tuningList.current.value)

        // Test not in list.
        var new = Tuning.fromString("E2")
        tuningList.setCurrent(TuningEntry.InstrumentTuning(new))
        assertEquals(TuningEntry.InstrumentTuning(new), tuningList.current.value)

        // Test equiv in list.
        new = Tuning.fromString("E4 B3 G3 D3 A2 E2")
        tuningList.setCurrent(TuningEntry.InstrumentTuning(new))
        assertEquals(TuningEntry.InstrumentTuning(Tuning.STANDARD), tuningList.current.value)

        // Test chromatic.
        tuningList.setCurrent(TuningEntry.ChromaticTuning)
        assertEquals(TuningEntry.ChromaticTuning, tuningList.current.value)
    }

    @Test
    fun testFavourites() {
        // Test default value.
        assertEquals(setOf(TuningEntry.InstrumentTuning(Tuning.STANDARD), TuningEntry.ChromaticTuning), tuningList.favourites.value)

        // Test set fav
        tuningList.setFavourited(TuningEntry.InstrumentTuning(Tuning.DROP_D), true)
        assertEquals(setOf(TuningEntry.InstrumentTuning(Tuning.STANDARD), TuningEntry.ChromaticTuning, TuningEntry.InstrumentTuning(Tuning.DROP_D)), tuningList.favourites.value)

        // Test set unfav
        tuningList.setFavourited(TuningEntry.InstrumentTuning(Tuning.DROP_D), false)
        assertEquals(setOf(TuningEntry.InstrumentTuning(Tuning.STANDARD), TuningEntry.ChromaticTuning), tuningList.favourites.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCustom() {
        // Starts the cold flow
        testScope.backgroundScope.launch {
            tuningList.custom.collect {
                println(it)
            }
        }

        // Test default value.
        assertEquals(emptySet<TuningEntry.InstrumentTuning>(), tuningList.custom.value)

        // Setup
        tuningList.setCurrent(TuningEntry.InstrumentTuning(Tuning.STANDARD))

        // Test add custom.
        val new = Tuning.fromString("E2")
        val named = Tuning.fromString("New", Tuning.DEFAULT_INSTRUMENT, null, "E2")
        tuningList.addCustom("New", new)
        testScope.advanceUntilIdle()
        assertEquals(setOf(TuningEntry.InstrumentTuning(named)), tuningList.custom.value)
        assertEquals(TuningEntry.InstrumentTuning(Tuning.STANDARD), tuningList.current.value)
        tuningList.removeCustom(named)
        testScope.advanceUntilIdle()

        // Test add custom with equiv current and pinned
        tuningList.setCurrent(TuningEntry.InstrumentTuning(new))
        tuningList.setPinned(TuningEntry.InstrumentTuning(new))
        tuningList.addCustom("New", new)
        testScope.advanceUntilIdle()
        assertEquals(TuningEntry.InstrumentTuning(named), tuningList.current.value)
        assertEquals(TuningEntry.InstrumentTuning(named), tuningList.pinned.value)

        // Test remove custom
        tuningList.setFavourited(TuningEntry.InstrumentTuning(named), true)
        tuningList.removeCustom(named)
        testScope.advanceUntilIdle()
        assertEquals(emptySet<TuningEntry.InstrumentTuning>(), tuningList.custom.value)
        assertEquals(setOf(TuningEntry.InstrumentTuning(Tuning.STANDARD), TuningEntry.ChromaticTuning), tuningList.favourites.value)
        assertEquals(TuningEntry.InstrumentTuning(new), tuningList.current.value)
        assertEquals(TuningEntry.InstrumentTuning(Tuning.STANDARD), tuningList.pinned.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFilteredTunings() {
        // Starts the cold flow
        testScope.backgroundScope.launch {
            tuningList.filteredTunings.collect {}
        }

        // Test default value
        val expectedDef = TuningList.GROUPED_TUNINGS
        assertEquals(expectedDef, tuningList.filteredTunings.value)

        // Test filtered by instrument
        tuningList.filterBy(instrument = Instrument.BASS)
        testScope.advanceUntilIdle()
        var expectedInstr = mapOf(
            Pair(Instrument.BASS, Category.COMMON) to listOf(
                TuningEntry.InstrumentTuning(Tunings.BASS_STANDARD),
                TuningEntry.InstrumentTuning(Tunings.BASS_DROP_D),
                TuningEntry.InstrumentTuning(Tunings.BASS_E_FLAT)
            )
        )
        assertEquals(expectedInstr, tuningList.filteredTunings.value)

        // Test filtered by category
        tuningList.filterBy(instrument = null, category = Category.COMMON)
        testScope.advanceUntilIdle()
        expectedInstr = mapOf(
            Pair(Instrument.GUITAR, Category.COMMON) to listOf(
                TuningEntry.InstrumentTuning(Tuning.STANDARD),
                TuningEntry.InstrumentTuning(Tunings.HALF_STEP_DOWN),
                TuningEntry.InstrumentTuning(Tunings.WHOLE_STEP_DOWN),
                TuningEntry.InstrumentTuning(Tunings.DROP_D)
            ),
            Pair(Instrument.BASS, Category.COMMON) to listOf(
                TuningEntry.InstrumentTuning(Tunings.BASS_STANDARD),
                TuningEntry.InstrumentTuning(Tunings.BASS_DROP_D),
                TuningEntry.InstrumentTuning(Tunings.BASS_E_FLAT)
            ),
            Pair(Instrument.UKULELE, Category.COMMON) to listOf(
                TuningEntry.InstrumentTuning(Tunings.UKULELE_STANDARD)
            )
        )
        assertEquals(expectedInstr, tuningList.filteredTunings.value)

        // Test filtered by both
        tuningList.filterBy(instrument = Instrument.BASS, category = Category.COMMON)
        testScope.advanceUntilIdle()
        expectedInstr = mapOf(
            Pair(Instrument.BASS, Category.COMMON) to listOf(
                TuningEntry.InstrumentTuning(Tunings.BASS_STANDARD),
                TuningEntry.InstrumentTuning(Tunings.BASS_DROP_D),
                TuningEntry.InstrumentTuning(Tunings.BASS_E_FLAT)
            )
        )
        assertEquals(expectedInstr, tuningList.filteredTunings.value)
    }

    @Test
    fun testFilterBy() {
        // Test instrument
        tuningList.filterBy(instrument = Instrument.GUITAR)
        assertEquals(tuningList.instrumentFilter.value, Instrument.GUITAR)
        tuningList.filterBy(instrument = null)
        assertNull(tuningList.instrumentFilter.value)

        // Test category
        tuningList.filterBy(category = Category.COMMON)
        assertEquals(tuningList.categoryFilter.value, Category.COMMON)
        tuningList.filterBy(category = null)
        assertNull(tuningList.categoryFilter.value)

        // Test both null.
        tuningList.filterBy()
        assertNull(tuningList.categoryFilter.value)
        assertNull(tuningList.instrumentFilter.value)

        // Test compatible
        tuningList.filterBy(instrument = Instrument.BASS, category = Category.COMMON)
        assertEquals(tuningList.instrumentFilter.value, Instrument.BASS)
        assertEquals(tuningList.categoryFilter.value, Category.COMMON)

        // Test incompatible
        assertThrows(IllegalArgumentException::class.java) {
            tuningList.filterBy(category = Category.MISC)
        }
        tuningList.filterBy(instrument = null, category = Category.MISC)
        assertThrows(IllegalArgumentException::class.java) {
            tuningList.filterBy(instrument = Instrument.BASS)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testInstrumentFilters() {
        // Starts the cold flow
        testScope.backgroundScope.launch {
            tuningList.instrumentFilters.collect {}
        }

        // Test initial
        var expected = Instrument.entries.dropLast(1).associateWith { true }
        assertEquals(expected, tuningList.instrumentFilters.value)

        // Test compatible filter.
        tuningList.filterBy(category = Category.COMMON)
        testScope.advanceUntilIdle()
        assertEquals(expected, tuningList.instrumentFilters.value)

        // Test incompatible filter.
        expected = mapOf(
            Instrument.GUITAR to true,
            Instrument.BASS to false,
            Instrument.UKULELE to false
        )
        tuningList.filterBy(category = Category.MISC)
        testScope.advanceUntilIdle()
        assertEquals(expected, tuningList.instrumentFilters.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCategoryFilters() {
        // Starts the cold flow
        testScope.backgroundScope.launch {
            tuningList.categoryFilters.collect {}
        }

        // Test initial
        var expected = Category.entries.associateWith { true }
        assertEquals(expected, tuningList.categoryFilters.value)

        // Test compatible filter.
        tuningList.filterBy(instrument = Instrument.GUITAR)
        testScope.advanceUntilIdle()
        assertEquals(expected, tuningList.categoryFilters.value)

        // Test incompatible filter.
        expected = mapOf(
            Category.COMMON to true,
            Category.POWER to false,
            Category.OPEN to false,
            Category.MISC to false
        )
        tuningList.filterBy(instrument = Instrument.BASS)
        testScope.advanceUntilIdle()
        assertEquals(expected, tuningList.categoryFilters.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testDeletedTuning() {
        // Add custom tunings.
        val new = Tuning.fromString("E2")
        val new2 = Tuning.fromString("E2")
        tuningList.addCustom("New", new)

        // Collect flow.
        val deleted = mutableListOf<Tuning>()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher(testScope.testScheduler)) {
            tuningList.deletedTuning.toList(deleted)
        }
        assertEquals(deleted.size, 0)

        // Delete tunings.
        tuningList.removeCustom(new)
        tuningList.removeCustom(new2)
        assertEquals(deleted[0], new)
        assertEquals(deleted[1], new2)
        assertEquals(deleted.size, 2)
    }

    @Test
    fun testEquals() {
        val newList = TuningList()
        assertEquals(tuningList, newList)

        newList.setCurrent(TuningEntry.InstrumentTuning(Tuning.STANDARD))
        assertNotEquals(tuningList, newList)
    }

    @Test
    fun testHashCode() {
        val equal = TuningList()
        assertEquals(equal.hashCode(), tuningList.hashCode())
    }

    @Test
    fun testGroupAndSort() {
        val guitarCommon = TuningEntry.InstrumentTuning(Tuning.fromString("", Instrument.GUITAR, Category.COMMON, "E2"))
        val guitarCommon2 = TuningEntry.InstrumentTuning(Tuning.fromString("", Instrument.GUITAR, Category.COMMON, "D2"))
        val guitarOpen = TuningEntry.InstrumentTuning(Tuning.fromString("", Instrument.GUITAR, Category.OPEN, "E2"))
        val bassCommon = TuningEntry.InstrumentTuning(Tuning.fromString("", Instrument.BASS, Category.COMMON, "E2"))
        val bassPower = TuningEntry.InstrumentTuning(Tuning.fromString("", Instrument.BASS, Category.POWER, "E2"))

        val tunings = listOf(bassPower, guitarCommon, guitarOpen, guitarCommon2, bassCommon)

        val expectedGroups = mapOf<Pair<Instrument, Category?>, List<TuningEntry>>(
            (Instrument.GUITAR to Category.COMMON) to listOf(guitarCommon, guitarCommon2),
            (Instrument.GUITAR to Category.OPEN) to listOf(guitarOpen),
            (Instrument.BASS to Category.COMMON) to listOf(bassCommon),
            (Instrument.BASS to Category.POWER) to listOf(bassPower)
        )

        val grouped = with (TuningList) { tunings.groupAndSort() }

        assertEquals(expectedGroups, grouped)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCurrentSaved() {
        // Starts the cold flow
        testScope.backgroundScope.launch {
            tuningList.currentSaved.collect {}
        }
        testScope.advanceUntilIdle()

        // Test default value.
        assertFalse(tuningList.currentSaved.value)

        val new = Tuning.fromString("E2")

        // Test not in list.
        tuningList.setCurrent(TuningEntry.InstrumentTuning(new))
        testScope.advanceUntilIdle()
        assertFalse(tuningList.currentSaved.value)

        // Test equiv in custom.
        val named = tuningList.addCustom("Saved", new)
        testScope.advanceUntilIdle()
        assertTrue(tuningList.currentSaved.value)

        // Test remove custom.
        tuningList.removeCustom(named)
        testScope.advanceUntilIdle()
        assertFalse(tuningList.currentSaved.value)

        // Test equiv in built-in.
        tuningList.setCurrent(TuningEntry.InstrumentTuning(Tuning.DROP_D))
        testScope.advanceUntilIdle()
        assertTrue(tuningList.currentSaved.value)

        // Test chromatic.
        tuningList.setCurrent(TuningEntry.ChromaticTuning)
        testScope.advanceUntilIdle()
        assertTrue(tuningList.currentSaved.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testIsFavourite() {
        tuningList.run {
            // Test default value.
            assertTrue(TuningEntry.InstrumentTuning(Tuning.STANDARD).isFavourite())
            assertTrue(TuningEntry.ChromaticTuning.isFavourite())
            assertFalse(TuningEntry.InstrumentTuning(Tuning.DROP_D).isFavourite())

            // Test set fav
            setFavourited(TuningEntry.InstrumentTuning(Tuning.DROP_D), true)
            testScope.advanceUntilIdle()
            assertTrue(TuningEntry.InstrumentTuning(Tuning.DROP_D).isFavourite())

            // Test set unfav
            setFavourited(TuningEntry.InstrumentTuning(Tuning.DROP_D), false)
            testScope.advanceUntilIdle()
            assertFalse(TuningEntry.InstrumentTuning(Tuning.DROP_D).isFavourite())
            setFavourited(TuningEntry.InstrumentTuning(Tuning.STANDARD), false)
            testScope.advanceUntilIdle()
            assertFalse(TuningEntry.InstrumentTuning(Tuning.STANDARD).isFavourite())
            setFavourited(TuningEntry.ChromaticTuning, false)
            testScope.advanceUntilIdle()
            assertFalse(TuningEntry.ChromaticTuning.isFavourite())
        }
    }

    @Test
    fun testGetCanonicalName() {
        with (tuningList) {
            // Test built in
            assertEquals(Tunings.HALF_STEP_DOWN.name, getCanonicalName(TuningEntry.InstrumentTuning(Tuning.STANDARD.lowerTuning())))

            // Test custom with name
            val new = Tuning.fromString("E2")
            assertEquals("E", getCanonicalName(TuningEntry.InstrumentTuning(new)))
            val named = addCustom("Named", new)
            assertEquals("Named", getCanonicalName(TuningEntry.InstrumentTuning(new)))
            removeCustom(named)
            assertEquals("E", getCanonicalName(TuningEntry.InstrumentTuning(new)))
        }
    }
}