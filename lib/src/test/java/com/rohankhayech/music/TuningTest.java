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

package com.rohankhayech.music;

import static com.rohankhayech.music.Tuning.DEFAULT_INSTRUMENT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Test harness for the Tuning class.
 *
 * @author Rohan Khayech
 */
public final class TuningTest {

    private final Tuning tuning = Tuning.STANDARD;

    @Test
    public void testConstructor() {
        // Check input validation
        assertThrows(NullPointerException.class, ()->new Tuning(DEFAULT_INSTRUMENT, GuitarString.E2, null)); // Element is null.
        assertThrows(NullPointerException.class, ()->new Tuning(DEFAULT_INSTRUMENT, (GuitarString[])null)); // Array is null.
        assertThrows(NullPointerException.class, ()->new Tuning((Instrument)null, GuitarString.E2)); // Instrument is null.
        assertFalse(new Tuning(null, DEFAULT_INSTRUMENT, Tuning.Category.COMMON, GuitarString.E2).hasName()); // Name is null.
        assertNull(new Tuning(null, DEFAULT_INSTRUMENT, null, GuitarString.E2).getCategory()); // Category is null.

        // Test copy
        assertEquals(tuning, new Tuning("Standard", tuning));
        assertEquals("Tuning", new Tuning("Tuning", tuning).getName());
        assertThrows(NullPointerException.class, ()->new Tuning("Name", null)); // Other is null
    }

    @Test
    public void testNumStrings() {
        assertEquals("Returns incorrect num of strings.", 6, tuning.numStrings());
    }

    // @noinspection ResultOfMethodCallIgnored
    @Test
    public void testGetString() {
        // Test valid index
        assertEquals("Returns incorrect string.", GuitarString.E4, tuning.getString(0));
        assertEquals("Returns incorrect string.", GuitarString.E2, tuning.getString(5));

        // Test invalid index
        assertThrows("Returns a string from invalid index.", IndexOutOfBoundsException.class, ()->tuning.getString(-1));
        assertThrows("Returns a string from invalid index.", IndexOutOfBoundsException.class, ()->tuning.getString(6));
    }

    @Test
    public void testGetStringNum() {
        // Check valid strings.
        assertEquals("Returns incorrect string num.",0, tuning.getStringNum(GuitarString.E4));
        assertEquals("Returns incorrect string num.",5, tuning.getStringNum(GuitarString.E2));

        // Check invalid string.
        assertThrows("Returns string num for non-existing string", NoSuchElementException.class, ()->tuning.getStringNum(
            GuitarString.D2));
    }

    @Test
    public void testGetStrings() {
        // Check list returned is correct.
        List<GuitarString> strings = tuning.getStrings();
        assertEquals("List of strings has incorrect size.", 6, strings.size());
        assertEquals("Returns incorrect string in list.", GuitarString.E4, strings.get(0));
        assertEquals("Returns incorrect string in list.", GuitarString.E2, strings.get(5));

        // Check unmodifiable.
        assertThrows("Returned list is modifiable.", UnsupportedOperationException.class, strings::clear);
    }

    @Test
    public void testGetStringsContaining() {
        List<GuitarString> strings = tuning.getStringsContaining(110.00);
        assertEquals("List does not contain expected number of strings.", 2, strings.size());
        assertTrue("List does not contain expected string.", strings.contains(GuitarString.E2));
        assertTrue("List does not contain expected string.", strings.contains(GuitarString.A2));
    }

    @Test
    public void testToString() {
        assertEquals("EADGBE", tuning.toString());
    }

    @Test
    public void testToFullString() {
        assertEquals("E4 B3 G3 D3 A2 E2", tuning.toFullString());
    }

    @Test
    public void testHasName() {
        assertTrue("Returned false with name.", tuning.hasName());

        Tuning t = new Tuning(GuitarString.E2);
        assertFalse("Returned true without name.", t.hasName());
    }

    @Test
    public void testHasCategory() {
        assertTrue("Returned false with category.", tuning.hasCategory());

        Tuning t = new Tuning(GuitarString.E2);
        assertFalse("Returned true without category.", t.hasCategory());
    }

