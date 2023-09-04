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

import com.rohankhayech.music.Tuning
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert
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
        tuningList = TuningList()
    }

    @Test
    fun testConstructor() {
        val tl = TuningList(Tuning.STANDARD)
        Assert.assertSame(Tuning.STANDARD, tl.current.value)
    }

    @Test
    fun testCurrent() {
        // Test default value.
        Assert.assertNull(tuningList.current.value)

        // Test not in list.
        var new = Tuning.fromString("E2")
        tuningList.setCurrent(new)
        Assert.assertSame(new, tuningList.current.value)

        // Test equiv in list.
        new = Tuning.fromString("E4 B3 G3 D3 A2 E2")
        tuningList.setCurrent(new)
        Assert.assertSame(Tuning.STANDARD, tuningList.current.value)
    }

    @Test
    fun testFavourites() {
        // Test default value.
        Assert.assertEquals(setOf(Tuning.STANDARD), tuningList.favourites.value)

        // Test set fav
        tuningList.setFavourited(Tuning.DROP_D, true)
        Assert.assertEquals(setOf(Tuning.STANDARD, Tuning.DROP_D), tuningList.favourites.value)

        // Test set unfav
        tuningList.setFavourited(Tuning.DROP_D, false)
        Assert.assertEquals(setOf(Tuning.STANDARD), tuningList.favourites.value)
    }

    @Test
    fun testCustom() {
        // Test default value.
        Assert.assertEquals(emptySet<Tuning>(), tuningList.custom.value)

        // Setup
        tuningList.setCurrent(Tuning.STANDARD)

        // Test add custom.
        val new = Tuning.fromString("E2")
        val named = Tuning.fromString("New", Tuning.DEFAULT_INSTRUMENT, "E2")
        tuningList.addCustom("New", new)
        Assert.assertEquals(setOf(named), tuningList.custom.value)
        Assert.assertEquals(Tuning.STANDARD, tuningList.current.value)
        tuningList.removeCustom(named)

        // Test add custom with equiv current
        tuningList.setCurrent(new)
        tuningList.addCustom("New", new)
        Assert.assertEquals(named, tuningList.current.value)

        // Test remove custom
        tuningList.setFavourited(named, true)
        tuningList.removeCustom(named)
        Assert.assertEquals(emptySet<Tuning>(), tuningList.custom.value)
        Assert.assertEquals(setOf(Tuning.STANDARD), tuningList.favourites.value)
        Assert.assertEquals(new, tuningList.current.value)
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
        Assert.assertEquals(deleted.size, 0)

        // Delete tunings.
        tuningList.removeCustom(new)
        tuningList.removeCustom(new2)
        Assert.assertEquals(deleted[0], new)
        Assert.assertEquals(deleted[1], new2)
        Assert.assertEquals(deleted.size, 2)
    }

    @Test
    fun testEquals() {
        val newList = TuningList()
        Assert.assertEquals(tuningList, newList)

        newList.setCurrent(Tuning.STANDARD)
        Assert.assertNotEquals(tuningList, newList)
    }

    @Test
    fun testHashCode() {
        val equal = TuningList()
        Assert.assertEquals(equal.hashCode(), tuningList.hashCode())
    }
}