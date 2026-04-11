/*
 * Choona - Guitar Tuner
 * Copyright (C) 2026 Rohan Khayech
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.runtime.Immutable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The tuning class stores a set of tuned strings on a guitar or other stringed instrument.
 * It also provides commonly used 6-string tunings for convenience.
 * This class is immutable.
 *
 * @author Rohan Khayech
 */
@Immutable
public final class InstrumentTuning extends Tuning implements Iterable<GuitarString> {

    /** The set of strings used for this tuning. */
    private final List<GuitarString> strings;

    /** The instrument the tuning is for. */
    private final Instrument instrument;

    /** The default instrument for tunings. */
    public static final Instrument DEFAULT_INSTRUMENT = Instrument.GUITAR;

    /**
     * @return The number of strings in this tuning.
     */
    public int numStrings() {
        return strings.size();
    }

    /**
     * Constructs a new guitar tuning.
     * @param strings The guitar strings to include in this tuning, from high to low. (eg. EBGDAE)
     * @throws NullPointerException If the specified array of strings or any string is null.
     */
    public InstrumentTuning(GuitarString... strings) {
        this(DEFAULT_INSTRUMENT, strings);
    }

    /**
     * Constructs a new guitar tuning.
     * @param instrument The instrument the tuning is for.
     * @param strings The guitar strings to include in this tuning, from high to low. (eg. EBGDAE)
     * @throws NullPointerException If the specified instrument, array of strings or any string is null.
     */
    public InstrumentTuning(Instrument instrument, GuitarString... strings) {
        this(null, instrument, null, strings);
    }

    /**
     * Constructs a new guitar tuning.
     * @param name Name of the tuning. Can be null.
     * @param instrument The instrument the tuning is for.
     * @param category The category of tuning.
     * @param strings The guitar strings to include in this tuning, from high to low. (eg. EBGDAE)
     * @throws NullPointerException If the specified instrument, array of strings or any string is null.
     */
    public InstrumentTuning(String name, Instrument instrument, Category category, GuitarString... strings) {
        this(name, instrument, category, Arrays.asList(
            Objects.requireNonNull(strings, "Array of strings cannot be null.")
        ));
    }

    /**
     * Constructs a new guitar tuning.
     * @param instrument The instrument the tuning is for.
     * @param strings The guitar strings to include in this tuning, from high to low. (eg. EBGDAE)
     * @throws NullPointerException If the specified list of strings or any string is null.
     */
    public InstrumentTuning(Instrument instrument, List<GuitarString> strings) {
        this(null, instrument, null, strings);
    }

    /**
     * Constructs a copy of the specified tuning with the specified name.
     * @param name The name of the tuning.
     * @param o The tuning to copy.
     */
    public InstrumentTuning(String name, @NonNull InstrumentTuning o) {
        super(name, Objects.requireNonNull(o).category);
        this.strings = o.strings;
        this.instrument = o.instrument;
    }

    /**
     * Constructs a new guitar tuning.
     * @param name Name of the tuning. Can be null.
     * @param instrument The instrument the tuning is for.
     * @param category The category of tuning.
     * @param strings The guitar strings to include in this tuning, from high to low. (eg. EBGDAE)
     * @throws NullPointerException If the specified list of strings or any string is null.
     */
    public InstrumentTuning(String name, Instrument instrument, Category category, List<GuitarString> strings) {
        super(name, category);

        // Check list of strings is not null.
        Objects.requireNonNull(strings, "List of strings cannot be null.");
        Objects.requireNonNull(instrument, "Instrument cannot be null.");

        // Check that no strings are null.
        if (strings.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("Strings cannot be null.");
        }

        // Initialise tuning.
        this.strings = new ArrayList<>(strings);
        this.instrument = instrument;
    }

    /**
     * Returns the nth string from this tuning.
     * @param n The string number to return.
     * @return The nth guitar string from this tuning.
     * @throws IndexOutOfBoundsException If the string number is invalid.
     */
    public GuitarString getString(int n) {
        return strings.get(n);
    }

    /**
     * Returns the position of the first occurrence of the specified string in the tuning.
     * @param string The guitar string.
     * @return The position of the first occurrence of the string in the tuning.
     * @throws NoSuchElementException If the string is not contained in this tuning.
     */
    public int getStringNum(GuitarString string) {
        int stringNum = strings.indexOf(string);
        if (stringNum != -1) {
            return stringNum;
        } else {
            throw new NoSuchElementException("The specified string "+string.toFullString()+" is not present in this tuning.");
        }
    }

    /** @return The set of strings used for this tuning. */
    public List<GuitarString> getStrings() {
        return Collections.unmodifiableList(strings);
    }

