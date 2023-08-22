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

package com.rohankhayech.choona.controller.fileio;

import static org.junit.Assert.assertEquals;

import com.rohankhayech.music.Tuning;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class TuningFileIOTest {

    private static final String TUNINGS_JSON = "{\"tunings\":[{\"strings\":\"E4 B3 G3 D3 A2 E2\",\"name\":\"Standard\",\"instrument\":\"GUITAR\"},{\"strings\":\"E4 B3 G3 D3 A2 D2\",\"name\":\"Drop D\",\"instrument\":\"GUITAR\"},{\"strings\":\"G3 D3 A2 E2\",\"instrument\":\"GUITAR\"}]}";

    @Test
    public void testParseTunings() {
        Set<Tuning> tunings = TuningFileIO.parseTunings(TUNINGS_JSON);
        Set<Tuning> expected = new HashSet<>();
        expected.add(Tuning.STANDARD);
        expected.add(Tuning.DROP_D);
        expected.add(Tuning.fromString("G3 D3 A2 E2"));
        assertEquals(expected, tunings);
    }

    @Test
    public void testEncodeTunings() throws JSONException {
        Set<Tuning> tunings = new HashSet<>();
        tunings.add(Tuning.STANDARD);
        tunings.add(Tuning.DROP_D);
        tunings.add(Tuning.fromString("G3 D3 A2 E2"));
        String json = TuningFileIO.encodeTunings(tunings);
        assertEquals(new JSONObject(TUNINGS_JSON).toString(), new JSONObject(json).toString());
    }
}