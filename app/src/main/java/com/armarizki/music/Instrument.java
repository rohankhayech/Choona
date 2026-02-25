
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

import org.billthefarmer.mididriver.GeneralMidiConstants;

public enum Instrument {

    GUITAR("Guitar"),

    BASS("Bass", 4, GeneralMidiConstants.ELECTRIC_BASS_FINGER);

    private final String name;

    private final int defaultNumStrings;

    private static final int DEFAULT_NUM_STRINGS = 6;

    private final byte midiInstrument;

    private static final byte DEFAULT_MIDI_INSTRUMENT = GeneralMidiConstants.ELECTRIC_GUITAR_CLEAN;

    Instrument(String name) {
        this.name = name;
        this.defaultNumStrings = DEFAULT_NUM_STRINGS;
        this.midiInstrument = DEFAULT_MIDI_INSTRUMENT;
    }

    Instrument(String name, int defaultNumStrings, byte midiInstrument) {
        this.name = name;
        this.defaultNumStrings = defaultNumStrings;
        this.midiInstrument = midiInstrument;
    }

    public String getName() {
        return name;
    }

    public int getDefaultNumStrings() {
        return defaultNumStrings;
    }

    public byte getMidiInstrument() {
        return midiInstrument;
    }
}
