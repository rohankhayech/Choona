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


import static com.rohankhayech.music.Instrument.BASS;
import static com.rohankhayech.music.Instrument.GUITAR;
import static com.rohankhayech.music.Instrument.UKULELE;
import static com.rohankhayech.music.Tuning.Category.MISC;
import static com.rohankhayech.music.Tuning.Category.OPEN;
import static com.rohankhayech.music.Tuning.Category.POWER;

import com.rohankhayech.music.Tuning;
import com.rohankhayech.music.Tuning.Category;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Static class defining common guitar tunings.
 * 
 * @author Rohan Khayech
 *
 * @noinspection unused
 */
public final class Tunings {

    /** Internal list of common guitar tunings. */
    private static final Set<Tuning> tunings = new LinkedHashSet<>();

    /** A list of common guitar tunings. */
    public static final Set<Tuning> COMMON = Collections.unmodifiableSet(tunings);

    public static final Tuning STANDARD = add(Tuning.STANDARD);
    public static final Tuning HALF_STEP_DOWN = add(Tuning.fromString("Half Step Down", GUITAR, Category.COMMON, "D#4 A#3 F#3 C#3 G#2 D#2"));
    public static final Tuning WHOLE_STEP_DOWN = add(Tuning.fromString("Whole Step Down", GUITAR, Category.COMMON, "D4 A3 F3 C3 G2 D2"));
    public static final Tuning DROP_D = add(Tuning.DROP_D);
    public static final Tuning DOUBLE_DROP_D = add(Tuning.fromString("Double Drop D", GUITAR, POWER, "D4 B3 G3 D3 A2 D2"));
    public static final Tuning BASS_STANDARD = add(Tuning.fromString("Standard", BASS, Category.COMMON, "G2 D2 A1 E1"));
    public static final Tuning BASS_DROP_D = add(Tuning.fromString("Drop D", BASS, Category.COMMON, "G2 D2 A1 D1"));
    public static final Tuning BASS_E_FLAT = add(Tuning.fromString("E Flat", BASS, Category.COMMON, "F#2 C#2 G#1 D#1"));
    public static final Tuning D_MODAL = add(Tuning.fromString("D Modal", GUITAR, POWER, "D4 A3 G3 D3 A2 D2"));
    public static final Tuning DOUBLE_DADDY = add(Tuning.fromString("Double Daddy", GUITAR, POWER, "D4 A3 D3 D3 A2 D2"));
    public static final Tuning DROP_CS = add(Tuning.fromString("Drop C#", GUITAR, POWER, "D#4 A#3 F#3 C#3 G#2 C#2"));
    public static final Tuning DROP_C = add(Tuning.fromString("Drop C", GUITAR, POWER, "D4 A3 F3 C3 G2 C2"));
    public static final Tuning DROP_B = add(Tuning.fromString("Drop B", GUITAR, POWER, "C#4 G#3 E3 B2 F#2 B1"));
    public static final Tuning DROP_A = add(Tuning.fromString("Drop A", GUITAR, POWER, "B3 F#3 D3 A2 E2 A1"));
    public static final Tuning OPEN_C = add(Tuning.fromString("Open C", GUITAR, OPEN, "E4 C4 G3 C3 G2 C2"));
    public static final Tuning OPEN_E = add(Tuning.fromString("Open E", GUITAR, OPEN, "E4 B3 G#3 E3 B2 E2"));
    public static final Tuning OPEN_F = add(Tuning.fromString("Open F", GUITAR, OPEN, "F4 A3 F3 C3 F2 C2"));
    public static final Tuning OPEN_G = add(Tuning.fromString("Open G", GUITAR, OPEN, "D4 B3 G3 D3 G2 D2"));
    public static final Tuning OPEN_A = add(Tuning.fromString("Open A", GUITAR, OPEN, "E4 C#4 A3 E3 A2 E2"));
    public static final Tuning OPEN_AM = add(Tuning.fromString("Open Am", GUITAR, OPEN, "E4 C4 A3 E3 A2 E2"));
    public static final Tuning OPEN_EM = add(Tuning.fromString("Open Em", GUITAR, OPEN, "E4 B3 G3 E3 B2 E2"));
    public static final Tuning OPEN_D = add(Tuning.fromString("Open D", GUITAR, OPEN, "D4 A3 F#3 D3 A2 D2"));
    public static final Tuning OPEN_DM = add(Tuning.fromString("Open Dm", GUITAR, OPEN, "D4 A3 F3 D3 A2 D2"));
    public static final Tuning G_MODAL = add(Tuning.fromString("G Modal", GUITAR, MISC, "D4 C4 G3 D3 G2 D2"));
    public static final Tuning ALL_4TH = add(Tuning.fromString("All 4th", GUITAR, MISC, "F4 C4 G3 D3 A2 E2"));
    public static final Tuning NST = add(Tuning.fromString("New Standard Tuning", GUITAR, MISC, "G4 E4 A3 D3 G2 C2"));
    public static final Tuning UKULELE_STANDARD = add(Tuning.fromString("Standard", UKULELE, Category.COMMON, "A4 E4 C4 G4"));

    /** Adds the specified tuning to the list and returns it. */
    private static Tuning add(Tuning tuning) {
        tunings.add(tuning);
        return tuning;
    }
}