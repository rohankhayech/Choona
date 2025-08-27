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
package com.rohankhayech.choona.controller.fileio

import com.rohankhayech.choona.model.tuning.TuningEntry
import com.rohankhayech.music.Tuning
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test

class TuningFileIOTest {

    companion object {
        private const val TUNINGS_JSON = "{\"tunings\":[{\"strings\":\"E4 B3 G3 D3 A2 E2\",\"name\":\"Standard\",\"instrument\":\"GUITAR\",\"category\":\"COMMON\"},{\"strings\":\"E4 B3 G3 D3 A2 D2\",\"name\":\"Drop D\",\"instrument\":\"GUITAR\",\"category\":\"COMMON\"},{\"strings\":\"G3 D3 A2 E2\",\"instrument\":\"GUITAR\"},{\"name\":\"Chromatic\",\"instrument\":\"chromatic\"}]}"
    }

    @Test
    fun testParseTunings() {
        val tunings = TuningFileIO.parseTunings(TUNINGS_JSON)
        val expected: MutableSet<TuningEntry> = LinkedHashSet()
        expected.add(TuningEntry.InstrumentTuning(Tuning.STANDARD))
        expected.add(TuningEntry.InstrumentTuning(Tuning.DROP_D))
        expected.add(TuningEntry.InstrumentTuning(Tuning.fromString("G3 D3 A2 E2")))
        expected.add(TuningEntry.ChromaticTuning)
        Assert.assertEquals(expected, tunings)
    }

    @Test
    @Throws(JSONException::class)
    fun testEncodeTunings() {
        val tunings: MutableSet<TuningEntry> = LinkedHashSet()
        tunings.add(TuningEntry.InstrumentTuning(Tuning.STANDARD))
        tunings.add(TuningEntry.InstrumentTuning(Tuning.DROP_D))
        tunings.add(TuningEntry.InstrumentTuning(Tuning.fromString("G3 D3 A2 E2")))
        tunings.add(TuningEntry.ChromaticTuning)
        val json = TuningFileIO.encodeTunings(tunings)
        Assert.assertEquals(JSONObject(TUNINGS_JSON).toString(), JSONObject(json).toString())
    }
}