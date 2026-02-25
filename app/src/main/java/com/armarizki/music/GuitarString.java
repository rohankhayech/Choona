
/*
 * ChromaticTuner - Arma Rizki
 *
 * Includes modified source code from Choona Guitar Tuner
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

package com.armarizki.music;

import androidx.compose.runtime.Immutable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;

@Immutable
public final class GuitarString implements Iterable<Double>, Comparable<GuitarString> {

    private static final Map<String, GuitarString> cache = new HashMap<>();

    public static final GuitarString D2 = fromRootNote("D2",()-> -31);
    public static final GuitarString E2 = fromRootNote("E2", ()-> -29);
    public static final GuitarString A2 = fromRootNote("A2", ()-> -24);
    public static final GuitarString D3 = fromRootNote("D3",()-> -19);
    public static final GuitarString G3 = fromRootNote("G3", ()-> -14);
    public static final GuitarString B3 = fromRootNote("B3", ()-> -10);
    public static final GuitarString E4 = fromRootNote("E4", ()-> -5);

    public static final int FRETS = 24;

    private final String root;
    private final int octave;
    private final int rootNoteIndex;

    private GuitarString(String rootNote, int rootNoteIndex) {
        this.root = Notes.getRootNote(rootNote);
        this.octave = Notes.getOctave(rootNote);
        this.rootNoteIndex = rootNoteIndex;
    }

    public String toString() {
        return root;
    }

    public String toFullString() {
        return root+octave;
    }

    public int getFret(double pitch) {
        int fret = Notes.getIndex(pitch) - rootNoteIndex;
        if (validFret(fret)) {
            return fret;
        } else {
            throw new NoSuchElementException("String "+toFullString()+" does not contain a note with the specified pitch "+pitch+".");
        }
    }

    public double getPitch(int fret) {
        if (validFret(fret)) {
            return Notes.getPitch(getNoteIndex(fret));
        } else {
            throw new IndexOutOfBoundsException("Specified fret is not contained on this string.");
        }
    }

    public boolean containsNote(double pitch) {
        try {
            getFret(pitch);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public int getRootNoteIndex() {
        return rootNoteIndex;
    }

    public int getNoteIndex(int fret) {
        if (!validFret(fret)) throw new IndexOutOfBoundsException("Specified fret is not contained on this string.");
        return rootNoteIndex+fret;
    }

    public GuitarString lowerString() {
        int lowerNoteIndex = rootNoteIndex-1;
        return GuitarString.fromRootNote(Notes.getSymbol(lowerNoteIndex), ()->lowerNoteIndex);
    }

    public GuitarString higherString() {
        int higherNoteIndex = rootNoteIndex+1;
        return GuitarString.fromRootNote(Notes.getSymbol(higherNoteIndex), ()->higherNoteIndex);
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

    @Override
    public Iterator<Double> iterator() {
        return new Iterator<Double>() {

            int fret = 0;

            @Override
            public boolean hasNext() {
                return fret<FRETS;
            }

            @Override
            public Double next() {
                double pitch = getPitch(fret);
                fret++;
                return pitch;
            }
        };
    }

    @Override
    public int compareTo(GuitarString o) {
        return this.rootNoteIndex - o.rootNoteIndex;
    }

    public static GuitarString fromRootNote(String rootNote) {
        return fromRootNote(rootNote, ()->Notes.getIndex(rootNote));
    }

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

    public static boolean validFret(int fret) {
        return fret >= 0 && fret <= FRETS;
    }
}
