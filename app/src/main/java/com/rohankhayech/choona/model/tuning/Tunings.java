/*
 * Copyright (c) 2023 Rohan Khayech
 */

package com.rohankhayech.choona.model.tuning;


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
    public static final Tuning HALF_STEP_DOWN = add(Tuning.fromString("Half Step Down", "D#4 A#3 F#3 C#3 G#2 D#2"));
    public static final Tuning WHOLE_STEP_DOWN = add(Tuning.fromString("Whole Step Down", "D4 A3 F3 C3 G2 D2"));
    public static final Tuning DROP_D = add(Tuning.DROP_D);
    public static final Tuning DOUBLE_DROP_D = add(Tuning.fromString("Double Drop-D", "D4 B3 G3 D3 A2 D2"));
    public static final Tuning D_MODAL = add(Tuning.fromString("D Modal", "D4 A3 G3 D3 A2 D2"));
    public static final Tuning DOUBLE_DADDY = add(Tuning.fromString("Double Daddy", "D4 A3 D3 D3 A2 D2"));
    public static final Tuning DROP_CS = add(Tuning.fromString("Drop C#", "D#4 A#3 F#3 C#3 G#2 C#2"));
    public static final Tuning DROP_C = add(Tuning.fromString("Drop C", "D4 A3 F3 C3 G2 C2"));
    public static final Tuning DROP_B = add(Tuning.fromString("Drop B", "C#4 G#3 E3 B2 F#2 B1"));
    public static final Tuning DROP_A = add(Tuning.fromString("Drop A", "B3 F#3 D3 A2 E2 A1"));
    public static final Tuning OPEN_C = add(Tuning.fromString("Open C", "E4 C4 G3 C3 G2 C2"));
    public static final Tuning OPEN_E = add(Tuning.fromString("Open E", "E4 B3 G#3 E3 B2 E2"));
    public static final Tuning OPEN_F = add(Tuning.fromString("Open F", "F4 A3 F3 C3 F2 C2"));
    public static final Tuning OPEN_G = add(Tuning.fromString("Open G", "D4 B3 G3 D3 G2 D2"));
    public static final Tuning OPEN_A = add(Tuning.fromString("Open A", "E4 C#4 A3 E3 A2 E2"));
    public static final Tuning OPEN_AM = add(Tuning.fromString("Open Am", "E4 C4 A3 E3 A2 E2"));
    public static final Tuning OPEN_EM = add(Tuning.fromString("Open Em", "E4 B3 G3 E3 B2 E2"));
    public static final Tuning OPEN_D = add(Tuning.fromString("Open D", "D4 A3 F#3 D3 A2 D2"));
    public static final Tuning OPEN_DM = add(Tuning.fromString("Open Dm", "D4 A3 F3 D3 A2 D2"));
    public static final Tuning G_MODAL = add(Tuning.fromString("G Modal", "D4 C4 G3 D3 G2 D2"));
    public static final Tuning ALL_4TH = add(Tuning.fromString("All 4th", "F4 C4 G3 D3 A2 E2"));
    public static final Tuning NST = add(Tuning.fromString("New Standard Tuning", "G4 E4 A3 D3 G2 C2"));

    /** Adds the specified tuning to the list and returns it. */
    private static Tuning add(Tuning tuning) {
        tunings.add(tuning);
        return tuning;
    }
}