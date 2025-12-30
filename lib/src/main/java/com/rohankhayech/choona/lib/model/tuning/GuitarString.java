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

package com.rohankhayech.choona.lib.model.tuning;

import androidx.compose.runtime.Immutable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The guitar string class represents a tuned guitar string. It provides methods to find the fret
 * and pitch of notes on a string, as well as definitions of commonly used tuned strings.
 * This class is immutable.
 *
 * @author Rohan Khayech
 */
@Immutable
public final class GuitarString implements Iterable<Double>, Comparable<GuitarString> {

    /** Cache used to store already constructed guitar string objects. */
    private static final Map<String, GuitarString> cache = new HashMap<>();

    // STANDARD STRINGS
    public static final GuitarString D2 = fromRootNote("D2",()-> -31);
    public static final GuitarString E2 = fromRootNote("E2", ()-> -29);
    public static final GuitarString A2 = fromRootNote("A2", ()-> -24);
    public static final GuitarString D3 = fromRootNote("D3",()-> -19);
    public static final GuitarString G3 = fromRootNote("G3", ()-> -14);
    public static final GuitarString B3 = fromRootNote("B3", ()-> -10);
    public static final GuitarString E4 = fromRootNote("E4", ()-> -5);

    /** The number of frets on a guitar string. */
    public static final int FRETS = 24;

    /** The root note of the string (eg. E for E2) */
    private final String root;
    /** The octave of the root note. (eg. 2 for E2) */
    private final int octave;
    /** The note index corresponding to the string's root note. */
    private final int rootNoteIndex;

    /**
     * Constructs a new guitar string.
     *
     * @param rootNote A string representing the root note of the string, containing a letter, an optional accidental and the octave (eg. "E2" or "A#4").
     * @param rootNoteIndex The note index corresponding to the string's root note.
     */
    private GuitarString(String rootNote, int rootNoteIndex) {
        this.root = Notes.getRootNote(rootNote);
        this.octave = Notes.getOctave(rootNote);
        this.rootNoteIndex = rootNoteIndex;
    }

    /**
     * @return The root note of the string, excluding the octave.
     */
    public String toString() {
        return root;
    }

    /**
     * @return The root note of the string, including the octave.
     */
    public String toFullString() {
        return root+octave;
    }

    /**
     * Returns the fret on this string corresponding to the note with the specified pitch.
     * @param pitch The pitch to find.
     * @return The corresponding fret on this string.
     * @throws NoSuchElementException If this string does not contain a note with the specified pitch.
     */
    public int getFret(double pitch) {
        int fret = Notes.getIndex(pitch) - rootNoteIndex;
        if (validFret(fret)) {
            return fret;
        } else {
            throw new NoSuchElementException("String "+toFullString()+" does not contain a note with the specified pitch "+pitch+".");
        }
    }

    /** @return The pitch of the note at the specified fret on this string.
     *  @throws IndexOutOfBoundsException If the specified fret is less than 0 or more than {@link GuitarString#FRETS}. */
    public double getPitch(int fret) {
        if (validFret(fret)) {
            return Notes.getPitch(getNoteIndex(fret));
        } else {
            throw new IndexOutOfBoundsException("Specified fret is not contained on this string.");
        }
    }

    /**
     * Checks if the note corresponding to the specified pitch exists on this string.
     * @param pitch The pitch of the note.
     * @return {@code true} if the note exists on this string.
     */
    public boolean containsNote(double pitch) {
        try {
            getFret(pitch);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /** @return The index of the root note of this string. */
    public int getRootNoteIndex() {
        return rootNoteIndex;
    }

    /**
     * Returns the index of the note corresponding to the specified fret on this string.
     * @param fret The fret on this string.
     * @return The corresponding note index.
     * @throws IndexOutOfBoundsException If the specified fret is less than 0 or more than FRETS.
     */
    public int getNoteIndex(int fret) {
        if (!validFret(fret)) throw new IndexOutOfBoundsException("Specified fret is not contained on this string.");
        return rootNoteIndex+fret;
    }

    /** @return A guitar string with a root note one semitone lower than this string. */
    public GuitarString lowerString() {
        int lowerNoteIndex = rootNoteIndex-1;
        return GuitarString.fromRootNoteIndex(lowerNoteIndex);
    }

    /** @return A guitar string with a root note one semitone lower than this string. */
    public GuitarString higherString() {
        int higherNoteIndex = rootNoteIndex+1;
        return GuitarString.fromRootNoteIndex(higherNoteIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof GuitarString)) return false;

        GuitarString o = (GuitarString)obj;
        return rootNoteIndex == o.rootNoteIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rootNoteIndex);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Double> iterator() {
        return new Iterator<>() {

            int fret = 0;

            @Override
            public boolean hasNext() {
                return fret < FRETS;
            }

            @Override
            public Double next() {
                double pitch = getPitch(fret);
                fret++;
                return pitch;
            }
        };
    }

    /**
     * Compares this guitar string with the specified string for order.  Returns a
     * negative integer, zero, or a positive integer as this string is less
     * than, equal to, or greater than the specified object.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this string
     *     is less than, equal to, or greater than the specified string.
     * @throws NullPointerException if the specified object is null.
     */
    @Override
    public int compareTo(GuitarString o) {
        return this.rootNoteIndex - o.rootNoteIndex;
    }

    /**
     * Returns a guitar string with the specified root note.
     * @param noteIndex The index of the root note.
     * @return The corresponding guitar string.
     */
    public static GuitarString fromRootNoteIndex(int noteIndex) {
        return fromRootNote(Notes.getSymbol(noteIndex), ()-> noteIndex );
    }

    /**
     * Returns a guitar string with the specified root note.
     * @param rootNote A string representing the root note of the string, containing a letter, an optional accidental and the octave (eg. "E2" or "A#4").
     * @return The corresponding guitar string.
     * @throws IllegalArgumentException If the note symbol string does not represent a valid note.
     * @throws NullPointerException If the root note string is null.
     */
    public static GuitarString fromRootNote(String rootNote) {
        return fromRootNote(rootNote, ()->Notes.getIndex(rootNote));
    }

    /**
     * Returns a guitar string with the specified root note.
     * Used to skip note index calculation for constructing statically defined standard strings.
     * @param rootNote A string representing the root note of the string, containing a letter, an optional accidental and the octave (eg. "E2" or "A#4").
     * @param rootNoteIndex Supplier for the index of the root note of the string. Called only if the string is note already in the cache.
     * @return The corresponding guitar string.
     * @throws IllegalArgumentException If the note symbol string does not represent a valid note.
     * @throws NullPointerException If the root note string or index supplier are null.
     */
    private static GuitarString fromRootNote(String rootNote, Supplier<Integer> rootNoteIndex) {
        Objects.requireNonNull(rootNote, "Root note string cannot be null.");
        Objects.requireNonNull(rootNoteIndex, "Root note index supplier cannot be null.");
        // Retrieve existing guitar string from cache if already exists.
        GuitarString gs = cache.get(rootNote);
        if (gs == null) {
            // Otherwise create the new object and add to cache.
            gs = new GuitarString(rootNote, rootNoteIndex.get());
            cache.put(rootNote, gs);
        }

        return gs;
    }

    /**
     * Checks whether the fret is valid.
     * @param fret The fret to check.
     * @return True if less than 0 and false if >= {@link GuitarString#FRETS}.
     */
    public static boolean validFret(int fret) {
        return fret >= 0 && fret <= FRETS;
    }
}
