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

package com.rohankhayech.music;

import org.billthefarmer.mididriver.GeneralMidiConstants;

/**
 * Enum representing an stringed instrument.
 *
 * @author Rohan Khayech
 */
public enum Instrument {

    /** Guitar */
    GUITAR("Guitar"),

    /** Bass */
    BASS("Bass", 4, GeneralMidiConstants.ELECTRIC_BASS_FINGER),

    /** Ukulele */
    UKULELE("Ukulele", 4, GeneralMidiConstants.ACOUSTIC_GUITAR_NYLON),

    /** Other Instrument */
    OTHER("Other");

    /** Name of the instrument. */
    private final String name;

    /** Default number of strings the instrument has. */
    private final int defaultNumStrings;

    /** Default value for the default number of strings for an instrument. */
    private static final int DEFAULT_NUM_STRINGS = 6;

    /** The MIDI instrument code the instrument should use when playing a note. */
    private final byte midiInstrument;

    /** Default MIDI instrument code to use when playing a note. */
    private static final byte DEFAULT_MIDI_INSTRUMENT = GeneralMidiConstants.ELECTRIC_GUITAR_CLEAN;

    /**
     * Defines an instrument with the specified name and default values.
     * @param name The name of the instrument.
     */
    Instrument(String name) {
        this.name = name;
        this.defaultNumStrings = DEFAULT_NUM_STRINGS;
        this.midiInstrument = DEFAULT_MIDI_INSTRUMENT;
    }

    /**
     * Defines an instrument with the specified values.
     * @param name The name of the instrument.
     * @param defaultNumStrings Default number of strings the instrument has.
     * @param midiInstrument The MIDI instrument code the instrument should use when playing a note.
     */
    Instrument(String name, int defaultNumStrings, byte midiInstrument) {
        this.name = name;
        this.defaultNumStrings = defaultNumStrings;
        this.midiInstrument = midiInstrument;
    }

    /** @return The name of the instrument. */
    public String getName() {
        return name;
    }

    /** @return The default number of strings the instrument has. */
    public int getDefaultNumStrings() {
        return defaultNumStrings;
    }

    /** @return The MIDI instrument code the instrument should use when playing a note. */
    public byte getMidiInstrument() {
        return midiInstrument;
    }
}