    @Test
    public void testGetName() {
        assertEquals("Standard", tuning.getName());

        GuitarString[] strings = new GuitarString[6];
        Arrays.fill(strings, GuitarString.E2);
        Tuning t = new Tuning(strings);
        assertEquals(t.toString(), t.getName());
    }

    @Test
    public void testGetFullName() {
        assertEquals("Standard (EADGBE)", tuning.getFullName());

        GuitarString[] strings = new GuitarString[6];
        Arrays.fill(strings, GuitarString.E2);
        Tuning t = new Tuning(strings);
        assertEquals(t.toString(), t.getFullName());
    }

    @Test
    public void testGetInstrument() {
        assertEquals(Instrument.GUITAR, tuning.getInstrument());
    }

    @Test
    public void testGetCategory() {
        assertEquals(Tuning.Category.COMMON, tuning.getCategory());
    }

    @Test
    public void testFromString() {
        // Check new tuning.
        GuitarString[] expectedStrings = new GuitarString[6];
        Arrays.fill(expectedStrings, GuitarString.E2);

        // Check unnamed.
        Tuning newTuning = Tuning.fromString("E2 E2 E2 E2 E2 E2");
        assertEquals("Did not return correct tuning.", new Tuning(expectedStrings), newTuning);
        assertEquals("Set name.", "EEEEEE", newTuning.getName());
        assertNull("Set category.", newTuning.getCategory());

        // Check named and categorised.
        newTuning = Tuning.fromString("Tuning", Instrument.GUITAR, Tuning.Category.COMMON, "E2 E2 E2 E2 E2 E2");
        assertEquals("Did not return correct tuning.", new Tuning("Tuning", Instrument.GUITAR, Tuning.Category.COMMON, expectedStrings), newTuning);

        // Check invalid format.
        assertThrows("Created tuning from invalid tuning string.", IllegalArgumentException.class, ()->Tuning.fromString("E4B4G3D3A2E2"));
    }

    @Test
    public void testLowerTuning() {
        assertEquals(
            new Tuning(
                GuitarString.E4.lowerString(),
                GuitarString.B3.lowerString(),
                GuitarString.G3.lowerString(),
                GuitarString.D3.lowerString(),
                GuitarString.A2.lowerString(),
                GuitarString.E2.lowerString()
            ),
            tuning.lowerTuning()
        );
    }

    @Test
    public void testHigherTuning() {
        assertEquals(
            new Tuning(
                GuitarString.E4.higherString(),
                GuitarString.B3.higherString(),
                GuitarString.G3.higherString(),
                GuitarString.D3.higherString(),
                GuitarString.A2.higherString(),
                GuitarString.E2.higherString()
            ),
            tuning.higherTuning()
        );
    }

    @Test
    public void testWithString() {
        // Test valid
        assertEquals(
            new Tuning(
                GuitarString.D2,
                GuitarString.B3,
                GuitarString.G3,
                GuitarString.D3,
                GuitarString.A2,
                GuitarString.E2
            ),
            tuning.withString(0, GuitarString.D2)
        );
        assertTrue(Tuning.DROP_D.equivalentTo(tuning.withString(5, GuitarString.D2)));

        // Test invalid
        assertThrows(IndexOutOfBoundsException.class, ()->tuning.withString(-1, GuitarString.E2));
        assertThrows(IndexOutOfBoundsException.class, ()->tuning.withString(6, GuitarString.E2));
        assertThrows(NullPointerException.class, ()->tuning.withString(0, null));
    }

    @Test
    public void testIterator() {
        // Check iterator
        Iterator<GuitarString> iter = tuning.iterator();

        // Check first value.
        GuitarString firstString = iter.next();
        assertEquals("Iterator returning incorrect values.", GuitarString.E4, firstString);

        // Check enough values.
        try {
            for (int i = 1; i < tuning.numStrings(); i++) {
                iter.next();
            }
        } catch (NoSuchElementException e) {
            throw new AssertionError("Iterator does not provide all values.", e);
        }

        // Check no more values.
        assertFalse("Iterator has too many values.", iter.hasNext());
    }

