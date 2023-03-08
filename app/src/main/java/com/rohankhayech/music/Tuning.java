/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.music;

import androidx.compose.runtime.Immutable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The tuning class stores a set of tuned strings on a guitar. It also provides commonly used 6-string tunings for convenience.
 * This class is immutable.
 *
 * @author Rohan Khayech
 */
@Immutable
public final class Tuning implements Iterable<GuitarString> {

    /** The standard name of this tuning. */
    private final String name;

    /** The set of strings used for this tuning. */
    private final List<GuitarString> strings;

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
    public Tuning(GuitarString... strings) {
        this(null, strings);
    }

    /**
     * Constructs a new guitar tuning.
     * @param name Name of the tuning. Can be null.
     * @param strings The guitar strings to include in this tuning, from high to low. (eg. EBGDAE)
     * @throws NullPointerException If the specified array of strings or any string is null.
     */
    public Tuning(String name, GuitarString... strings) {
        this(name, Arrays.asList(
            Objects.requireNonNull(strings, "Array of strings cannot be null.")
        ));
    }

    /**
     * Constructs a new guitar tuning.
     * @param strings The guitar strings to include in this tuning, from high to low. (eg. EBGDAE)
     * @throws NullPointerException If the specified list of strings or any string is null.
     */
    public Tuning(List<GuitarString> strings) {
        this(null, strings);
    }

    /**
     * Constructs a copy of the specified tuning with the specified name.
     * @param name The name of the tuning.
     * @param o The tuning to copy.
     */
    public Tuning(String name, Tuning o) {
        Objects.requireNonNull(o);
        this.name = name;
        this.strings = o.strings;
    }

    /**
     * Constructs a new guitar tuning.
     * @param name Name of the tuning. Can be null.
     * @param strings The guitar strings to include in this tuning, from high to low. (eg. EBGDAE)
     * @throws NullPointerException If the specified list of strings or any string is null.
     */
    public Tuning(String name, List<GuitarString> strings) {
        // Check list of strings is not null.
        Objects.requireNonNull(strings, "List of strings cannot be null.");

        // Check that no strings are null.
        if (strings.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("Strings cannot be null.");
        }

        // Initialise tuning.
        this.strings = new ArrayList<>(strings);
        this.name = name;
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
            throw new NoSuchElementException("The specified string"+string.toFullString()+"is not present in this tuning.");
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

    /** @return True if this tuning is named, false otherwise. */
    public boolean hasName() {
        return name != null;
    }

    /**
     * @return The standard name of this tuning, or the string representation if it is not named.
     */
    public String getName() {
        return name != null ? name : toString();
    }

    /**
     * @return The standard name of this tuning (if named) including it's string representation.
     */
    public String getFullName() {
        return name != null ? name + " (" + this + ")" : toString();
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
    public Tuning lowerTuning() {
        return new Tuning(
            strings.stream()
                .map(GuitarString::lowerString)
                .collect(Collectors.toList())
        );
    }

    /** @return A tuning with all strings tuned one semitone higher than this tuning. */
    public Tuning higherTuning() {
        return new Tuning(
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
    public Tuning withString(int n, GuitarString string) {
        Objects.requireNonNull(string);
        List<GuitarString> newList = new ArrayList<>(strings);
        newList.set(n, string);
        return new Tuning(newList);
    }

    /**
     * Creates a Tuning object from a java string containing the root notes of each guitar string
     * in the tuning.
     * @param tuningStr A java string containing the root notes of each guitar string
     *                  in the tuning, separated by spaces. This is the same format as
     *                  returned by {@code Tuning.toFullString()}.;
     * @return The corresponding guitar tuning.
     * @throws IllegalArgumentException If any of the root notes do not correspond to a defined standard string.
     */
    public static Tuning fromString(String tuningStr) {
        return fromString(null, tuningStr);
    }

    /**
     * Creates a Tuning object from a java string containing the root notes of each guitar string
     * in the tuning.
     * @param name Name of the tuning. Can be null.
     * @param tuningStr A java string containing the root notes of each guitar string
     *                  in the tuning, separated by spaces. This is the same format as
     *                  returned by {@code Tuning.toFullString()}.;
     * @return The corresponding guitar tuning.
     * @throws IllegalArgumentException If any of the root notes do not correspond to a defined standard string.
     */
    public static Tuning fromString(String name, String tuningStr) {
        // Construct new tuning otherwise.
        String[] rootNotes = tuningStr.split(" ");
        GuitarString[] strings = new GuitarString[rootNotes.length];
        for (int i = 0; i < rootNotes.length; i++) {
            strings[i] = GuitarString.fromRootNote(rootNotes[i]);
        }

        return new Tuning(name, strings);
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
        if (!(obj instanceof Tuning)) return false;

        Tuning o = (Tuning)obj;
        return Objects.equals(name, o.name)
            && strings.equals(o.strings);
    }

    /**
     * Returns whether this tuning is equivalent to the specified tuning.
     * @param other The tuning to check equivalence with.
     * @return True if the other tuning has the same strings as this tuning, false otherwise.
     */
    public boolean equivalentTo(Tuning other) {
        if (other == null) return false;
        return other == this || strings.equals(other.strings);
    }

    /**
     * Returns whether the specified collection contains an equivalent tuning to this tuning.
     * @param tunings The collection of tunings to search.
     * @return True if the collection contains an equivalent tuning, false otherwise.
     * @throws NullPointerException If the collection of tunings is null.
     *
     * @see #equivalentTo(Tuning)
     */
    public boolean hasEquivalentIn(Collection<Tuning> tunings) {
        Objects.requireNonNull(tunings);
        return tunings.stream().anyMatch(this::equivalentTo);
    }

    /**
     * Searches for an equivalent tuning in the specified collection.
     * @param tunings The collection of tunings to search.
     * @return The equivalent tuning in the collection, or null if one is not found.
     * @throws NullPointerException If the collection of tunings is null.
     */
    public Tuning findEquivalentIn(Collection<Tuning> tunings) {
        Objects.requireNonNull(tunings);
        return tunings.stream()
            .filter(this::equivalentTo)
            .findAny().orElse(null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, strings);
    }


    // STANDARD TUNINGS

    /**
     * Standard Tuning (EADGBE)
     */
    public static final Tuning STANDARD = new Tuning(
        "Standard",
        GuitarString.E4,
        GuitarString.B3,
        GuitarString.G3,
        GuitarString.D3,
        GuitarString.A2,
        GuitarString.E2
    );

    /** Drop-D Tuning (DADGBE) */
    public static final Tuning DROP_D = new Tuning(
        "Drop D",
        GuitarString.E4,
        GuitarString.B3,
        GuitarString.G3,
        GuitarString.D3,
        GuitarString.A2,
        GuitarString.D2
    );
}