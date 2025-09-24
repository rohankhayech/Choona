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

import static org.junit.Assert.assertEquals;

import org.billthefarmer.mididriver.GeneralMidiConstants;
import org.junit.Test;

public class InstrumentTest {

    private final Instrument instrument = Instrument.BASS;

    @Test
    public void testGetName() {
        assertEquals("Bass", instrument.getName());
    }

    @Test
    public void testGetDefaultNumStrings() {
        assertEquals(4, instrument.getDefaultNumStrings());
    }

    @Test
    public void getMidiInstrument() {
        assertEquals(GeneralMidiConstants.ELECTRIC_BASS_FINGER, instrument.getMidiInstrument());
    }
}