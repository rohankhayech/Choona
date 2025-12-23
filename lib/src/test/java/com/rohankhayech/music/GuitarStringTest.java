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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Test harness for the GuitarString class.
 *
 * @author Rohan Khayech
 */
public final class GuitarStringTest {

    private final GuitarString str = GuitarString.E2;

    @Test
    public void testToString() {
        assertEquals("Wrong root note returned.","E", str.toString());
    }

    @Test
    public void testToFullString() {
        assertEquals("Wrong root note returned.","E2", str.toFullString());
    }

    @Test
    public void testGetFret() {
        // Retrieves correct fret for valid pitch.
        assertEquals("Incorrect fret for valid pitch.",0, str.getFret(82.41));
        assertEquals("Incorrect fret for valid pitch.",24, str.getFret(329.63));

        // Check fails for invalid pitch.
        assertThrows("Returns a fret for ", NoSuchElementException.class, ()->str.getFret(77.0));
        assertThrows("Returns a fret for ", NoSuchElementException.class, ()->str.getFret(340.0));
    }

    @Test
    public void testGetPitch() {
        // Check retrieves correct pitch for valid frets.
        assertEquals("Incorrect pitch for valid fret.", 82.407, str.getPitch(0), 0.001);
        assertEquals("Incorrect pitch for valid fret.", 329.63, str.getPitch(24), 0.01);

        // Check fails for invalid fret.
        assertThrows("Returns a pitch for invalid fret.", IndexOutOfBoundsException.class, ()->str.getPitch(-1));
        assertThrows("Returns a pitch for invalid fret.", IndexOutOfBoundsException.class, ()->str.getPitch(GuitarString.FRETS+1));
    }

    @Test
    public void testContainsNote() {
        // Check when true.
        assertTrue("Returns false despite containing note.", str.containsNote(82.41));
        assertTrue("Returns false despite contains note.", str.containsNote(329.63));

        // Check when false.
        assertFalse("Returns true without containing note.", str.containsNote(77.0));
        assertFalse("Returns true without containing note.", str.containsNote(340.0));
    }

    @Test
    public void testLowerString() {
        assertEquals("Lower string is not 1st lower.",str.getRootNoteIndex()-1, str.lowerString().getRootNoteIndex());
    }

    @Test
    public void testHigherString() {
        assertEquals("Higher string is not 1st higher.",str.getRootNoteIndex()+1, str.higherString().getRootNoteIndex());
    }

    @Test
    public void testGetRootNoteIndex() {
        assertEquals("Incorrect note index.", -29, str.getRootNoteIndex());
    }

    @Test
    public void testGetNoteIndex() {
        // Check valid
        assertEquals("Incorrect note index.", str.getRootNoteIndex(), str.getNoteIndex(0));
        assertEquals("Incorrect note index.", str.getRootNoteIndex()+4, str.getNoteIndex(4));
        assertEquals("Incorrect note index.", str.getRootNoteIndex()+24, str.getNoteIndex(24));

        // Check invalid
        assertThrows("Didn't fail on invalid fret.", IndexOutOfBoundsException.class, ()->str.getNoteIndex(-1));
        assertThrows("Didn't fail on invalid fret.", IndexOutOfBoundsException.class, ()->str.getNoteIndex(25));
    }

    @Test
    public void testFromRootNote() {
        // Check for new string.
        GuitarString cs5 = GuitarString.fromRootNote("C#5");
        assertEquals("Constructed wrong string.", 4, cs5.getRootNoteIndex());

        // Check cache is used.
        assertSame("Existing object not cached or not used.", cs5, GuitarString.fromRootNote("C#5"));
        assertSame("Existing object not cached or not used.", GuitarString.E2, GuitarString.fromRootNote("E2"));

        assertThrows(NullPointerException.class, ()->GuitarString.fromRootNote(null));
    }

    @Test
    public void testEquals() {
        // Check same are equal.
        assertEquals("Same strings are not equal.", GuitarString.E2, str);

        // Check different are not equal.
        assertNotEquals("Different strings are equal.", GuitarString.E2, GuitarString.E4);
    }

    @Test
    public void testHashCode() {
        // Check equal objects have same hashcode.
        assertEquals("Equal objects have different hashcodes.", str.hashCode(), GuitarString.E2.hashCode());
    }

    @Test
    public void testIterator() {
        // Check iterator
        Iterator<Double> iter = str.iterator();

        // Check first value.
        double lastPitch = iter.next();
        assertEquals("Iterator returning incorrect values.", 82.407, lastPitch, 0.001);

        // Check enough values.
        try {
            for (int i = 1; i <= GuitarString.FRETS; i++) {
                iter.next();
            }
        } catch (NoSuchElementException e) {
            throw new AssertionError("Iterator does not provide all values.", e);
        }

        // Check no more values.
        assertFalse("Iterator has too many values.", iter.hasNext());
    }

    @Test
    public void testCompareTo() {
        // Test equal
        assertEquals("Equal comparison not returning 0.", 0, GuitarString.E2.compareTo(GuitarString.E2));
        // Test greater than
        assertEquals("A2 should be 5 greater than E2", 5,GuitarString.A2.compareTo(GuitarString.E2));
        // Test less than
        assertEquals("E2 should be 5 less than A2", -5,GuitarString.E2.compareTo(GuitarString.A2));
        // Test greater than
        assertEquals("A2 should be 5 greater than E2", 5,GuitarString.A2.compareTo(GuitarString.E2));

        // Test fail on null.
        assertThrows("Compared string to null string.", NullPointerException.class, ()->GuitarString.E2.compareTo(null));
    }

    @Test
    public void testValidFret() {
        // Test valid.
        assertTrue(GuitarString.validFret(0));
        assertTrue(GuitarString.validFret(12));
        assertTrue(GuitarString.validFret(GuitarString.FRETS));

        // Test invalid
        assertFalse(GuitarString.validFret(-1));
        assertFalse(GuitarString.validFret(GuitarString.FRETS+1));
    }
}