    /**
     * Returns the set of strings in this tuning that contain the note corresponding to the specified pitch.
     * @param pitch The pitch of the note.
     * @return A list of all strings in this tuning that contain the specified note.
     */
    public List<GuitarString> getStringsContaining(double pitch) {
        return strings.stream().filter(s->s.containsNote(pitch)).collect(Collectors.toList());
    }

    /**
     * @return The instrument the tuning is for.
     */
    public Instrument getInstrument() {
        return instrument;
    }

    /**
     * @return A human-readable java string representation of the guitar strings in this tuning, excluding octaves.
     * Ordered from lowest to highest string.
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = strings.size()-1; i>=0; i--) {
            str.append(strings.get(i).toString());
        }
        return str.toString();
    }

    /**
     * @return A java string representation of the guitar strings in this tuning, including octaves.
     * Ordered from highest to lowest string.
     */
    public String toFullString() {
        StringBuilder str = new StringBuilder();
        for (GuitarString s : strings) {
            str.append(s.toFullString()).append(" ");
        }
        return str.toString().trim();
    }

    /** @return A tuning with all strings tuned one semitone lower than this tuning. */
    public InstrumentTuning lowerTuning() {
        return new InstrumentTuning(
            instrument,
            strings.stream()
                .map(GuitarString::lowerString)
                .collect(Collectors.toList())
        );
    }

    /** @return A tuning with all strings tuned one semitone higher than this tuning. */
    public InstrumentTuning higherTuning() {
        return new InstrumentTuning(
            instrument,
            strings.stream()
                .map(GuitarString::higherString)
                .collect(Collectors.toList())
        );
    }

    /**
     * Returns a copy of this tuning with the nth string replaced with the specified string.
     * @param n The string number to replace.
     * @param string The string to replace the nth string with.
     * @return A copy of this tuning with the nth string replaced with the specified string.
     * @throws IndexOutOfBoundsException If the string number is invalid.
     * @throws NullPointerException If the specified string is null.
     */
    public InstrumentTuning withString(int n, GuitarString string) {
        Objects.requireNonNull(string);
        List<GuitarString> newList = new ArrayList<>(strings);
        newList.set(n, string);
        return new InstrumentTuning(null, instrument, null, newList);
    }

    public @NonNull String getKey() {
        return instrument+"-["+toFullString()+"]";
    }

    /**
     * Creates a InstrumentTuning object from a java string containing the root notes of each guitar string
     * in the tuning.
     * @param tuningStr A java string containing the root notes of each guitar string
     *                  in the tuning, separated by spaces. This is the same format as
     *                  returned by {@code InstrumentTuning.toFullString()}.;
     * @return The corresponding guitar tuning.
     * @throws IllegalArgumentException If any of the root notes do not correspond to a defined standard string.
     */
    public static InstrumentTuning fromString(String tuningStr) {
        return fromString(null, DEFAULT_INSTRUMENT, null, tuningStr);
    }

    /**
     * Creates a InstrumentTuning object from a java string containing the root notes of each guitar string
     * in the tuning.
     * @param name Name of the tuning. Can be null.
     * @param instrument The instrument the tuning is for.
     * @param category The category of tuning.
     * @param tuningStr A java string containing the root notes of each guitar string
     *                  in the tuning, separated by spaces. This is the same format as
     *                  returned by {@code InstrumentTuning.toFullString()}.;
     * @return The corresponding guitar tuning.
     * @throws IllegalArgumentException If any of the root notes do not correspond to a defined standard string.
     */
    public static InstrumentTuning fromString(String name, Instrument instrument, Category category, String tuningStr) {
        // Construct new tuning otherwise.
        String[] rootNotes = tuningStr.split(" ");
        GuitarString[] strings = new GuitarString[rootNotes.length];
        for (int i = 0; i < rootNotes.length; i++) {
            strings[i] = GuitarString.fromRootNote(rootNotes[i]);
        }

        return new InstrumentTuning(name, instrument, category, strings);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<GuitarString> iterator() {
        return getStrings().iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof InstrumentTuning)) return false;

        InstrumentTuning o = (InstrumentTuning)obj;
        return super.equals(o)
            && strings.equals(o.strings)
            && instrument == o.instrument;
    }

    /**
     * Returns whether this tuning is equivalent to the specified tuning.
     * @param other The tuning to check equivalence with.
     * @return True if the other tuning has the same strings and instrument as this tuning, false otherwise.
     */
    @Override
    public boolean equivalentTo(@Nullable Tuning other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof InstrumentTuning)) return false;
        InstrumentTuning o = (InstrumentTuning)other;
        return strings.equals(o.strings) && instrument == o.instrument;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), strings, instrument);
    }
}