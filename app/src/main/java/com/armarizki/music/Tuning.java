
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Immutable
public final class Tuning implements Iterable<GuitarString> {

    private final String name;

    private final List<GuitarString> strings;

    private final Instrument instrument;

    public static final Instrument DEFAULT_INSTRUMENT = Instrument.GUITAR;

    private final Category category;

    public int numStrings() {
        return strings.size();
    }

    public Tuning(GuitarString... strings) {
        this(DEFAULT_INSTRUMENT, strings);
    }

    public Tuning(Instrument instrument, GuitarString... strings) {
        this(null, instrument, null, strings);
    }

    public Tuning(String name, Instrument instrument, Category category, GuitarString... strings) {
        this(name, instrument, category, Arrays.asList(
            Objects.requireNonNull(strings, "Array of strings cannot be null.")
        ));
    }

    public Tuning(Instrument instrument, List<GuitarString> strings) {
        this(null, instrument, null, strings);
    }

    public Tuning(String name, Tuning o) {
        Objects.requireNonNull(o);
        this.name = name;
        this.strings = o.strings;
        this.instrument = o.instrument;
        this.category = o.category;
    }

    public Tuning(String name, Instrument instrument, Category category, List<GuitarString> strings) {
        // Check list of strings is not null.
        Objects.requireNonNull(strings, "List of strings cannot be null.");
        Objects.requireNonNull(instrument, "Instrument cannot be null.");

        // Check that no strings are null.
        if (strings.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("Strings cannot be null.");
        }

        // Initialise tuning.
        this.strings = new ArrayList<>(strings);
        this.name = name;
        this.instrument = instrument;
        this.category = category;
    }

    public GuitarString getString(int n) {
        return strings.get(n);
    }

    public int getStringNum(GuitarString string) {
        int stringNum = strings.indexOf(string);
        if (stringNum != -1) {
            return stringNum;
        } else {
            throw new NoSuchElementException("The specified string"+string.toFullString()+"is not present in this tuning.");
        }
    }

    public List<GuitarString> getStrings() {
        return Collections.unmodifiableList(strings);
    }

    public List<GuitarString> getStringsContaining(double pitch) {
        return strings.stream().filter(s->s.containsNote(pitch)).collect(Collectors.toList());
    }

    public boolean hasName() {
        return name != null;
    }

    public String getName() {
        return name != null ? name : toString();
    }

    public String getFullName() {
        return name != null ? name + " (" + this + ")" : toString();
    }


    public Instrument getInstrument() {
        return instrument;
    }

     
    public boolean hasCategory() {
        return category != null;
    }

     
    public Category getCategory() { return category; }

     
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = strings.size()-1; i>=0; i--) {
            str.append(strings.get(i).toString());
        }
        return str.toString();
    }

     
    public String toFullString() {
        StringBuilder str = new StringBuilder();
        for (GuitarString s : strings) {
            str.append(s.toFullString()).append(" ");
        }
        return str.toString().trim();
    }

     
    public Tuning lowerTuning() {
        return new Tuning(
            instrument,
            strings.stream()
                .map(GuitarString::lowerString)
                .collect(Collectors.toList())
        );
    }

     
    public Tuning higherTuning() {
        return new Tuning(
            instrument,
            strings.stream()
                .map(GuitarString::higherString)
                .collect(Collectors.toList())
        );
    }

     
    public Tuning withString(int n, GuitarString string) {
        Objects.requireNonNull(string);
        List<GuitarString> newList = new ArrayList<>(strings);
        newList.set(n, string);
        return new Tuning(null, instrument, null, newList);
    }

     
    public static Tuning fromString(String tuningStr) {
        return fromString(null, DEFAULT_INSTRUMENT, null, tuningStr);
    }

     
    public static Tuning fromString(String name, Instrument instrument, Category category, String tuningStr) {
        // Construct new tuning otherwise.
        String[] rootNotes = tuningStr.split(" ");
        GuitarString[] strings = new GuitarString[rootNotes.length];
        for (int i = 0; i < rootNotes.length; i++) {
            strings[i] = GuitarString.fromRootNote(rootNotes[i]);
        }

        return new Tuning(name, instrument, category, strings);
    }

     
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
            && strings.equals(o.strings)
            && instrument == o.instrument
            && category == o.category;
    }

     
    public boolean equivalentTo(Tuning other) {
        if (other == null) return false;
        return other == this || (strings.equals(other.strings) && instrument == other.instrument);
    }

     
    public boolean hasEquivalentIn(Collection<Tuning> tunings) {
        Objects.requireNonNull(tunings);
        return tunings.stream().anyMatch(this::equivalentTo);
    }

     
    public Tuning findEquivalentIn(Collection<Tuning> tunings) {
        Objects.requireNonNull(tunings);
        return tunings.stream()
            .filter(this::equivalentTo)
            .findAny().orElse(null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, strings, instrument, category);
    }

     
    public enum Category {
         
        COMMON,
         
        POWER,
         
        OPEN,
         
        MISC
    }


    // STANDARD TUNINGS

     
    public static final Tuning STANDARD = new Tuning(
        "Standard",
        Instrument.GUITAR,
        Category.COMMON,
        GuitarString.E4,
        GuitarString.B3,
        GuitarString.G3,
        GuitarString.D3,
        GuitarString.A2,
        GuitarString.E2
    );

     
    public static final Tuning DROP_D = new Tuning(
        "Drop D",
        Instrument.GUITAR,
        Category.COMMON,
        GuitarString.E4,
        GuitarString.B3,
        GuitarString.G3,
        GuitarString.D3,
        GuitarString.A2,
        GuitarString.D2
    );
}