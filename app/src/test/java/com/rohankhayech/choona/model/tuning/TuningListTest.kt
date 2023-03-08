package com.rohankhayech.choona.model.tuning

import com.rohankhayech.music.Tuning
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

    @Before
    fun setUp() {
        tuningList = TuningList()
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
        val named = Tuning.fromString("New", "E2")
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