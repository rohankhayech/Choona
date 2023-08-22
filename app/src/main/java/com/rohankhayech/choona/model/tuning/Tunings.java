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

package com.rohankhayech.choona.model.tuning;


import com.rohankhayech.music.Instrument;
import com.rohankhayech.music.Tuning;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Static class defining common guitar tunings.
 * 
 * @author Rohan Khayech
 */
public final class Tunings {

    /** Internal list of common guitar tunings. */
    private static final Set<Tuning> tunings = new LinkedHashSet<>();

    /** A list of common guitar tunings. */
    public static final Set<Tuning> COMMON = Collections.unmodifiableSet(tunings);

    public static final Tuning STANDARD = add(Tuning.STANDARD);
    public static final Tuning HALF_STEP_DOWN = add(Tuning.fromString("Half Step Down", Instrument.GUITAR, "D#4 A#3 F#3 C#3 G#2 D#2"));
    public static final Tuning WHOLE_STEP_DOWN = add(Tuning.fromString("Whole Step Down", Instrument.GUITAR, "D4 A3 F3 C3 G2 D2"));
    public static final Tuning DROP_D = add(Tuning.DROP_D);
    public static final Tuning DOUBLE_DROP_D = add(Tuning.fromString("Double Drop D", Instrument.GUITAR, "D4 B3 G3 D3 A2 D2"));
    public static final Tuning BASS_STANDARD = add(Tuning.fromString("Standard", Instrument.BASS, "G2 D2 A1 E1"));
    public static final Tuning BASS_DROP_D = add(Tuning.fromString("Drop D", Instrument.BASS, "G2 D2 A1 D1"));
    public static final Tuning BASS_E_FLAT = add(Tuning.fromString("E Flat", Instrument.BASS, "F#2 C#2 G#1 D#1"));
    public static final Tuning D_MODAL = add(Tuning.fromString("D Modal", Instrument.GUITAR, "D4 A3 G3 D3 A2 D2"));
    public static final Tuning DOUBLE_DADDY = add(Tuning.fromString("Double Daddy", Instrument.GUITAR, "D4 A3 D3 D3 A2 D2"));
    public static final Tuning DROP_CS = add(Tuning.fromString("Drop C#", Instrument.GUITAR, "D#4 A#3 F#3 C#3 G#2 C#2"));
    public static final Tuning DROP_C = add(Tuning.fromString("Drop C", Instrument.GUITAR, "D4 A3 F3 C3 G2 C2"));
    public static final Tuning DROP_B = add(Tuning.fromString("Drop B", Instrument.GUITAR, "C#4 G#3 E3 B2 F#2 B1"));
    public static final Tuning DROP_A = add(Tuning.fromString("Drop A", Instrument.GUITAR, "B3 F#3 D3 A2 E2 A1"));
    public static final Tuning OPEN_C = add(Tuning.fromString("Open C", Instrument.GUITAR, "E4 C4 G3 C3 G2 C2"));
    public static final Tuning OPEN_E = add(Tuning.fromString("Open E", Instrument.GUITAR, "E4 B3 G#3 E3 B2 E2"));
    public static final Tuning OPEN_F = add(Tuning.fromString("Open F", Instrument.GUITAR, "F4 A3 F3 C3 F2 C2"));
    public static final Tuning OPEN_G = add(Tuning.fromString("Open G", Instrument.GUITAR, "D4 B3 G3 D3 G2 D2"));
    public static final Tuning OPEN_A = add(Tuning.fromString("Open A", Instrument.GUITAR, "E4 C#4 A3 E3 A2 E2"));
    public static final Tuning OPEN_AM = add(Tuning.fromString("Open Am", Instrument.GUITAR, "E4 C4 A3 E3 A2 E2"));
    public static final Tuning OPEN_EM = add(Tuning.fromString("Open Em", Instrument.GUITAR, "E4 B3 G3 E3 B2 E2"));
    public static final Tuning OPEN_D = add(Tuning.fromString("Open D", Instrument.GUITAR, "D4 A3 F#3 D3 A2 D2"));
    public static final Tuning OPEN_DM = add(Tuning.fromString("Open Dm", Instrument.GUITAR, "D4 A3 F3 D3 A2 D2"));
    public static final Tuning G_MODAL = add(Tuning.fromString("G Modal", Instrument.GUITAR, "D4 C4 G3 D3 G2 D2"));
    public static final Tuning ALL_4TH = add(Tuning.fromString("All 4th", Instrument.GUITAR, "F4 C4 G3 D3 A2 E2"));
    public static final Tuning NST = add(Tuning.fromString("New Standard Tuning", Instrument.GUITAR, "G4 E4 A3 D3 G2 C2"));

    /** Adds the specified tuning to the list and returns it. */
    private static Tuning add(Tuning tuning) {
        tunings.add(tuning);
        return tuning;
    }
}