    @Test
    public void testEquals() {
        // Check same are equal
        assertEquals("Same objects are not equal.", Tuning.STANDARD, tuning);

        // Check equality.
        Tuning standard = new Tuning("Standard", Instrument.GUITAR, Tuning.Category.COMMON, GuitarString.E4, GuitarString.B3, GuitarString.G3, GuitarString.D3, GuitarString.A2, GuitarString.E2);
        assertEquals("Tunings were not equal.", Tuning.STANDARD, standard);
        Tuning eadgbe = new Tuning(Instrument.GUITAR, GuitarString.E4, GuitarString.B3, GuitarString.G3, GuitarString.D3, GuitarString.A2, GuitarString.E2);
        assertNotEquals("", eadgbe, Tuning.STANDARD);
        Tuning bass = new Tuning("Standard", Instrument.BASS, Tuning.Category.COMMON, GuitarString.E4, GuitarString.B3, GuitarString.G3, GuitarString.D3, GuitarString.A2, GuitarString.E2);
        assertNotEquals("", bass, Tuning.STANDARD);
        Tuning diffCat = new Tuning("Standard", Instrument.GUITAR, null, GuitarString.E4, GuitarString.B3, GuitarString.G3, GuitarString.D3, GuitarString.A2, GuitarString.E2);
        assertNotEquals("", diffCat, Tuning.STANDARD);
    }

    @Test
    public void testEquivalentTo() {
        // Check same are equivalent
        assertTrue("Same objects are not equal.", Tuning.STANDARD.equivalentTo(tuning));

        // Check equivalence.
        Tuning standard = new Tuning(GuitarString.E4, GuitarString.B3, GuitarString.G3, GuitarString.D3, GuitarString.A2, GuitarString.E2);
        assertTrue("Tunings were not equivalent.", Tuning.STANDARD.equivalentTo(standard));
        assertFalse("Different tunings were equivalent.", Tuning.DROP_D.equivalentTo(Tuning.STANDARD));
        Tuning bass = new Tuning(Instrument.BASS, GuitarString.E4, GuitarString.B3, GuitarString.G3, GuitarString.D3, GuitarString.A2, GuitarString.E2);
        assertFalse("Different instrument tunings were equivalent.", bass.equivalentTo(Tuning.STANDARD));
    }

    @Test
    public void testHasEquivalentIn() {
        Tuning standard = new Tuning(GuitarString.E4, GuitarString.B3, GuitarString.G3, GuitarString.D3, GuitarString.A2, GuitarString.E2);

        // Check containing
        List<Tuning> list = new ArrayList<>();
        list.add(Tuning.DROP_D);
        list.add(Tuning.STANDARD);
        assertTrue("Returned false for list containing equivalent.", standard.hasEquivalentIn(list));

        // Check not containing
        list = new ArrayList<>();
        list.add(Tuning.DROP_D);
        assertFalse("Returned true for list not containing equivalent.", standard.hasEquivalentIn(list));

        // Check null
        assertThrows(NullPointerException.class, ()->standard.hasEquivalentIn(null));
    }

    @Test
    public void testFindEquivalentIn() {
        Tuning standard = new Tuning(GuitarString.E4, GuitarString.B3, GuitarString.G3, GuitarString.D3, GuitarString.A2, GuitarString.E2);

        // Check containing
        List<Tuning> list = new ArrayList<>();
        list.add(Tuning.DROP_D);
        list.add(Tuning.STANDARD);
        assertSame("Did not return correct tuning for list containing equivalent.", Tuning.STANDARD, standard.findEquivalentIn(list));

        // Check not containing
        list = new ArrayList<>();
        list.add(Tuning.DROP_D);
        assertNull("Returned tuning for list containing equivalent.", standard.findEquivalentIn(list));

        // Check null
        assertThrows(NullPointerException.class, ()->standard.findEquivalentIn(null));
    }

    @Test
    public void testHashCode() {
        // Check equal tunings have same hashcode.
        assertEquals("Equal tunings have different hashcodes", tuning.hashCode(), Tuning.STANDARD.hashCode());
    }
}