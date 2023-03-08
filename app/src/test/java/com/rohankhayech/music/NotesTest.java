/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.music;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test harness for the Notes class.
 *
 * @author Rohan Khayech
 */
public final class NotesTest {

    @Test
    public void testGetPitch() {
        // Test valid notes.
        Assert.assertEquals("Incorrect pitch.", 440.0, Notes.getPitch(0), 0.001); //A4
        assertEquals("Incorrect pitch.", 82.407, Notes.getPitch(-29), 0.001); //E2
    }

    @Test
    public void testGetIndexFromPitch() {
        // Test valid pitches.
        assertEquals("Incorrect note index.", 0, Notes.getIndex(440)); //A4
        assertEquals("Incorrect note index.", -29, Notes.getIndex(82.41)); //E2

        // Test close pitch
        assertEquals("Incorrect note index.", 0, Notes.getIndex(441)); //A4
    }

    @Test
    public void testGetOffsetFromA4() {
        assertEquals("Incorrect note index.", -28.999, Notes.getOffsetFromA4(82.41), 0.001); //E2
    }

    @Test
    public void testGetIndexFromNote() {
        // Test valid notes.
        assertEquals("Incorrect note index.", 0, Notes.getIndex("A4"));
        assertEquals("Incorrect note index.", -29, Notes.getIndex("E2"));
        assertEquals("Incorrect note index.", -11, Notes.getIndex("A#3"));

        // Test invalid symbols.
        assertThrows("Does not fail on invalid note symbol.", IllegalArgumentException.class, ()->Notes.getIndex("Eb2")); // Flat notation
        assertThrows("Does not fail on invalid note symbol.", IllegalArgumentException.class, ()->Notes.getIndex("E2vd")); // Too long
        assertThrows("Does not fail on invalid note symbol.", IllegalArgumentException.class, ()->Notes.getIndex("E")); // Too short
        assertThrows("Does not fail on invalid note symbol.", IllegalArgumentException.class, ()->Notes.getIndex("H2")); // Invalid letter
        assertThrows("Does not fail on invalid note symbol.", IllegalArgumentException.class, ()->Notes.getIndex("D#x")); // Invalid number
        assertThrows("Does not fail on invalid note symbol.", IllegalArgumentException.class, ()->Notes.getIndex("")); // Empty string
        assertThrows("Does not fail on invalid note symbol.", NullPointerException.class, ()->Notes.getIndex(null)); // Null string
    }

    @Test
    public void getSymbol() {
        assertEquals("Incorrect pitch.", "A4", Notes.getSymbol(0));
        assertEquals("Incorrect pitch.", "E2", Notes.getSymbol(-29));
        assertEquals("Incorrect pitch.", "C#5", Notes.getSymbol(4));
    }
}