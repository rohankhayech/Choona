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

package com.rohankhayech.music;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The notes class stores pitch values for all the guitar notes recognised by the application.
 * It provides methods for finding the closest note corresponding to a certain pitch.
 *
 * @implNote Many methods return or take a {@code noteIndex} value, which corresponds to a stored pitch value.
 * This value should only be used during the program's execution as the pitch values corresponding to
 * each index may change as values are added/removed. Notes stored externally for the purposes of
 * save/load functionality should store the value as the corresponding pitch to ensure it keeps the
 * same pitch value.
 *
 * @author Rohan Khayech
 */
public final class Notes {

    /** The frequency of note A4 in Hz. */
    public static final double A4_PITCH = 440;

    /** The MIDI number of note A4. */
    public static final int A4_MIDI_NOTE_NUMBER = 69;

    private static final List<String> NOTE_SYMBOLS = Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");

    /**
     * Returns the pitch of the specified note.
     * @param noteIndex The index of the note, defined by the number of semitones from note A4 to the specified note.
     * @return The pitch of the specified note, in Hz.
     */
    public static double getPitch(int noteIndex) {
        if (noteIndex == 0) return A4_PITCH;
        return Math.pow(2, noteIndex/12.0) * A4_PITCH;
    }

    /**
     * Finds the closest note corresponding to the given pitch.
     * @param pitch The pitch to find the note index of.
     * @return The index of the corresponding note, defined by the number of semitones from note A4 to the corresponding note.
     */
    public static int getIndex(double pitch) {
        return (int)Math.round(getOffsetFromA4(pitch));
    }

    /**
     * Finds the offset from note A4 to the specified note.
     * @param pitch The pitch to find the offset of.
     * @return The exact offset of the corresponding note, defined by the number of semitones from note A4 to the corresponding note.
     */
    public static double getOffsetFromA4(double pitch) {
        return 12*(Math.log(pitch/A4_PITCH)/Math.log(2));
    }

    /**
     * Returns the index of the note represented by the given string.
     * @param note A string representing the note, containing a letter, an optional sharp symbol and the octave (eg. "E2" or "A#4").
     * @return The index of the specified note, defined by the number of semitones from note A4 to the specified note.
     * @throws IllegalArgumentException If the note symbol string does not represent a valid note.
     * @throws NullPointerException If the note symbol string is null.
     */
    public static int getIndex(String note) {
        Objects.requireNonNull(note, "Note symbol cannot be null.");
        int len = note.length();
        if (len < 2 || len > 3) throw new IllegalArgumentException("Invalid note symbol.");

        int offset = getOffsetWithinOctave(note.substring(0, len-1));
        try {
            int octave = Integer.parseInt(note.substring(len - 1));
            return (octave-4)*12 + offset;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid note symbol.", e);
        }


    }

    /**
     * Returns the offset of the note within the octave.
     * @param noteSymbol The symbol representing the note, including a letter and an optional sharp symbol (eg. "E or A#").
     * @return The offset of the note within the octave.
     * @throws IllegalArgumentException If the note symbol string does not represent a valid note.
     */
    private static int getOffsetWithinOctave(String noteSymbol) {
        int len = noteSymbol.length();
        if (len < 1 || len > 2) throw new IllegalArgumentException("Invalid note symbol.");

        int letterIndex = NOTE_SYMBOLS.indexOf(noteSymbol);
        if (letterIndex != -1) {
            return letterIndex - 9;
        } else throw new IllegalArgumentException("Invalid note symbol.");
    }

    /**
     * Returns the note symbol representing the note with the specified index.
     * @param noteIndex The index of the note, defined by the number of semitones from note A4 to the specified note.
     * @return The note symbol representing the note with the specified index.
     */
    public static String getSymbol(int noteIndex) {
        int octave = 4 + Math.floorDiv(noteIndex+9, 12);
        return NOTE_SYMBOLS.get(Math.floorMod(noteIndex+9, 12)) + octave;
    }
}
